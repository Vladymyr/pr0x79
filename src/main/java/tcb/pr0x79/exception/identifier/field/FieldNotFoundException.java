package tcb.pr0x79.exception.identifier.field;

import tcb.pr0x79.mapping.identification.AnnotatedElementDescription;
import tcb.pr0x79.mapping.identification.type.FieldIdentifier;

/**
 * Thrown when an {@link FieldIdentifier} fails to identify a field
 */
public class FieldNotFoundException extends FieldIdentifierException {
	/**
	 *
	 */
	private static final long serialVersionUID = 1453444951899664209L;

	public FieldNotFoundException(String accessor, AnnotatedElementDescription method, String identifierId, FieldIdentifier identifier) {
		super(String.format("Field identifier %s#%s[%s] was unable to identify a field", accessor, method.getName() + method.getDescriptor(), identifierId), accessor, method, identifierId, identifier);
	}

	public FieldNotFoundException(String msg, Exception excp, String accessor, AnnotatedElementDescription method, String identifierId, FieldIdentifier identifier) {
		super(msg, excp, accessor, method, identifierId, identifier);
	}
}
