package tcb.pr0x79.exception.identifier.field;

import tcb.pr0x79.mapping.identification.AnnotatedElementDescription;
import tcb.pr0x79.mapping.identification.type.FieldIdentifier;

/**
 * Thrown when multiple fields are identified by one {@link FieldIdentifier}
 */
public class MultipleFieldsIdentifiedException extends FieldIdentifierException {
	/**
	 *
	 */
	private static final long serialVersionUID = -4795049985687138200L;

	public MultipleFieldsIdentifiedException(String accessor, AnnotatedElementDescription method, String identifierId, FieldIdentifier identifier) {
		super(String.format("Field identifier %s#%s[%s] has identified multiple fields", accessor, method.getName() + method.getDescriptor(), identifierId), accessor, method, identifierId, identifier);
	}

	public MultipleFieldsIdentifiedException(String msg, Exception excp, String accessor, AnnotatedElementDescription method, String identifierId, FieldIdentifier identifier) {
		super(msg, excp, accessor, method, identifierId, identifier);
	}
}
