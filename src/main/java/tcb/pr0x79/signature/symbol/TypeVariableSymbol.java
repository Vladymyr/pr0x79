package tcb.pr0x79.signature.symbol;

import org.objectweb.asm.signature.SignatureVisitor;

import java.util.function.Consumer;

/**
 * Describes a variable symbol
 */
public final class TypeVariableSymbol extends TypeSymbol {
	private String name;
	private boolean array;

	public TypeVariableSymbol(String name, boolean array) {
		this.name = name;
		this.array = array;
	}

	/**
	 * Returns the name of the symbol variable
	 *
	 * @return
	 */
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public boolean isVariable() {
		return true;
	}

	@Override
	public boolean isArray() {
		return this.array;
	}

	@Override
	public String toString() {
		return this.getName() + (this.isArray() ? "[]" : "");
	}

	@Override
	public void accept(SignatureVisitor visitor) {
		if (this.isArray()) {
			visitor = visitor.visitArrayType();
		}
		visitor.visitTypeVariable(this.getName());
	}

	@Override
	public void traverseDFS(Consumer<SignatureSymbol> consumer) {
	}
}