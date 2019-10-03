package tcb.pr0x79.exception.accessor.fieldgenerator;

import tcb.pr0x79.exception.accessor.AccessorException;
import tcb.pr0x79.mapping.identification.AnnotatedElementDescription;

/**
 * Thrown when something goes wrong with a field generator
 */
public class FieldGeneratorException extends AccessorException {
	/**
	 *
	 */
	private static final long serialVersionUID = -5589064862787370805L;

	private final String accessor;
	private final AnnotatedElementDescription method;

	public FieldGeneratorException(String msg, String accessor, AnnotatedElementDescription method) {
		this(msg, null, accessor, method);
	}

	public FieldGeneratorException(String msg, Exception excp, String accessor, AnnotatedElementDescription method) {
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
