package tcb.pr0x79.exception.accessor.method;

import tcb.pr0x79.exception.accessor.AccessorException;
import tcb.pr0x79.mapping.identification.AnnotatedElementDescription;

/**
 * Thrown when something goes wrong with a method accessor
 */
public class MethodAccessorException extends AccessorException {
	/**
	 *
	 */
	private static final long serialVersionUID = -3804720363531842287L;

	private final String accessor;
	private final AnnotatedElementDescription method;

	public MethodAccessorException(String msg, String accessor, AnnotatedElementDescription method) {
		this(msg, null, accessor, method);
	}

	public MethodAccessorException(String msg, Exception excp, String accessor, AnnotatedElementDescription method) {
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
