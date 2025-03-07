package tcb.pr0x79.signature;

import tcb.pr0x79.ClassHierarchy;
import tcb.pr0x79.ClassHierarchy.ClassData;
import tcb.pr0x79.signature.symbol.FormalTypeParameterSymbol;
import tcb.pr0x79.signature.symbol.TypeVariableSymbol;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Finds and resolves all formal symbol parameters from variables in a {@link Signature}
 */
public final class SignatureTypesResolver {
	protected final ClassLoader loader;
	protected final ClassHierarchy hierarchy;

	public SignatureTypesResolver(ClassHierarchy hierarchy, ClassLoader loader) {
		this.loader = loader;
		this.hierarchy = hierarchy;
	}

	/**
	 * Attempts to resolve all variables in the specified method signature.
	 * May fail for local classes.
	 *
	 * @param owner The internal class name of the owner class
	 * @param sig   The signature to resolve
	 * @return
	 */
	public Map<String, FormalTypeParameterSymbol> resolve(String owner, Signature sig) {
		Set<String> variables = findVariables(sig);

		Map<String, FormalTypeParameterSymbol> resolved = new HashMap<>();

		resolve(owner, variables, resolved, sig);

		return resolved;
	}

	/**
	 * Resolves all variables in the specified class signature.
	 * May fail for local classes.
	 *
	 * @param cls The class of the signature
	 * @param sig The signature to resolve
	 * @return
	 */
	public Map<String, FormalTypeParameterSymbol> resolve(ClassData cls, Signature sig) {
		Set<String> variables = findVariables(sig);

		Map<String, FormalTypeParameterSymbol> resolved = new HashMap<>();
		resolve(cls, variables, resolved, sig);

		return resolved;
	}

	private void resolve(String owner, Set<String> variables, Map<String, FormalTypeParameterSymbol> resolved, Signature sig) {
		resolveVariables(resolved, variables, sig);
		variables.addAll(findVariables(sig));
		for (String key : resolved.keySet()) {
			variables.remove(key);
		}

		if (!variables.isEmpty()) {
			ClassData ownerCls = this.hierarchy.getClass(this.loader, owner, null);
			if (ownerCls.signature != null) {
				Signature ownerSig = Signature.parse(ownerCls.signature);
				resolve(ownerCls, variables, resolved, ownerSig);
			}
		}
	}

	private void resolve(ClassData cls, Set<String> variables, Map<String, FormalTypeParameterSymbol> resolved, Signature sig) {
		resolveVariables(resolved, variables, sig);
		variables.addAll(findVariables(sig));
		for (String key : resolved.keySet()) {
			variables.remove(key);
		}

		if (!variables.isEmpty()) {
			ClassData outerCls = this.hierarchy.getOuterClass(this.loader, cls.name, null);
			if (outerCls != null) {
				if (outerCls.signature != null) {
					Signature outerSig = Signature.parse(outerCls.signature);
					resolve(outerCls, variables, resolved, outerSig);
				}
			}
		}
	}

	private void resolveVariables(Map<String, FormalTypeParameterSymbol> resolved, Set<String> variables, Signature sig) {
		for (FormalTypeParameterSymbol param : sig.formalTypeParameters) {
			if (variables.contains(param.getName()) && !resolved.containsKey(param.getName())) {
				resolved.put(param.getName(), param);
			}
		}
	}

	private Set<String> findVariables(Signature sig) {
		Set<String> variables = new HashSet<>();
		sig.traverseDFS(symbol -> {
			if (symbol instanceof TypeVariableSymbol) {
				variables.add(symbol.getName());
			}
		});
		return variables;
	}
}
