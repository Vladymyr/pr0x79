package tcb.pr0x79.exception.accessor.field;

import tcb.pr0x79.mapping.identification.AnnotatedElementDescription;

/**
 * Thrown when a field accessor has an invalid return symbol
 */
public class InvalidGetterTypeException extends FieldAccessorException {
	/**
	 *
	 */
	private static final long serialVersionUID = -5905302624286334226L;

	private final String currentType, expectedType;

	public InvalidGetterTypeException(String accessor, AnnotatedElementDescription method, String currentReturnType, String expectedReturnType) {
		this(String.format("Field accessor %s#%s return symbol does not match. Current: %s, Expected: %s, or an accessor of that class", accessor, method.getName() + method.getDescriptor(), currentReturnType, expectedReturnType), null, accessor, method, currentReturnType, expectedReturnType);
	}

	public InvalidGetterTypeException(String msg, Exception excp, String accessor, AnnotatedElementDescription method, String currentReturnType, String expectedReturnType) {
		super(msg, excp, accessor, method);
		this.currentType = currentReturnType;
		this.expectedType = expectedReturnType;
	}

	public String getCurrentReturnType() {
		return this.currentType;
	}

	public String getExpectedReturnType() {
		return this.expectedType;
	}
}
