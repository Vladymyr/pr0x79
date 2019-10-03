package tcb.pr0x79.signature.symbol;

import org.objectweb.asm.signature.SignatureVisitor;

import java.util.function.Consumer;

/**
 * Describes a class symbol argument
 */
public final class TypeArgSymbol extends SignatureSymbol {
	private final char wildcard;
	private TypeSymbol symbol;

	public TypeArgSymbol(char wildcard) {
		this(wildcard, null);
	}

	public TypeArgSymbol(char wildcard, TypeSymbol symbol) {
		this.wildcard = wildcard;
		this.symbol = symbol;
	}

	@Override
	public String getName() {
		return String.valueOf(this.wildcard);
	}

	/**
	 * Returns the wildcard of the arg.
	 * Can be the following characters:
	 * <ul>
	 * <li>{@link SignatureVisitor#EXTENDS}</li>
	 * <li>{@link SignatureVisitor#SUPER}</li>
	 * <li>{@link SignatureVisitor#INSTANCEOF}</li>
	 * <li>*</li>
	 * </ul>
	 *
	 * @return
	 */
	public char getWildcard() {
		return this.wildcard;
	}

	/**
	 * Returns the symbol of the arg. Can be null.<p>
	 * Can be {@link TypeClassSymbol} or {@link TypeVariableSymbol}
	 *
	 * @return
	 */
	public TypeSymbol getSymbol() {
		return this.symbol;
	}

	public void setSymbol(TypeSymbol symbol) {
		this.symbol = symbol;
	}

	/**
	 * Whether this argument is equivalent to {@literal <?>}
	 *
	 * @return
	 */
	public boolean isAny() {
		return this.wildcard == '*';
	}

	/**
	 * Whether this argument is equivalent to {@literal <? extends }<i>symbol</>{@literal >}
	 *
	 * @return
	 */
	public boolean isExtends() {
		return this.wildcard == SignatureVisitor.EXTENDS;
	}

	/**
	 * Whether this argument is equivalent to {@literal <? super }<i>symbol</>{@literal >}
	 *
	 * @return
	 */
	public boolean isSuper() {
		return this.wildcard == SignatureVisitor.SUPER;
	}

	/**
	 * Whether this argument is equivalent to {@literal <}<i>symbol</>{@literal >}
	 *
	 * @return
	 */
	public boolean isSpecific() {
		return this.wildcard == SignatureVisitor.INSTANCEOF;
	}

	@Override
	public String toString() {
		switch (this.wildcard) {
			default:
			case '*':
				return "?";
			case SignatureVisitor.EXTENDS:
				return "? extends " + this.getSymbol().toString();
			case SignatureVisitor.SUPER:
				return "? super " + this.getSymbol().toString();
			case SignatureVisitor.INSTANCEOF:
				return this.getSymbol().toString();
		}
	}

	@Override
	public void accept(SignatureVisitor visitor) {
		if (this.isAny()) {
			visitor.visitTypeArgument();
		} else {
			this.getSymbol().accept(visitor.visitTypeArgument(this.wildcard));
		}
	}

	@Override
	public void traverseDFS(Consumer<SignatureSymbol> consumer) {
		if (this.symbol != null) {
			consumer.accept(this.symbol);
			this.symbol.traverseDFS(consumer);
		}
	}
}