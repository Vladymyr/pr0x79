package tcb.pr0x79.signature.symbol;

import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureVisitor;

import java.util.List;
import java.util.StringJoiner;
import java.util.function.Consumer;

/**
 * Describes a class symbol
 */
public final class TypeClassSymbol extends TypeSymbol {
	private final boolean array;
	private final List<TypeArgSymbol> args;
	private Type type;


	public TypeClassSymbol(Type type, boolean array, List<TypeArgSymbol> args) {
		this.type = type;
		this.array = array;
		this.args = args;
	}

	/**
	 * Returns the symbol of the symbol
	 *
	 * @return
	 */
	public Type getType() {
		return this.type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	@Override
	public String getName() {
		return this.type.getInternalName();
	}

	/**
	 * Returns the arguments of this symbol
	 *
	 * @return
	 */
	public List<TypeArgSymbol> getArgs() {
		return this.args;
	}

	public boolean addArg(TypeArgSymbol arg) {
		return this.args.add(arg);
	}

	@Override
	public boolean isVariable() {
		return false;
	}

	@Override
	public boolean isArray() {
		return this.array;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.type.getClassName());
		if (!this.args.isEmpty()) {
			sb.append("<");
			StringJoiner joiner = new StringJoiner(", ");
			for (TypeArgSymbol arg : this.args) {
				joiner.add(arg.toString());
			}
			sb.append(joiner.toString());
			sb.append(">");
		}
		if (this.isArray()) {
			sb.append("[]");
		}
		return sb.toString();
	}

	@Override
	public void accept(SignatureVisitor visitor) {
		String[] classes = this.getName().split("$"); //TODO Is this valid?
		if (this.isArray()) {
			visitor = visitor.visitArrayType();
		}

		if (this.type.getSort() == Type.OBJECT) {
			visitor.visitClassType(classes[0]);
			for (int i = 1; i < classes.length; i++) {
				visitor.visitInnerClassType(classes[i]);
			}
			for (TypeArgSymbol arg : this.args) {
				arg.accept(visitor);
			}
			visitor.visitEnd();
		} else {
			visitor.visitBaseType(this.type.getDescriptor().charAt(0));
		}
	}

	@Override
	public void traverseDFS(Consumer<SignatureSymbol> consumer) {
		for (TypeArgSymbol arg : this.args) {
			consumer.accept(arg);
			arg.traverseDFS(consumer);
		}
	}
}