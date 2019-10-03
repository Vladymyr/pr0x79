package tcb.pr0x79.exception.identifier.instruction;

import tcb.pr0x79.mapping.identification.AnnotatedElementDescription;
import tcb.pr0x79.mapping.identification.type.InstructionTypeIdentifier;

/**
 * Thrown when an {@link InstructionTypeIdentifier} fails to identify an instruction
 */
public class InstructionNotFoundException extends InstructionIdentifierException {
	/**
	 *
	 */
	private static final long serialVersionUID = -6301836443408809031L;

	public InstructionNotFoundException(String accessor, AnnotatedElementDescription method, String identifierId, InstructionTypeIdentifier identifier) {
		super(String.format("Instruction identifier %s#%s[%s] was unable to identify the instruction", accessor, method.getName() + method.getDescriptor(), identifierId), accessor, method, identifierId, identifier);
	}

	public InstructionNotFoundException(String msg, Exception exc, String accessor, AnnotatedElementDescription method, String identifierId, InstructionTypeIdentifier identifier) {
		super(msg, exc, accessor, method, identifierId, identifier);
	}
}
