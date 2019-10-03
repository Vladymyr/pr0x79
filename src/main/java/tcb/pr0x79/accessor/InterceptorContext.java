package tcb.pr0x79.accessor;

import tcb.pr0x79.Internal;
import tcb.pr0x79.mapping.identification.type.InstructionTypeIdentifier;

public interface InterceptorContext<T> {
	/**
	 * Causes the interception to exit at the instruction identified by the specified {@link InstructionTypeIdentifier}
	 *
	 * @param index The index of the {@link InstructionTypeIdentifier} specified in {@link Interceptor#exitInstructionIdentifiers()}
	 */
	void exitAt(int index);

	/**
	 * Cancels {@link #exitAt(int)}
	 */
	void cancelExit();

	/**
	 * Returns the exit index (see {@link #exitAt(int)})
	 *
	 * @return
	 */
	@Internal(id = "get_exit")
	int getExit();

	/**
	 * Returns whether an exit index was set and the
	 * interceptor will exit at a different instruction
	 *
	 * @return
	 */
	@Internal(id = "is_exiting")
	boolean isExiting();

	/**
	 * Causes the intercepted method to return with the specified value
	 *
	 * @param obj The object to be returned
	 */
	void returnWith(T obj);

	/**
	 * Cancels {@link #returnWith(Object)}
	 */
	void cancelReturn();

	/**
	 * Returns the return value (see {@link #returnWith(Object)}
	 *
	 * @return
	 */
	@Internal(id = "get_return")
	T getReturn();

	/**
	 * Returns whether a return value was set and the
	 * intercepted method will return after the interception
	 * is over
	 *
	 * @return
	 */
	@Internal(id = "is_returning")
	boolean isReturning();

	/**
	 * Populated with all local variables after the interceptor terminates
	 *
	 * @return
	 */
	@Internal(id = "get_local_variables")
	Object[] getLocalVariables();
}
