package tcb.pr0x79.signature;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;
import tcb.pr0x79.signature.symbol.FormalTypeParameterSymbol;
import tcb.pr0x79.signature.symbol.SignatureSymbol;
import tcb.pr0x79.signature.symbol.TypeSymbol;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Consumer;

/**
 * Describes a full signature
 */
public final class Signature {
	public final List<FormalTypeParameterSymbol> formalTypeParameters = new ArrayList<>();
	public final List<TypeSymbol> parameters = new ArrayList<>();
	public final List<TypeSymbol> interfaces = new ArrayList<>();
	public final List<TypeSymbol> exceptions = new ArrayList<>();
	public TypeSymbol returnType;
	public TypeSymbol superclass;

	/**
	 * Parses the specified signature
	 *
	 * @param signature
	 * @return
	 */
	public static Signature parse(String signature) {
		SignatureParserVisitor vis = new SignatureParserVisitor(Opcodes.ASM7);

		new SignatureReader(signature).accept(vis);

		Signature sig = new Signature();

		sig.formalTypeParameters.addAll(vis.getFormalTypeParameters());
		sig.parameters.addAll(vis.getParameterTypes());
		sig.returnType = vis.getReturnType();
		sig.exceptions.addAll(vis.getExceptions());
		sig.superclass = vis.getSuperclass();
		sig.interfaces.addAll(vis.getInterfaces());

		return sig;
	}

	/**
	 * Accepts the signature visitor
	 *
	 * @param visitor
	 */
	public void accept(SignatureVisitor visitor) {
		for (FormalTypeParameterSymbol formalType : this.formalTypeParameters) {
			formalType.accept(visitor);
		}
		for (TypeSymbol param : this.parameters) {
			param.accept(visitor.visitParameterType());
		}

		if (this.returnType != null) {
			this.returnType.accept(visitor.visitReturnType());
		}

		for (TypeSymbol excp : this.exceptions) {
			excp.accept(visitor.visitExceptionType());
		}

		if (this.superclass != null) {
			this.superclass.accept(visitor.visitSuperclass());
		}
		for (TypeSymbol itf : this.interfaces) {
			itf.accept(visitor.visitInterface());
		}
	}

	/**
	 * Traverses all symbols in depth first order
	 *
	 * @param consumer
	 */
	public void traverseDFS(Consumer<SignatureSymbol> consumer) {
		for (FormalTypeParameterSymbol formalType : this.formalTypeParameters) {
			consumer.accept(formalType);
			formalType.traverseDFS(consumer);
		}

		for (TypeSymbol param : this.parameters) {
			consumer.accept(param);
			param.traverseDFS(consumer);
		}

		if (this.returnType != null) {
			consumer.accept(this.returnType);
			this.returnType.traverseDFS(consumer);
		}

		for (TypeSymbol excp : this.exceptions) {
			consumer.accept(excp);
			excp.traverseDFS(consumer);
		}

		if (this.superclass != null) {
			consumer.accept(this.superclass);
			this.superclass.traverseDFS(consumer);
		}

		for (TypeSymbol itf : this.interfaces) {
			consumer.accept(itf);
			itf.traverseDFS(consumer);
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		boolean isClassSig = !this.interfaces.isEmpty() || this.superclass != null;

		if (isClassSig) {
			sb.append("Class");
		}

		if (!this.formalTypeParameters.isEmpty()) {
			sb.append("<");
			StringJoiner joiner = new StringJoiner(", ");
			for (FormalTypeParameterSymbol formalParam : this.formalTypeParameters) {
				joiner.add(formalParam.toString());
			}
			sb.append(joiner.toString());
			sb.append("> ");
		}

		if (isClassSig) {
			if (this.superclass != null) {
				sb.append("extends ");
				sb.append(this.superclass.toString());
			}
			if (!this.interfaces.isEmpty()) {
				sb.append("implements ");
				StringJoiner joiner = new StringJoiner(", ");
				for (TypeSymbol itf : this.interfaces) {
					joiner.add(itf.toString());
				}
				sb.append(joiner.toString());
			}
		} else {
			sb.append(this.returnType.toString());
			sb.append(" method(");
			StringJoiner joiner = new StringJoiner(", ");
			for (TypeSymbol param : this.parameters) {
				joiner.add(param.toString());
			}
			sb.append(joiner.toString());
			sb.append(")");
			if (!this.exceptions.isEmpty()) {
				sb.append(" throws ");
				joiner = new StringJoiner(", ");
				for (TypeSymbol excp : this.exceptions) {
					joiner.add(excp.toString());
				}
				sb.append(joiner.toString());
			}
		}

		return sb.toString();
	}
}