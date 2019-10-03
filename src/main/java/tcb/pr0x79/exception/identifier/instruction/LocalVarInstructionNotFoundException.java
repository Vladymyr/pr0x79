package tcb.pr0x79.exception.identifier.instruction;

import tcb.pr0x79.mapping.identification.AnnotatedElementDescription;
import tcb.pr0x79.mapping.identification.type.InstructionTypeIdentifier;

/**
 * Thrown when an {@link InstructionTypeIdentifier} fails to identify a local variable
 */
public class LocalVarInstructionNotFoundException extends InstructionNotFoundException {
	/**
	 *
	 */
	private static final long serialVersionUID = -6715875622023464775L;

	private final int param;

	public LocalVarInstructionNotFoundException(int param, String accessor, AnnotatedElementDescription method, String identifierId, InstructionTypeIdentifier identifier) {
		this(String.format("Instruction identifier of @LocalVar for parameter %d of %s#%s[%s] was unable to identify the the local variable", param, accessor, method.getName() + method.getDescriptor(), identifierId), null, param, accessor, method, identifierId, identifier);
	}

	public LocalVarInstructionNotFoundException(String msg, Exception exc, int param, String accessor, AnnotatedElementDescription method, String identifierId, InstructionTypeIdentifier identifier) {
		super(msg, exc, accessor, method, identifierId, identifier);
		this.param = param;
	}

	public int getParameterIndex() {
		return this.param;
	}
}
