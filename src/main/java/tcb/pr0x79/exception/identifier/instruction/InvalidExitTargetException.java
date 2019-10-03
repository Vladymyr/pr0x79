package tcb.pr0x79.exception.identifier.instruction;

import tcb.pr0x79.mapping.identification.AnnotatedElementDescription;
import tcb.pr0x79.mapping.identification.type.InstructionTypeIdentifier;

/**
 * Thrown when an {@link InstructionTypeIdentifier} returns an invalid exit target
 */
public class InvalidExitTargetException extends InstructionIdentifierException {
	/**
	 *
	 */
	private static final long serialVersionUID = 400466053219303093L;

	private final int targetInstruction;

	public InvalidExitTargetException(String msg, int targetInstruction, String accessor, AnnotatedElementDescription method, String identifierId, InstructionTypeIdentifier identifier) {
		this(msg, null, targetInstruction, accessor, method, identifierId, identifier);
	}

	public InvalidExitTargetException(String msg, Exception excp, int targetInstruction, String accessor, AnnotatedElementDescription method, String identifierId, InstructionTypeIdentifier identifier) {
		super(msg, excp, accessor, method, identifierId, identifier);
		this.targetInstruction = targetInstruction;
	}

	public int getTargetInstruction() {
		return this.targetInstruction;
	}
}
