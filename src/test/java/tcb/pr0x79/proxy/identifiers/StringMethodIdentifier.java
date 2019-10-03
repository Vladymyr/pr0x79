package tcb.pr0x79.proxy.identifiers;

import tcb.pr0x79.mapping.identification.AnnotatedElementDescription;
import tcb.pr0x79.mapping.identification.type.MethodIdentifier;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A simple implementation of {@link MethodIdentifier} that
 * identifies methods by matching the method descriptor
 * and the method name with a list of specified names
 */
public class StringMethodIdentifier implements MethodIdentifier {
	private final Set<AnnotatedElementDescription<Method>> mappings = new HashSet<>();

	public StringMethodIdentifier(List<String> methodNames, List<String> methodDescriptors) {
		for (int i = 0; i < methodNames.size(); i++) {
			this.mappings.add(AnnotatedElementDescription.methodDescription(methodNames.get(i), methodDescriptors.get(i)));
		}
	}

	@Override
	public Set<AnnotatedElementDescription<Method>> getData() {
		return this.mappings;
	}

	@Override
	public boolean isStatic() {
		return true;
	}
}
