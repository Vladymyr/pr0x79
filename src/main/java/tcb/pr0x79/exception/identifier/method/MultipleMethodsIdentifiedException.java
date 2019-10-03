package tcb.pr0x79.exception.identifier.method;

import tcb.pr0x79.mapping.identification.AnnotatedElementDescription;
import tcb.pr0x79.mapping.identification.type.MethodIdentifier;

/**
 * Thrown when multiple methods are identified by one {@link MethodIdentifier}
 */
public class MultipleMethodsIdentifiedException extends MethodIdentifierException {
	/**
	 *
	 */
	private static final long serialVersionUID = -3935137290069904342L;

	public MultipleMethodsIdentifiedException(String accessor, AnnotatedElementDescription method, String identifierId, MethodIdentifier identifier) {
		super(String.format("Method identifier %s#%s[%s] has identified multiple methods", accessor, method.getName() + method.getDescriptor(), identifierId), accessor, method, identifierId, identifier);
	}

	public MultipleMethodsIdentifiedException(String msg, Exception excp, String accessor, AnnotatedElementDescription method, String identifierId, MethodIdentifier identifier) {
		super(msg, excp, accessor, method, identifierId, identifier);
	}
}
