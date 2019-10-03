package tcb.pr0x79.exception.accessor.field;

import tcb.pr0x79.exception.accessor.AccessorException;
import tcb.pr0x79.mapping.identification.AnnotatedElementDescription;

/**
 * Thrown when something goes wrong with a field accessor
 */
public class FieldAccessorException extends AccessorException {
	/**
	 *
	 */
	private static final long serialVersionUID = 7112058799886783845L;

	private final String accessor;
	private final AnnotatedElementDescription method;

	public FieldAccessorException(String msg, String accessor, AnnotatedElementDescription method) {
		this(msg, null, accessor, method);
	}

	public FieldAccessorException(String msg, Exception excp, String accessor, AnnotatedElementDescription method) {
		super(msg);
		this.accessor = accessor;
		this.method = method;
	}

	public String getAccessorClass() {
		return this.accessor;
	}

	public AnnotatedElementDescription getAccessorMethod() {
		return this.method;
	}
}
