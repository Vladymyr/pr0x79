package tcb.pr0x79;

import tcb.pr0x79.accessor.Accessor;
import tcb.pr0x79.mapping.identification.type.ClassIdentifier;
import tcb.pr0x79.mapping.identification.type.FieldIdentifier;
import tcb.pr0x79.mapping.identification.type.InstructionTypeIdentifier;
import tcb.pr0x79.mapping.identification.type.MethodIdentifier;

/**
 * <b>The class that implements {@link Instrumentor}, and any {@link Accessor} used therein, must not be loaded before the bootstrapper is initialized</b>.
 * Instead, the full class name of the instrumentor must be passed in to {@link Bootstrapper#initialize(String[], java.lang.instrument.Instrumentation)}.
 * The instrumentor must have a no-args constructor.
 */
public interface Instrumentor {
	/**
	 * Called after the bootstrapper has been initialized.
	 *
	 * @param bootstrapper
	 */
	default void postInitBootstrapper(Bootstrapper bootstrapper) {
	}

	/**
	 * Called when the bootstrapper is initialized.
	 * Identifiers ({@link ClassIdentifier}, {@link MethodIdentifier}, {@link FieldIdentifier}, {@link InstructionTypeIdentifier}) and {@link Accessor}s must be registered during the bootstrapper initialization.
	 *
	 * @param bootstrapper
	 */
	default void initBootstrapper(Bootstrapper bootstrapper) {
	}

	/**
	 * Called when an instrumentor is registered to the bootstrapper
	 *
	 * @param instrumentor
	 */
	default void onInstrumentorRegistered(Instrumentor instrumentor) {
	}

	/**
	 * Called when an exception occurs caused by the bootstrapper
	 *
	 * @param ex
	 */
	default void onBootstrapperException(Exception ex) {
		throw new RuntimeException(ex);
	}
}
