package tcb.pr0x79.exception.identifier.instruction;

import tcb.pr0x79.exception.identifier.IdentifierException;
import tcb.pr0x79.mapping.identification.AnnotatedElementDescription;
import tcb.pr0x79.mapping.identification.type.InstructionTypeIdentifier;

/**
 * Thrown when something goes wrong during identification of instructions by an {@link InstructionTypeIdentifier}
 */
public class InstructionIdentifierException extends IdentifierException {
	/**
	 *
	 */
	private static final long serialVersionUID = -283728513729500070L;

	private final InstructionTypeIdentifier identifier;
	private final String accessor, identifierId;
	private final AnnotatedElementDescription method;

	public InstructionIdentifierException(String msg, String accessor, AnnotatedElementDescription method, String identifierId, InstructionTypeIdentifier identifier) {
		this(msg, null, accessor, method, identifierId, identifier);
	}

	public InstructionIdentifierException(String msg, Exception exc, String accessor, AnnotatedElementDescription method, String identifierId, InstructionTypeIdentifier identifier) {
		super(msg, exc);
		this.identifier = identifier;
		this.accessor = accessor;
		this.method = method;
		this.identifierId = identifierId;
	}

	public InstructionTypeIdentifier getIdentifier() {
		return this.identifier;
	}

	public String getAccessorClass() {
		return this.accessor;
	}

	public AnnotatedElementDescription getAccessorMethod() {
		return this.method;
	}

	public String getIdentifierId() {
		return this.identifierId;
	}
}
