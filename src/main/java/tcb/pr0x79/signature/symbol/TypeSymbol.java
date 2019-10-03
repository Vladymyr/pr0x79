package tcb.pr0x79.signature.symbol;


/**
 * Generic symbol that describes a symbol
 */
public abstract class TypeSymbol extends SignatureSymbol {

	/**
	 * Whether this symbol symbol is a variable
	 *
	 * @return
	 */
	public abstract boolean isVariable();

	/**
	 * Whether this symbol symbol is an array
	 *
	 * @return
	 */
	public abstract boolean isArray();

	/**
	 * Casts this symbol to a variable symbol
	 *
	 * @return
	 * @see #isVariable()
	 */
	public TypeVariableSymbol getAsVariable() {
		return (TypeVariableSymbol) this;
	}

	/**
	 * Casts this symbol to a class symbol
	 *
	 * @return
	 * @see #isVariable()
	 */
	public TypeClassSymbol getAsClass() {
		return (TypeClassSymbol) this;
	}
}