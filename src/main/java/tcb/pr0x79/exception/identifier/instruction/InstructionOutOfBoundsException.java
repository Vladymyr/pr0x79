package tcb.pr0x79.exception.identifier.instruction;

import tcb.pr0x79.mapping.identification.AnnotatedElementDescription;
import tcb.pr0x79.mapping.identification.type.InstructionTypeIdentifier;

/**
 * Thrown when an {@link InstructionTypeIdentifier} returns an instruction index that is out of bounds
 */
public class InstructionOutOfBoundsException extends InstructionIdentifierException {
	/**
	 *
	 */
	private static final long serialVersionUID = 8012394449082583525L;

	private final int targetInstruction;
	private final int min, max;

	public InstructionOutOfBoundsException(String msg, Exception excp, int targetInstruction, int min, int max, String accessor, AnnotatedElementDescription method, String identifierId, InstructionTypeIdentifier identifier) {
		super(msg, excp, accessor, method, identifierId, identifier);
		this.targetInstruction = targetInstruction;
		this.min = min;
		this.max = max;
	}

	public int getTargetInstruction() {
		return this.targetInstruction;
	}

	public int getLowerBound() {
		return this.min;
	}

	public int getUpperBound() {
		return this.max;
	}
}
