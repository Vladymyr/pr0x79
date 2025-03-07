package tcb.pr0x79;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import tcb.pr0x79.mapping.Mapper;
import tcb.pr0x79.mapping.MapperRegistry;
import tcb.pr0x79.accessor.ClassAccessor;
import tcb.pr0x79.data.ClassAccessorData;
import tcb.pr0x79.exception.InstrumentorException;
import tcb.pr0x79.mapping.identification.type.ClassIdentifier;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry for accessors
 */
public final class Accessors {
	private final Bootstrapper bootstrapper;
	private final MapperRegistry mapperRegistry;
	private final BytecodeInstrumentation instrumentor;
	private final Map<String, List<ClassAccessorData>> accessorsById = new ConcurrentHashMap<>();
	private final Map<String, ClassAccessorData> accessorsByClassName = new ConcurrentHashMap<>();

	Accessors(Bootstrapper bootstrapper, MapperRegistry mapperRegistry, BytecodeInstrumentation instrumentor) {
		this.bootstrapper = bootstrapper;
		this.mapperRegistry = mapperRegistry;
		this.instrumentor = instrumentor;
	}

	/**
	 * Registers an accessor. The accessor class must not be loaded before or during
	 * the Bootstrapper initialization
	 *
	 * @param className
	 */
	public void registerAccessor(String className) {
		if (!this.bootstrapper.isInitializing()) {
			throw new InstrumentorException(String.format("Accessor %s must be registered during the bootstrap initialization", className));
		}

		final ClassNode clsNode;
		try {
			ClassReader clsReader = new ClassReader(className);
			clsNode = new ClassNode();
			clsReader.accept(clsNode, ClassReader.SKIP_FRAMES);
		} catch (IOException e) {
			throw new InstrumentorException(String.format("Could not load accessor class %s", className));
		}

		if ((clsNode.access & Opcodes.ACC_INTERFACE) == 0) {
			throw new InstrumentorException(String.format("Accessor %s is not an interface", className));
		}

		String classIdentifierId = BytecodeInstrumentation.getAnnotationValue(clsNode.visibleAnnotations, ClassAccessor.class, BytecodeInstrumentation.getInternal(ClassAccessor.class, "class_identifier", Class::getDeclaredMethods).getName(), String.class, null, null);
		if (classIdentifierId == null) {
			throw new InstrumentorException(String.format("Accessor %s does not have a class accessor annotation", className));
		}

		ClassIdentifier clsIdentifier = null;
		if (classIdentifierId != null) {
			clsIdentifier = this.mapperRegistry.getClassIdentifier(classIdentifierId, Mapper.SearchType.ACCESSOR);
		}

		if (clsIdentifier == null) {
			throw new InstrumentorException(String.format("Class identifier %s[%s] is not mapped", className, classIdentifierId));
		}

		ClassAccessorData accessorData = new ClassAccessorData(classIdentifierId, this.mapperRegistry, className, clsNode, clsIdentifier, this.instrumentor);
		List<ClassAccessorData> accessors = this.accessorsById.computeIfAbsent(classIdentifierId, k -> new ArrayList<>());
		accessors.add(accessorData);

		this.accessorsByClassName.put(className, accessorData);
	}

	/**
	 * Unregisters an accessor
	 *
	 * @param className
	 * @return
	 */
	public ClassAccessorData unregisterAccessor(String className) {
		if (!this.bootstrapper.isInitializing()) {
			throw new InstrumentorException(String.format("Accessor %s must be unregistered during the bootstrap initialization", className));
		}

		ClassAccessorData accessor = this.accessorsByClassName.remove(className);
		if (accessor != null) {
			List<ClassAccessorData> accessors = this.accessorsById.get(accessor.getIdentifierId());
			if (accessors != null) {
				accessors.remove(accessor);
				if (accessors.isEmpty()) {
					this.accessorsById.remove(accessor.getIdentifierId());
				}
			}
		}

		return accessor;
	}

	/**
	 * Gets an accessor by (non-internal!) class name
	 *
	 * @param name
	 * @return
	 */
	public ClassAccessorData getAccessorByClassName(String name) {
		return this.accessorsByClassName.get(name);
	}

	/**
	 * Gets a list of accessors with the specified id, can be null if no accessors are found
	 *
	 * @param name
	 * @return
	 */
	public List<ClassAccessorData> getAccessorsById(String name) {
		return this.accessorsById.get(name);
	}

	/**
	 * Returns all class accessors
	 *
	 * @return
	 */
	public Collection<ClassAccessorData> getClassAccessors() {
		return Collections.unmodifiableCollection(this.accessorsByClassName.values());
	}
}
