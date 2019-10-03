package tcb.pr0x79.proxy.identifiers;

import org.objectweb.asm.tree.MethodNode;
import tcb.pr0x79.mapping.identification.type.InstructionTypeIdentifier;

public class IndexLocalVariableIdentifier implements InstructionTypeIdentifier {
	private final int index;
	private final boolean reversed;

	public IndexLocalVariableIdentifier(int index) {
		this.index = index;
		this.reversed = false;
	}

	public IndexLocalVariableIdentifier(int index, boolean reversed) {
		this.index = index;
		this.reversed = reversed;
	}

	@Override
	public InstructionType getType() {
		return InstructionType.LOCAL_VARIABLE;
	}

	@Override
	public int identify(MethodNode method) {
		return this.reversed ? method.localVariables.size() - this.index - 1 : this.index;
	}
}
