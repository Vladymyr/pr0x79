package tcb.pr0x79.exception.accessor.method;


import tcb.pr0x79.mapping.identification.AnnotatedElementDescription;

/**
 * Thrown when a method accessor has an invalid descriptor
 */
public class InvalidMethodDescriptorException extends MethodAccessorException {
	/**
	 *
	 */
	private static final long serialVersionUID = -5567689382530923304L;

	private final String currentDesc, expectedDesc;

	public InvalidMethodDescriptorException(String msg, String accessor, AnnotatedElementDescription method, String currentDesc, String expectedDesc) {
		this(msg, null, accessor, method, currentDesc, expectedDesc);
	}

	public InvalidMethodDescriptorException(String msg, Exception excp, String accessor, AnnotatedElementDescription method, String currentDesc, String expectedDesc) {
		super(msg, excp, accessor, method);
		this.currentDesc = currentDesc;
		this.expectedDesc = expectedDesc;
	}

	public String getCurrentDescriptor() {
		return this.currentDesc;
	}

	public String getExpectedDescriptor() {
		return this.expectedDesc;
	}
}
