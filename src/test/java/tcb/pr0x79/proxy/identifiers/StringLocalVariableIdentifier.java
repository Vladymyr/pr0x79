package tcb.pr0x79.proxy.identifiers;

import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;
import tcb.pr0x79.mapping.identification.type.InstructionTypeIdentifier;

import java.util.List;

public class StringLocalVariableIdentifier implements InstructionTypeIdentifier {
	private final String[] names;

	public StringLocalVariableIdentifier(List<String> names) {
		this.names = names.toArray(new String[0]);
	}

	@Override
	public InstructionType getType() {
		return InstructionType.LOCAL_VARIABLE;
	}

	@Override
	public int identify(MethodNode method) {
		for (LocalVariableNode localVariable : method.localVariables) {
			for (String name : this.names) {
				if (name.equals(localVariable.name)) {
					return localVariable.index;
				}
			}
		}
		return -1;
	}
}
