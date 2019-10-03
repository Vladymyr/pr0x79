package tcb.pr0x79.exception.accessor;

import tcb.pr0x79.accessor.Accessor;
import tcb.pr0x79.exception.InstrumentorException;

/**
 * Thrown when something goes wrong with an {@link Accessor}
 */
public class AccessorException extends InstrumentorException {
	/**
	 *
	 */
	private static final long serialVersionUID = 5778137463465864291L;

	public AccessorException(String msg) {
		super(msg);
	}

	public AccessorException(String msg, Exception exc) {
		super(msg, exc);
	}
}
