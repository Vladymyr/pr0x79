package tcb.pr0x79.exception.accessor.method;

import tcb.pr0x79.mapping.identification.AnnotatedElementDescription;

/**
 * Thrown when the name of a method accessor is already present in the class to instrument
 */
public class MethodAccessorTakenException extends MethodAccessorException {
	/**
	 *
	 */
	private static final long serialVersionUID = 2935900368573669929L;

	public MethodAccessorTakenException(String msg, String accessor, AnnotatedElementDescription method) {
		super(msg, accessor, method);
	}

	public MethodAccessorTakenException(String msg, Exception excp, String accessor, AnnotatedElementDescription method) {
		super(msg, excp, accessor, method);
	}
}
