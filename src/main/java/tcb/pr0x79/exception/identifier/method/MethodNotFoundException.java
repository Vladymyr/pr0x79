package tcb.pr0x79.exception.identifier.method;

import tcb.pr0x79.mapping.identification.AnnotatedElementDescription;
import tcb.pr0x79.mapping.identification.type.MethodIdentifier;

/**
 * Thrown when an {@link MethodIdentifier} fails to identify a method
 */
public class MethodNotFoundException extends MethodIdentifierException {
	/**
	 *
	 */
	private static final long serialVersionUID = 9036686398085016462L;

	public MethodNotFoundException(String accessor, AnnotatedElementDescription method, String identifierId, MethodIdentifier identifier) {
		super(String.format("Method identifier %s#%s[%s] was unable to identify a method", accessor, method.getName() + method.getDescriptor(), identifierId), accessor, method, identifierId, identifier);
	}

	public MethodNotFoundException(String msg, Exception excp, String accessor, AnnotatedElementDescription method, String identifierId, MethodIdentifier identifier) {
		super(msg, excp, accessor, method, identifierId, identifier);
	}
}
