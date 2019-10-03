package tcb.pr0x79.mapping.identification.type;

import org.objectweb.asm.tree.ClassNode;
import tcb.pr0x79.exception.InstrumentorException;
import tcb.pr0x79.mapping.identification.BytecodeIdentifier;

import java.util.function.Function;

/**
 * Identifies classes
 */
public interface ClassIdentifier extends BytecodeIdentifier<String, String> {

	default boolean isIdentifiedClass(ClassNode cls, int flags, Function<Integer, ClassNode> reader) {
		throw new InstrumentorException("Dynamic mapping not implemented");
	}
}
