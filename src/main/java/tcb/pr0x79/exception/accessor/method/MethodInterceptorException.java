package tcb.pr0x79.exception.accessor.method;

import tcb.pr0x79.exception.accessor.AccessorException;
import tcb.pr0x79.mapping.identification.AnnotatedElementDescription;

/**
 * Thrown when something goes wrong with a method interceptor
 */
public class MethodInterceptorException extends AccessorException {

	/**
	 *
	 */
	private static final long serialVersionUID = 7367122485446639843L;

	private final String accessor;
	private final AnnotatedElementDescription method;

	public MethodInterceptorException(String msg, String accessor, AnnotatedElementDescription method) {
		this(msg, null, accessor, method);
	}

	public MethodInterceptorException(String msg, Exception excp, String accessor, AnnotatedElementDescription method) {
		super(msg);
		this.accessor = accessor;
		this.method = method;
	}

	public String getAccessorClass() {
		return this.accessor;
	}

	public AnnotatedElementDescription getInterceptorMethod() {
		return this.method;
	}
}
