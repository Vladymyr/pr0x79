package tcb.pr0x79.exception.identifier.instruction;

import tcb.pr0x79.mapping.identification.AnnotatedElementDescription;
import tcb.pr0x79.mapping.identification.type.InstructionTypeIdentifier;

/**
 * Thrown when an {@link InstructionTypeIdentifier} fails to identify the exit instruction
 */
public class ExitInstructionNotFoundException extends InstructionNotFoundException {
	/**
	 *
	 */
	private static final long serialVersionUID = 1709704515366053314L;

	public ExitInstructionNotFoundException(String accessor, AnnotatedElementDescription method, String identifierId, InstructionTypeIdentifier identifier) {
		super(String.format("Instruction identifier for the instruction exit of %s#%s[%s] was unable to identify the instruction", accessor, method.getName() + method.getDescriptor(), identifierId), null, accessor, method, identifierId, identifier);
	}

	public ExitInstructionNotFoundException(String msg, Exception excp, String accessor, AnnotatedElementDescription method, String identifierId, InstructionTypeIdentifier identifier) {
		super(msg, excp, accessor, method, identifierId, identifier);
	}
}
