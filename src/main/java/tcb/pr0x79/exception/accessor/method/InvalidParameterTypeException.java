package tcb.pr0x79.exception.accessor.method;

import tcb.pr0x79.mapping.identification.AnnotatedElementDescription;

/**
 * Thrown when a method accessor has invalid parameters
 */
public class InvalidParameterTypeException extends MethodAccessorException {
	/**
	 *
	 */
	private static final long serialVersionUID = 4937835901586489041L;

	private final int param;
	private final String currentType, expectedType;

	public InvalidParameterTypeException(String msg, String accessor, AnnotatedElementDescription method, int param, String currentType, String expectedType) {
		this(msg, null, accessor, method, param, currentType, expectedType);
	}

	public InvalidParameterTypeException(String msg, Exception excp, String accessor, AnnotatedElementDescription method, int param, String currentType, String expectedType) {
		super(msg, excp, accessor, method);
		this.param = param;
		this.currentType = currentType;
		this.expectedType = expectedType;
	}

	public int getParameterIndex() {
		return this.param;
	}

	public String getCurrentParameterType() {
		return this.currentType;
	}

	public String getExpectedParameterType() {
		return this.expectedType;
	}
}
