package tcb.pr0x79.mapping.identification;

import tcb.pr0x79.exception.InstrumentorException;

import java.util.Set;

public interface BytecodeIdentifier<T, U> {

	default boolean isIdentified(U data) {
		throw new InstrumentorException("Dynamic mapping not implemented");
	}

	default Set<T> getData() {
		throw new InstrumentorException("Static mapping not implemented");
	}

	boolean isStatic();
}
