package tcb.pr0x79.signature.symbol;


import org.objectweb.asm.signature.SignatureVisitor;

import java.util.function.Consumer;

/**
 * Generic symbol used in the signature
 */
public abstract class SignatureSymbol {

	/**
	 * The name of this symbol
	 *
	 * @return
	 */
	public abstract String getName();

	/**
	 * Accepts the signature visitor
	 *
	 * @param visitor
	 */
	public abstract void accept(SignatureVisitor visitor);

	/**
	 * Traverses all child symbols in depth first order
	 *
	 * @param consumer
	 */
	public abstract void traverseDFS(Consumer<SignatureSymbol> consumer);
}