package pr0x79;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.InvocationTargetException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.WeakHashMap;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;

import pr0x79.instrumentation.BytecodeInstrumentation;
import pr0x79.instrumentation.accessor.Accessors;
import pr0x79.instrumentation.accessor.IAccessor;
import pr0x79.instrumentation.accessor.Interceptors;
import pr0x79.instrumentation.accessor.MethodInterceptorData;
import pr0x79.instrumentation.exception.InstrumentorException;
import pr0x79.instrumentation.identification.Identifiers;

public class Bootstrapper {
	private static final Bootstrapper INSTANCE = new Bootstrapper();

	private final Identifiers identifiers;
	private final Accessors accessors;
	private final BytecodeInstrumentation instrumentor;
	private final Interceptors interceptors;

	//Contains all accessor classes that were loaded through the class transformer
	private final Map<ClassLoader, Set<String>> internallyLoadedAccessorClasses = new WeakHashMap<ClassLoader, Set<String>>();

	//Contains all instrumentor classes that were loaded through the class transformer
	private final Map<ClassLoader, Set<String>> internallyLoadedInstrumentorClasses = new WeakHashMap<ClassLoader, Set<String>>();

	private Set<IInstrumentor> instrumentors;
	private boolean initializing = true;

	private Bootstrapper() {
		this.identifiers = new Identifiers(this);
		Map<String, MethodInterceptorData> interceptorMap = new HashMap<>();
		this.instrumentor = new BytecodeInstrumentation(this.identifiers, interceptorMap);
		this.interceptors = new Interceptors(interceptorMap);
		this.accessors = new Accessors(this, this.identifiers, this.instrumentor);
		this.instrumentor.setAccessors(this.accessors);
	}

	/**
	 * Initializes the bootstrapper, called from the agent
	 * @param args The instrumentor class names
	 * @param inst The bytecode instrumentation
	 */
	public static void initialize(String[] instrumentorClasses, Instrumentation inst) {
		if(!INSTANCE.isInitializing()) {
			throw new RuntimeException("Bootstrapper can only be initialized once");
		}
		INSTANCE.init(instrumentorClasses, inst);
	}

	/**
	 * Adds the specified {@link IAccessor} or {@link IInstrumentor} to the internally loaded class list
	 * @param map
	 * @param loader
	 * @param className
	 */
	private void addInternallyLoadedClass(Map<ClassLoader, Set<String>> map, ClassLoader loader, String className) {
		synchronized(map) {
			Set<String> classes = map.get(loader);
			if (classes == null) {
				map.put(loader, classes = new HashSet<String>());
			}
			classes.add(className);
		}
	}

	/**
	 * Returns whether an {@link IAccessor} or {@link IInstrumentor} was loaded through the internal class transformer
	 * @param map
	 * @param loader
	 * @param className
	 * @return
	 */
	private boolean wasClassLoadedInternally(Map<ClassLoader, Set<String>> map, ClassLoader loader, String className) {
		while(loader != null) {
			synchronized(map) {
				Set<String> classes = map.get(loader);
				if (classes != null && classes.contains(className)) {
					return true;
				}
			}
			loader = loader.getParent();
		}
		return false;
	}

	/**
	 * Initializes the bootstrapper
	 * @param instrumentorClasses The instrumentor class names
	 * @param inst The bytecode instrumentation
	 */
	private void init(String[] instrumentorClasses, Instrumentation inst) {
		//All exceptions before the IInstrumentors have been registered go into this list and are later redirected to the IInstrumentors after initialization
		List<Exception> bootstrapperInitExceptions = Collections.synchronizedList(new ArrayList<>());

		inst.addTransformer(new ClassFileTransformer() {
			@Override
			public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDoman, byte[] bytes) throws IllegalClassFormatException {
				ClassReader classReader = new ClassReader(bytes);
				ClassNode clsNode = new ClassNode();
				classReader.accept(clsNode, ClassReader.SKIP_FRAMES);

				if(clsNode.interfaces.contains(Type.getInternalName(IInstrumentor.class))) {
					addInternallyLoadedClass(internallyLoadedInstrumentorClasses, loader, className.replace("/", "."));
				}

				if(clsNode.interfaces.contains(Type.getInternalName(IAccessor.class))) {
					addInternallyLoadedClass(internallyLoadedAccessorClasses, loader, className.replace("/", "."));

					try {
						if(instrumentor.instrumentAccessorClass(clsNode, Bootstrapper.this)) {
							ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
							clsNode.accept(classWriter);
							return classWriter.toByteArray();
						}
					} catch(Exception ex) {
						if(!isInitializing()) {
							onBootstrapperException(ex);
						} else {
							bootstrapperInitExceptions.add(ex);
						}
					}
				}

				return bytes;
			}
		});

		List<IInstrumentor> instrumentorInstances = new ArrayList<>();
		for(String instrumentorClass : instrumentorClasses) {
			IInstrumentor instrumentor = null;
			try {
				instrumentor = this.initInstrumentor(instrumentorClass);

				//Makes sure that the instrumentor class was not already loaded
				if(!this.wasClassLoadedInternally(this.internallyLoadedInstrumentorClasses, Bootstrapper.class.getClassLoader(), instrumentorClass)) {
					bootstrapperInitExceptions.add(new InstrumentorException(String.format("Instrumentor class %s was already loaded before the bootstrapper initialization!", instrumentorClass)));
				}
			} catch(ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException ex) {
				bootstrapperInitExceptions.add(ex);
			}
			if(instrumentor != null) {
				instrumentorInstances.add(instrumentor);
			}
		}

		this.instrumentors = new TreeSet<IInstrumentor>((c1, c2) -> c1.getClass().getName().compareTo(c2.getClass().getName()));
		this.instrumentors.addAll(instrumentorInstances);

		for(IInstrumentor instrumentor : this.instrumentors) {
			try {
				instrumentor.initBootstrapper(this);
			} catch(Exception ex) {
				bootstrapperInitExceptions.add(ex);
			}
		}

		for(MethodInterceptorData interceptor : this.interceptors.getMethodInterceptors()) {
			try {
				interceptor.initIdentifiers(identifiers);
			} catch(Exception ex) {
				bootstrapperInitExceptions.add(ex);
			}
		}

		synchronized(this) {
			this.initializing = false;
		}

		synchronized(bootstrapperInitExceptions) {
			for(Exception ex : bootstrapperInitExceptions) {
				this.onBootstrapperException(ex);
			}
		}

		inst.addTransformer(new ClassFileTransformer() {
			@Override
			public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDoman, byte[] bytes) throws IllegalClassFormatException {
				try {
					if(className != null && instrumentor.acceptsClass(className)) {
						ClassReader classReader = new ClassReader(bytes);
						ClassNode clsNode = new ClassNode();
						classReader.accept(clsNode, ClassReader.SKIP_FRAMES);

						instrumentor.instrumentClass(clsNode);

						ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
						clsNode.accept(classWriter);
						return classWriter.toByteArray();
					}
				} catch(Exception ex) {
					onBootstrapperException(ex);
				}
				return bytes;
			}
		});

		for(IInstrumentor instrumentor : this.instrumentors) {
			try {
				instrumentor.onInstrumentorRegistered(instrumentor);
			} catch(Exception ex) {
				this.onBootstrapperException(ex);
			}
		}

		for(IInstrumentor instrumentor : this.instrumentors) {
			try {
				instrumentor.postInitBootstrapper(this);
			} catch(Exception ex) {
				this.onBootstrapperException(ex);
			}
		}
	}

	/**
	 * Creates a new instance of
	 * the specified instrumentor class
	 * @param instrumentorClass
	 * @return
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	private IInstrumentor initInstrumentor(String instrumentorClass) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		@SuppressWarnings("unchecked")
		Class<IInstrumentor> instrumentorCls = (Class<IInstrumentor>) Bootstrapper.class.getClassLoader().loadClass(instrumentorClass);
		return instrumentorCls.getDeclaredConstructor().newInstance();
	}

	/**
	 * Returns the identifier registry
	 * @return
	 */
	public Identifiers getIdentifiers() {
		return this.identifiers;
	}

	/**
	 * Returns the accessor registry
	 * @return
	 */
	public Accessors getAccessors() {
		return this.accessors;
	}

	/**
	 * Returns the method interceptors
	 * @return
	 */
	public Interceptors getInterceptors() {
		return this.interceptors;
	}

	/**
	 * Returns whether the bootstrapper is in the initialization phase
	 * @return
	 */
	public synchronized boolean isInitializing() {
		return this.initializing;
	}

	/**
	 * Returns whether the specified accessor class was correctly loaded
	 * through the internal class transformer
	 * @param cls
	 * @return
	 */
	public boolean isAccessorClassValid(Class<? extends IAccessor> cls) {
		return this.wasClassLoadedInternally(this.internallyLoadedAccessorClasses, Bootstrapper.class.getClassLoader(), cls.getName());
	}

	/**
	 * Called when an exception occurs caused by the bootstrapper
	 * @param ex
	 */
	protected void onBootstrapperException(Exception ex) {
		for(IInstrumentor instrumentor : this.instrumentors) {
			instrumentor.onBootstrapperException(ex);
		}
	}
}
