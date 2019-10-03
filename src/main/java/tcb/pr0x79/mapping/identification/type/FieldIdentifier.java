package tcb.pr0x79.mapping.identification.type;

import org.objectweb.asm.tree.FieldNode;
import tcb.pr0x79.mapping.identification.AnnotatedElementDescription;
import tcb.pr0x79.mapping.identification.BytecodeIdentifier;

import java.lang.reflect.Field;

/**
 * Identifies classes
 */
public interface FieldIdentifier extends BytecodeIdentifier<AnnotatedElementDescription<Field>, FieldNode> {


	default boolean isIdentifiedField(FieldNode fieldNode) {
		return (!this.isStatic() && this.isIdentified(fieldNode))
				|| (this.isStatic() && isInData(fieldNode));
	}

	default boolean isInData(FieldNode fieldNode) {
		for (AnnotatedElementDescription<Field> field : getData()) {
			if (field.getName().equals(fieldNode.name) && field.getDescriptor().equals(fieldNode.desc)) {
				return true;
			}
		}

		return false;
	}

}
