package tcb.pr0x79.exception.accessor.field;


import tcb.pr0x79.mapping.identification.AnnotatedElementDescription;

/**
 * Thrown when the name of a field accessor is already present in the class to instrument
 */
public class FieldAccessorTakenException extends FieldAccessorException {
	/**
	 *
	 */
	private static final long serialVersionUID = -1789455878450140795L;

	public FieldAccessorTakenException(String msg, String accessor, AnnotatedElementDescription method) {
		super(msg, accessor, method);
	}

	public FieldAccessorTakenException(String msg, Exception excp, String accessor, AnnotatedElementDescription method) {
		super(msg, excp, accessor, method);
	}
}
