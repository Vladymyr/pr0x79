package tcb.pr0x79.mapping.identification.type;

import org.objectweb.asm.tree.MethodNode;

/**
 * Identifies the index of an instruction in a {@link MethodNode}
 */
public interface InstructionTypeIdentifier {
	/**
	 * Returns the instruction symbol this identifier can identify
	 *
	 * @return
	 */
	InstructionType getType();

	/**
	 * Returns the index of the instruction to identify, or -1 if not found
	 *
	 * @param method
	 * @return
	 */
	int identify(MethodNode method);

	enum InstructionType {
		INSTRUCTION,
		LOCAL_VARIABLE
	}
}