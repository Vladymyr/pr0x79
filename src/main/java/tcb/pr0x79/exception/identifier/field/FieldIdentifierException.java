package tcb.pr0x79.exception.identifier.field;

import tcb.pr0x79.exception.identifier.IdentifierException;
import tcb.pr0x79.mapping.identification.AnnotatedElementDescription;
import tcb.pr0x79.mapping.identification.type.FieldIdentifier;

/**
 * Thrown when something goes wrong during identification of fields by an {@link FieldIdentifier}
 */
public class FieldIdentifierException extends IdentifierException {
	/**
	 *
	 */
	private static final long serialVersionUID = 2578257736499007681L;

	private final FieldIdentifier identifier;
	private final String accessor, identifierId;
	private final AnnotatedElementDescription method;

	public FieldIdentifierException(String msg, String accessor, AnnotatedElementDescription method, String identifierId, FieldIdentifier identifier) {
		this(msg, null, accessor, method, identifierId, identifier);
	}

	public FieldIdentifierException(String msg, Exception excp, String accessor, AnnotatedElementDescription method, String identifierId, FieldIdentifier identifier) {
		super(msg);
		this.identifier = identifier;
		this.accessor = accessor;
		this.method = method;
		this.identifierId = identifierId;
	}

	public FieldIdentifier getIdentifier() {
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
