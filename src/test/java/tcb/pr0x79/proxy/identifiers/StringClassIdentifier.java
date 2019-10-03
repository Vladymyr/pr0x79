package tcb.pr0x79.proxy.identifiers;

import tcb.pr0x79.mapping.identification.type.ClassIdentifier;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * A simple implementation of {@link ClassIdentifier} that
 * identifies classes by matching the class name with a list of specified
 * names
 */
public class StringClassIdentifier implements ClassIdentifier {
	private final Set<String> classNames;

	public StringClassIdentifier(List<String> classNames) {
		this.classNames = new HashSet<>(classNames);
	}

	@Override
	public Set<String> getData() {
		return this.classNames;
	}

	@Override
	public boolean isStatic() {
		return true;
	}
}
