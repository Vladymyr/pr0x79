package tcb.pr0x79.data;

import tcb.pr0x79.mapping.Mapper;
import tcb.pr0x79.mapping.MapperRegistry;
import tcb.pr0x79.exception.InstrumentorException;
import tcb.pr0x79.mapping.identification.type.InstructionTypeIdentifier;

public final class LocalVarData {
	private final String interceptorMethod, interceptorMethodDesc;
	private final int parameterIndex;
	private final String instructionIdentifierId, accessorClass;
	private InstructionTypeIdentifier instructionIdentifier;

	LocalVarData(String interceptorMethod, String interceptorMethodDesc, int parameterIndex, String accessorClass, String instructionIdentifierId) {
		this.interceptorMethod = interceptorMethod;
		this.interceptorMethodDesc = interceptorMethodDesc;
		this.parameterIndex = parameterIndex;
		this.instructionIdentifierId = instructionIdentifierId;
		this.accessorClass = accessorClass;
	}

	/**
	 * Returns the index of the parameter of the interception method
	 *
	 * @return
	 */
	public int getParameterIndex() {
		return this.parameterIndex;
	}

	/**
	 * Returns the local variable instruction identifier ID of the local variable to be imported
	 *
	 * @return
	 */
	public String getInstructionIdentifierId() {
		return this.instructionIdentifierId;
	}

	/**
	 * Returns the local variable instruction identifier of the local variable to be imported
	 *
	 * @return
	 */
	public InstructionTypeIdentifier getInstructionIdentifier() {
		return this.instructionIdentifier;
	}

	/**
	 * Returns the interceptor method name
	 *
	 * @return
	 */
	public String getInterceptorMethod() {
		return this.interceptorMethod;
	}

	/**
	 * Returns the interceptor method descriptor
	 *
	 * @return
	 */
	public String getInterceptorMethodDesc() {
		return this.interceptorMethodDesc;
	}

	/**
	 * Initializes the identifier
	 *
	 * @param mapperRegistry
	 */
	public void initIdentifier(MapperRegistry mapperRegistry) {
		this.instructionIdentifier = mapperRegistry.getInstructionIdentifier(this.instructionIdentifierId, Mapper.SearchType.LOCAL_VARIABLE);
		if (this.instructionIdentifier == null) {
			throw new InstrumentorException(String.format("Instruction identifier %s#%s[%s] is not mapped", this.accessorClass, this.interceptorMethod + this.interceptorMethodDesc, this.instructionIdentifierId));
		}

		if (this.instructionIdentifier.getType() != InstructionTypeIdentifier.InstructionType.LOCAL_VARIABLE) {
			throw new InstrumentorException(String.format("Instruction identifier %s#%s[%s] is not of symbol LOCAL_VARIABLE", this.accessorClass, this.interceptorMethod + this.interceptorMethodDesc, this.instructionIdentifierId));
		}
	}
}
