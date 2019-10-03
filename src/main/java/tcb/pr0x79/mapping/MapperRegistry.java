package tcb.pr0x79.mapping;

import tcb.pr0x79.Bootstrapper;
import tcb.pr0x79.mapping.identification.type.ClassIdentifier;
import tcb.pr0x79.mapping.identification.type.FieldIdentifier;
import tcb.pr0x79.mapping.identification.type.InstructionTypeIdentifier;
import tcb.pr0x79.mapping.identification.type.MethodIdentifier;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Registry for mappers
 */
public final class MapperRegistry {
	private final Bootstrapper bootstrapper;
	private final Map<String, Mapper<ClassIdentifier>> classIdentifierMappers = new HashMap<>();
	private final Map<String, Mapper<FieldIdentifier>> fieldIdentifierMappers = new HashMap<>();
	private final Map<String, Mapper<MethodIdentifier>> methodIdentifierMappers = new HashMap<>();
	private final Map<String, Mapper<InstructionTypeIdentifier>> instructionIdentifierMappers = new HashMap<>();

	public MapperRegistry(Bootstrapper bootstrapper) {
		this.bootstrapper = bootstrapper;
	}

	/**
	 * Registers a class mapper that maps a string
	 * to a {@link ClassIdentifier}.
	 *
	 * @param id     The ID of the mapper
	 * @param mapper The mapper that maps a string to a {@link ClassIdentifier}
	 */
	public synchronized void registerClassMapper(String id, Mapper<ClassIdentifier> mapper) {
		this.checkBootstrapperState();
		this.classIdentifierMappers.put(id, mapper);
	}

	/**
	 * Unregisters a class mapper
	 *
	 * @param id The ID of the mapper
	 * @return
	 */
	public synchronized Mapper<ClassIdentifier> unregisterClassMapper(String id) {
		this.checkBootstrapperState();
		return this.classIdentifierMappers.remove(id);
	}

	/**
	 * Returns all registered class mapper IDs
	 *
	 * @return
	 */
	public synchronized Set<String> getClassMappers() {
		return new HashSet<>(this.classIdentifierMappers.keySet());
	}

	/**
	 * Returns the class identifier for the specified identifier string.
	 *
	 * @param identifier The identifier string
	 * @param search     What is being searched
	 * @return
	 */
	public synchronized ClassIdentifier getClassIdentifier(String identifier, Mapper.SearchType search) {
		for (Mapper<ClassIdentifier> mapper : this.classIdentifierMappers.values()) {
			ClassIdentifier i = mapper.map(identifier, search);
			if (i != null) {
				return i;
			}
		}

		return null;
	}

	/**
	 * Registers a field mapper that maps a string
	 * to a {@link FieldIdentifier}.
	 *
	 * @param id     The ID of the mapper
	 * @param mapper The mapper that maps a string to a {@link FieldIdentifier}
	 */
	public synchronized void registerFieldMapper(String id, Mapper<FieldIdentifier> mapper) {
		this.checkBootstrapperState();

		this.fieldIdentifierMappers.put(id, mapper);
	}

	/**
	 * Unregisters a field mapper
	 *
	 * @param id The ID of the mapper
	 * @return
	 */
	public synchronized Mapper<FieldIdentifier> unregisterFieldMapper(String id) {
		this.checkBootstrapperState();

		return this.fieldIdentifierMappers.remove(id);
	}

	/**
	 * Returns all registered field mapper IDs
	 *
	 * @return
	 */
	public synchronized Set<String> getFieldMappers() {
		return new HashSet<>(this.fieldIdentifierMappers.keySet());
	}

	/**
	 * Returns the field identifier for the specified identifier string.
	 *
	 * @param identifier The identifier string
	 * @param search     What is being searched
	 * @return
	 */
	public synchronized FieldIdentifier getFieldIdentifier(String identifier, Mapper.SearchType search) {
		for (Mapper<FieldIdentifier> mapper : this.fieldIdentifierMappers.values()) {
			FieldIdentifier i = mapper.map(identifier, search);
			if (i != null) {
				return i;
			}
		}
		return null;
	}

	/**
	 * Registers a method mapper that maps a string
	 * to a {@link MethodIdentifier}.
	 *
	 * @param id     The ID of the mapper
	 * @param mapper The mapper that maps a string to a {@link MethodIdentifier}
	 */
	public synchronized void registerMethodMapper(String id, Mapper<MethodIdentifier> mapper) {
		this.checkBootstrapperState();
		this.methodIdentifierMappers.put(id, mapper);
	}

	/**
	 * Unregisters a method mapper
	 *
	 * @param id The ID of the mapper
	 * @return
	 */
	public synchronized Mapper<MethodIdentifier> unregisterMethodMapper(String id) {
		this.checkBootstrapperState();
		return this.methodIdentifierMappers.remove(id);
	}

	/**
	 * Returns all registered method mapper IDs
	 *
	 * @return
	 */
	public synchronized Set<String> getMethodMappers() {
		return new HashSet<>(this.methodIdentifierMappers.keySet());
	}

	/**
	 * Returns the method identifier for the specified identifier string.
	 *
	 * @param identifier The identifier string
	 * @param search     What is being searched
	 * @return
	 */
	public synchronized MethodIdentifier getMethodIdentifier(String identifier, Mapper.SearchType search) {
		for (Mapper<MethodIdentifier> mapper : this.methodIdentifierMappers.values()) {
			MethodIdentifier i = mapper.map(identifier, search);
			if (i != null) {
				return i;
			}
		}
		return null;
	}

	/**
	 * Registers an instruction mapper that maps a string
	 * to a {@link InstructionTypeIdentifier}.
	 *
	 * @param id     The ID of the mapper
	 * @param mapper The mapper that maps a string to a {@link InstructionTypeIdentifier}
	 */
	public synchronized void registerInstructionMapper(String id, Mapper<InstructionTypeIdentifier> mapper) {
		this.checkBootstrapperState();
		this.instructionIdentifierMappers.put(id, mapper);
	}

	/**
	 * Unregisters an instruction mapper
	 *
	 * @param id The ID of the mapper
	 * @return
	 */
	public synchronized Mapper<InstructionTypeIdentifier> unregisterInstructionMapper(String id) {
		this.checkBootstrapperState();
		return this.instructionIdentifierMappers.remove(id);
	}

	/**
	 * Returns all registered instruction mapper IDs
	 *
	 * @return
	 */
	public synchronized Set<String> getInstructionMappers() {
		return new HashSet<>(this.instructionIdentifierMappers.keySet());
	}

	/**
	 * Returns the instruction identifier for the specified identifier string.
	 *
	 * @param identifier The identifier string
	 * @param search     What is being searched
	 * @return
	 */
	public synchronized InstructionTypeIdentifier getInstructionIdentifier(String identifier, Mapper.SearchType search) {
		for (Mapper<InstructionTypeIdentifier> mapper : this.instructionIdentifierMappers.values()) {
			InstructionTypeIdentifier i = mapper.map(identifier, search);
			if (i != null) {
				return i;
			}
		}
		return null;
	}

	/**
	 * Validates the boostrapper state and throws an exception if the bootstrapper is no longer initializing
	 */
	private void checkBootstrapperState() {
		if (!this.bootstrapper.isInitializing()) {
			throw new RuntimeException("MapperRegistry must be (un-)registered during the bootstrap initialization");
		}
	}

}
