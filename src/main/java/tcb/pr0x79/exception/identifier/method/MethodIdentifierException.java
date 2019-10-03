package tcb.pr0x79.exception.identifier.method;

import tcb.pr0x79.exception.identifier.IdentifierException;
import tcb.pr0x79.mapping.identification.AnnotatedElementDescription;
import tcb.pr0x79.mapping.identification.type.MethodIdentifier;

/**
 * Thrown when something goes wrong during identification of methods by an {@link MethodIdentifier}
 */
public class MethodIdentifierException extends IdentifierException {
	/**
	 *
	 */
	private static final long serialVersionUID = -698120271459930196L;

	private final MethodIdentifier identifier;

	private final String accessor, identifierId;
	private final AnnotatedElementDescription method;

	public MethodIdentifierException(String msg, String accessor, AnnotatedElementDescription method, String identifierId, MethodIdentifier identifier) {
		this(msg, null, accessor, method, identifierId, identifier);
	}

	public MethodIdentifierException(String msg, Exception excp, String accessor, AnnotatedElementDescription method, String identifierId, MethodIdentifier identifier) {
		super(msg, excp);
		this.identifier = identifier;
		this.accessor = accessor;
		this.method = method;
		this.identifierId = identifierId;
	}

	public MethodIdentifier getIdentifier() {
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
