package tcb.pr0x79;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import tcb.pr0x79.accessor.Accessor;
import tcb.pr0x79.accessor.ClassAccessor;
import tcb.pr0x79.data.ClassAccessorData;
import tcb.pr0x79.exception.InstrumentorException;
import tcb.pr0x79.mapping.MapperRegistry;
import tcb.pr0x79.mapping.locator.ClassLocatorRegistry;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public enum Bootstrapper {
	INSTANCE;

	private final MapperRegistry mapperRegistry;
	private final Accessors accessors;
	private final ClassLocatorRegistry classLocatorRegistry;
	private final BytecodeInstrumentation instrumentor;

	//Contains all accessor classes that were loaded through the class transformer
	private final ClassHierarchy hierarchy;

	private Set<Instrumentor> instrumentors;
	private boolean initializing = true;

	Bootstrapper() {
		this.mapperRegistry = new MapperRegistry(this);
		this.classLocatorRegistry = new ClassLocatorRegistry(this);
		this.hierarchy = new ClassHierarchy(this.classLocatorRegistry);
		this.instrumentor = new BytecodeInstrumentation(this.hierarchy, this.classLocatorRegistry);
		this.accessors = new Accessors(this, this.mapperRegistry, this.instrumentor);
		this.instrumentor.setAccessors(this.accessors);
	}

	/**
	 * Initializes the bootstrapper, called from the agent
	 *
	 * @param inst                The bytecode instrumentation
	 * @param instrumentorClasses The instrumentor classes
	 */
	public static void initialize(Instrumentation inst, Class... instrumentorClasses) {
		if (!INSTANCE.isInitializing()) {
			throw new RuntimeException("Bootstrapper can only be initialized once");
		}

		String[] intrumentorClsNames = new String[instrumentorClasses.length];
		for (int i = 0, instrumentorClassesLength = instrumentorClasses.length; i < instrumentorClassesLength; i++) {
			Class<?> cls = instrumentorClasses[i];
			intrumentorClsNames[i] = cls.getName();
		}

		INSTANCE.init(intrumentorClsNames, inst);
	}

	/**
	 * Initializes the bootstrapper, called from the agent
	 *
	 * @param inst                The bytecode instrumentation
	 * @param instrumentorClasses The instrumentor class names
	 */
	public static void initialize(Instrumentation inst, String[] instrumentorClasses) {
		if (!INSTANCE.isInitializing()) {
			throw new RuntimeException("Bootstrapper can only be initialized once");
		}

		INSTANCE.init(instrumentorClasses, inst);
	}


	/**
	 * Initializes the bootstrapper
	 *
	 * @param instrumentorClasses The instrumentor class names
	 * @param inst                The bytecode instrumentation
	 */
	private void init(String[] instrumentorClasses, Instrumentation inst) {
		//All exceptions before the IInstrumentors have been registered go into this list and are later redirected to the IInstrumentors after initialization
		List<Exception> bootstrapperInitExceptions = Collections.synchronizedList(new ArrayList<>());

		inst.addTransformer((loader, className, classBeingRedefined, protectionDoman, bytes) -> {
			try {
				boolean modified = false;

				final int structureFlags = ClassReader.SKIP_FRAMES | ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG;
				ClassReader classReader = new ClassReader(bytes);
				ClassNode clsNode = new ClassNode();
				classReader.accept(clsNode, structureFlags);

				hierarchy.addClass(loader, clsNode);

				final String classIdentifier = BytecodeInstrumentation.getAnnotationValue(clsNode.visibleAnnotations, ClassAccessor.class, BytecodeInstrumentation.getInternal(ClassAccessor.class, "class_identifier", Class::getDeclaredMethods).getName(), String.class, null, null);

				if (classIdentifier != null) {
					clsNode = new ClassNode();
					classReader.accept(clsNode, ClassReader.SKIP_FRAMES);
					if (instrumentor.instrumentAccessorClass(clsNode, this)) {
						modified = true;
					}
				}

				final ClassNode acceptsClassNode = clsNode;
				if (className != null && instrumentor.acceptsClass(clsNode, classIdentifier != null ? ClassReader.SKIP_FRAMES : structureFlags, flags -> {
					if ((classIdentifier != null && flags == ClassReader.SKIP_FRAMES) || (classIdentifier == null && flags == structureFlags)) {
						return acceptsClassNode;
					}

					ClassNode newNode = new ClassNode();
					classReader.accept(newNode, flags);
					return newNode;
				})) {
					if (classIdentifier == null) {
						clsNode = new ClassNode();
						classReader.accept(clsNode, ClassReader.SKIP_FRAMES);
					}

					final ClassNode instrumentClassNode = clsNode;
					instrumentor.instrumentClass(loader, clsNode, ClassReader.SKIP_FRAMES, flags -> {
						if (flags == ClassReader.SKIP_FRAMES) {
							return instrumentClassNode;
						}

						ClassNode newNode = new ClassNode();
						classReader.accept(newNode, flags);
						return newNode;
					});

					modified = true;
				}

				if (modified) {
					ClassWriter classWriter = new InstrumentationClassWriter(hierarchy, loader, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
					clsNode.accept(classWriter);
					return classWriter.toByteArray();
				}
			} catch (Exception ex) {
				if (!isInitializing()) {
					onBootstrapperException(ex);
				} else {
					synchronized (bootstrapperInitExceptions) {
						bootstrapperInitExceptions.add(ex);
					}
				}
			}

			return bytes;
		});

		List<Instrumentor> instrumentorInstances = new ArrayList<>();
		for (String instrumentorClass : instrumentorClasses) {
			try {
				Instrumentor instrumentor = this.initInstrumentor(instrumentorClass);
				instrumentorInstances.add(instrumentor);
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException ex) {
				bootstrapperInitExceptions.add(ex);
			}
		}

		this.instrumentors = new TreeSet<>(Comparator.comparing(c -> c.getClass().getName()));
		this.instrumentors.addAll(instrumentorInstances);

		for (Instrumentor instrumentor : this.instrumentors) {
			try {
				instrumentor.initBootstrapper(this);
			} catch (Exception ex) {
				bootstrapperInitExceptions.add(ex);
			}
		}

		for (ClassAccessorData accessor : this.accessors.getClassAccessors()) {
			if (this.hierarchy.getClass(Bootstrapper.class.getClassLoader(), accessor.getAccessorClass().replace(".", "/"), false, null) != null) {
				throw new InstrumentorException(String.format("Accessor class %s was already loaded before or during the bootstrapper initialization!", accessor.getAccessorClass()));
			}
		}

		for (ClassAccessorData accessor : this.accessors.getClassAccessors()) {
			try {
				@SuppressWarnings("unchecked")
				Class<Accessor> accessorCls = (Class<Accessor>) Bootstrapper.class
						.getClassLoader()
						.loadClass(accessor.getAccessorClass());

				if (this.hierarchy.getClass(Bootstrapper.class.getClassLoader(), accessorCls.getName().replace(".", "/"), false, null) == null) {
					throw new InstrumentorException(String.format("Accessor class %s could not be loaded properly!", accessorCls.getName()));
				}
			} catch (ClassNotFoundException e) {
				bootstrapperInitExceptions.add(e);
			}
		}

		synchronized (this) {
			this.initializing = false;
		}

		synchronized (bootstrapperInitExceptions) {
			for (Exception ex : bootstrapperInitExceptions) {
				this.onBootstrapperException(ex);
			}
		}

		for (Instrumentor instrumentor : this.instrumentors) {
			try {
				instrumentor.onInstrumentorRegistered(instrumentor);
			} catch (Exception ex) {
				this.onBootstrapperException(ex);
			}
		}

		for (Instrumentor instrumentor : this.instrumentors) {
			try {
				instrumentor.postInitBootstrapper(this);
			} catch (Exception ex) {
				this.onBootstrapperException(ex);
			}
		}
	}

	/**
	 * Creates a new instance of
	 * the specified instrumentor class
	 *
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
	private Instrumentor initInstrumentor(String instrumentorClass) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		return ((Class<Instrumentor>) this.getClass().getClassLoader().loadClass(instrumentorClass))
				.getDeclaredConstructor()
				.newInstance();
	}

	/**
	 * Returns the mapper registry
	 *
	 * @return
	 */
	public MapperRegistry getMapperRegistry() {
		return this.mapperRegistry;
	}

	/**
	 * Returns the accessor registry
	 *
	 * @return
	 */
	public Accessors getAccessors() {
		return this.accessors;
	}

	/**
	 * Returns the class locator registry
	 *
	 * @return
	 */
	public ClassLocatorRegistry getClassLocatorRegistry() {
		return this.classLocatorRegistry;
	}

	/**
	 * Returns whether the bootstrapper is in the initialization phase
	 *
	 * @return
	 */
	public synchronized boolean isInitializing() {
		return this.initializing;
	}

	/**
	 * Called when an exception occurs caused by the bootstrapper
	 *
	 * @param ex
	 */
	protected void onBootstrapperException(Exception ex) {
		for (Instrumentor instrumentor : this.instrumentors) {
			instrumentor.onBootstrapperException(ex);
		}
	}
}
