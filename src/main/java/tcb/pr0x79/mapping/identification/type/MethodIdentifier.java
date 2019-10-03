package tcb.pr0x79.mapping.identification.type;

import org.objectweb.asm.tree.MethodNode;
import tcb.pr0x79.mapping.identification.AnnotatedElementDescription;
import tcb.pr0x79.mapping.identification.BytecodeIdentifier;

import java.lang.reflect.Method;

/**
 * Identifies methods
 */
public interface MethodIdentifier extends BytecodeIdentifier<AnnotatedElementDescription<Method>, MethodNode> {

	default boolean isIdentifiedMethod(MethodNode methodNode) {
		return (!this.isStatic() && this.isIdentified(methodNode))
				|| (this.isStatic() && isInData(methodNode));
	}

	default boolean isInData(MethodNode methodNode) {
		for (AnnotatedElementDescription<Method> method : getData()) {
			if (method.getName().equals(methodNode.name) && method.getDescriptor().equals(methodNode.desc)) {
				return true;
			}
		}

		return false;
	}
}
