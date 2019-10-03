package tcb.pr0x79.proxy.identifiers;

import org.objectweb.asm.tree.MethodNode;
import tcb.pr0x79.mapping.identification.type.InstructionTypeIdentifier;

public class IndexInstructionIdentifier implements InstructionTypeIdentifier {
	private final int index;
	private final boolean reversed;

	public IndexInstructionIdentifier(int index) {
		this.index = index;
		this.reversed = false;
	}

	public IndexInstructionIdentifier(int index, boolean reversed) {
		this.index = index;
		this.reversed = reversed;
	}

	@Override
	public InstructionTypeIdentifier.InstructionType getType() {
		return InstructionType.INSTRUCTION;
	}

	@Override
	public int identify(MethodNode method) {
		return this.reversed ? method.instructions.size() - this.index - 1 : this.index;
	}
}
