package tcb.pr0x79.proxy.identifiers;

import tcb.pr0x79.mapping.identification.AnnotatedElementDescription;
import tcb.pr0x79.mapping.identification.type.FieldIdentifier;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A simple implementation of {@link FieldIdentifier} that
 * identifies fields by matching the field descriptor
 * and the field name with a list of specified names
 */
public class StringFieldIdentifier implements FieldIdentifier {
	private final Set<AnnotatedElementDescription<Field>> mappings = new HashSet<>();

	public StringFieldIdentifier(List<String> fieldNames, List<String> fieldDescriptors) {
		for (int i = 0; i < fieldNames.size(); i++) {
			this.mappings.add(AnnotatedElementDescription.fieldDescription(fieldNames.get(i), fieldDescriptors.get(i)));
		}
	}

	@Override
	public Set<AnnotatedElementDescription<Field>> getData() {
		return this.mappings;
	}

	@Override
	public boolean isStatic() {
		return true;
	}
}
