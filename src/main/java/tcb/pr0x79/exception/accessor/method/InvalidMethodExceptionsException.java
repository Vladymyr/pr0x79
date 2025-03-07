package tcb.pr0x79.exception.accessor.method;

import tcb.pr0x79.mapping.identification.AnnotatedElementDescription;

/**
 * Thrown when a method accessor has invalid checked exceptions
 */
public class InvalidMethodExceptionsException extends MethodAccessorException {
	/**
	 *
	 */
	private static final long serialVersionUID = -7575991650644819642L;

	private final String currentExceptions, expectedExceptions;

	public InvalidMethodExceptionsException(String msg, String accessor, AnnotatedElementDescription method, String currentExceptions, String expectedExceptions) {
		this(msg, null, accessor, method, currentExceptions, expectedExceptions);
	}

	public InvalidMethodExceptionsException(String msg, Exception excp, String accessor, AnnotatedElementDescription method, String currentExceptions, String expectedExceptions) {
		super(msg, excp, accessor, method);
		this.currentExceptions = currentExceptions;
		this.expectedExceptions = expectedExceptions;
	}

	public String getCurrentExceptions() {
		return this.currentExceptions;
	}

	public String getExpectedExceptions() {
		return this.expectedExceptions;
	}
}
