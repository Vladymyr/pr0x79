package tcb.pr0x79.signature.symbol;

import org.objectweb.asm.signature.SignatureVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Consumer;

/**
 * Describes a formal symbol parameter
 */
public final class FormalTypeParameterSymbol extends SignatureSymbol {
	public String name;
	public TypeSymbol extendsType;
	public List<TypeClassSymbol> implementsTypes = new ArrayList<>();


	/**
	 * Returns the name of the symbol parameter
	 *
	 * @return
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns the symbol that this parameter must extend.
	 * Can be {@link TypeClassSymbol} or {@link TypeVariableSymbol}
	 *
	 * @return
	 */
	public TypeSymbol getExtends() {
		return this.extendsType;
	}

	/**
	 * Returns the symbols that this parameter must implement
	 *
	 * @return
	 */
	public List<TypeClassSymbol> getImplements() {
		return this.implementsTypes;
	}

	@Override
	public String toString() {
		List<TypeSymbol> allExtends = new ArrayList<>();
		if (this.getExtends() != null) {
			allExtends.add(this.getExtends());
		}
		allExtends.addAll(this.getImplements());
		StringBuilder sb = new StringBuilder();
		sb.append(this.name);
		if (!allExtends.isEmpty()) {
			StringJoiner joiner = new StringJoiner(" & ");
			for (TypeSymbol type : allExtends) {
				joiner.add(type.toString());
			}
			sb.append(" extends " + joiner.toString());
		}
		return sb.toString();
	}

	@Override
	public void accept(SignatureVisitor visitor) {
		visitor.visitFormalTypeParameter(this.name);
		if (this.extendsType != null) {
			this.extendsType.accept(visitor.visitClassBound());
		}
		for (TypeClassSymbol impl : this.implementsTypes) {
			impl.accept(visitor.visitInterfaceBound());
		}
	}

	@Override
	public void traverseDFS(Consumer<SignatureSymbol> consumer) {
		if (this.extendsType != null) {
			consumer.accept(this.extendsType);
			this.extendsType.traverseDFS(consumer);
		}
		for (TypeClassSymbol itf : this.implementsTypes) {
			consumer.accept(itf);
			itf.traverseDFS(consumer);
		}
	}
}