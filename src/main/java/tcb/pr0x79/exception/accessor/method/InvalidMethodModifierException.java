package tcb.pr0x79.exception.accessor.method;

import tcb.pr0x79.mapping.identification.AnnotatedElementDescription;

/**
 * Thrown when a method accessor has invalid modifiers
 */
public class InvalidMethodModifierException extends MethodAccessorException {
	/**
	 *
	 */
	private static final long serialVersionUID = -5370975537875899083L;

	private final int invalidModifier;

	public InvalidMethodModifierException(String msg, String accessor, AnnotatedElementDescription method, int invalidModifier) {
		this(msg, null, accessor, method, invalidModifier);
	}

	public InvalidMethodModifierException(String msg, Exception excp, String accessor, AnnotatedElementDescription method, int invalidModifier) {
		super(msg, excp, accessor, method);
		this.invalidModifier = invalidModifier;
	}

	public int getInvalidModifier() {
		return this.invalidModifier;
	}
}
