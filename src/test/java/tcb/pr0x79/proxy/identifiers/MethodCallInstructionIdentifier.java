package tcb.pr0x79.proxy.identifiers;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import tcb.pr0x79.mapping.identification.AnnotatedElementDescription;
import tcb.pr0x79.mapping.identification.type.InstructionTypeIdentifier;

import java.util.Iterator;
import java.util.List;

public class MethodCallInstructionIdentifier implements InstructionTypeIdentifier {
	private final String[] owners;
	private final AnnotatedElementDescription[] methods;
	private final boolean before;

	public MethodCallInstructionIdentifier(List<String> owners, List<String> methodNames, List<String> methodDescriptors, boolean before) {
		this.owners = owners.toArray(new String[0]);
		this.methods = new AnnotatedElementDescription[methodNames.size()];
		for (int i = 0; i < methodNames.size(); i++) {
			this.methods[i] = AnnotatedElementDescription.methodDescription(methodNames.get(i), methodDescriptors.get(i));
		}
		this.before = before;
	}

	@Override
	public InstructionType getType() {
		return InstructionType.INSTRUCTION;
	}

	@Override
	public int identify(MethodNode method) {
		Iterator<AbstractInsnNode> it = method.instructions.iterator();
		AbstractInsnNode insn;
		while (it.hasNext()) {
			insn = it.next();
			if (insn instanceof MethodInsnNode) {
				MethodInsnNode methodNode = (MethodInsnNode) insn;
				for (int i = 0; i < this.owners.length; i++) {
					String owner = this.owners[i];
					AnnotatedElementDescription methodDescription = this.methods[i];
					if (owner.equals(methodNode.owner) && methodDescription.getName().equals(methodNode.name) && methodDescription.getDescriptor().equals(methodNode.desc)) {
						return method.instructions.indexOf(methodNode) + (this.before ? 0 : 1);
					}
				}
			}
		}
		return -1;
	}
}
