package tcb.pr0x79.signature;

import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureVisitor;
import tcb.pr0x79.signature.symbol.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class SignatureParserVisitor extends SignatureVisitor {
	private final List<FormalTypeParameterSymbol> formal = new ArrayList<>();
	private final List<TypeSymbol> params = new ArrayList<>();
	private final List<TypeSymbol> interfaces = new ArrayList<>();
	private final List<TypeSymbol> exceptions = new ArrayList<>();
	private TypeSymbol ret;
	private TypeSymbol superclass;

	private FormalTypeParameterSymbol typeParam;

	/**
	 * This signature visitor parses a signature into an ASM Tree API-like structure
	 *
	 * @param api
	 */
	public SignatureParserVisitor(int api) {
		super(api);
	}

	/**
	 * Returns all formal symbol parameters
	 *
	 * @return
	 */
	public List<FormalTypeParameterSymbol> getFormalTypeParameters() {
		return this.formal;
	}

	/**
	 * Returns all method parameter types
	 *
	 * @return
	 */
	public List<TypeSymbol> getParameterTypes() {
		return this.params;
	}

	/**
	 * Returns the method return symbol
	 *
	 * @return
	 */
	public TypeSymbol getReturnType() {
		return this.ret;
	}

	/**
	 * Returns all interfaces the class implements
	 *
	 * @return
	 */
	public List<TypeSymbol> getInterfaces() {
		return this.interfaces;
	}

	/**
	 * Returns all exceptions the method throws
	 *
	 * @return
	 */
	public List<TypeSymbol> getExceptions() {
		return this.exceptions;
	}

	/**
	 * Returns the superclass of the class
	 *
	 * @return
	 */
	public TypeSymbol getSuperclass() {
		return this.superclass;
	}

	@Override
	public void visitFormalTypeParameter(String name) {
		this.typeParam = new FormalTypeParameterSymbol();
		this.typeParam.name = name;
		this.formal.add(this.typeParam);
	}

	@Override
	public SignatureVisitor visitClassBound() {
		return new SignatureTypeVisitor(this.api, this.typeParam, false);
	}

	@Override
	public SignatureVisitor visitInterfaceBound() {
		return new SignatureTypeVisitor(this.api, this.typeParam, true);
	}

	@Override
	public SignatureVisitor visitParameterType() {
		return new SignatureTypeVisitor(this.api, this.params::add);
	}

	@Override
	public SignatureVisitor visitReturnType() {
		return new SignatureTypeVisitor(this.api, ret -> this.ret = ret);
	}

	@Override
	public SignatureVisitor visitSuperclass() {
		return new SignatureTypeVisitor(this.api, cls -> this.superclass = cls);
	}

	@Override
	public SignatureVisitor visitInterface() {
		return new SignatureTypeVisitor(this.api, this.interfaces::add);
	}

	@Override
	public SignatureVisitor visitExceptionType() {
		return new SignatureTypeVisitor(this.api, this.exceptions::add);
	}


	public static class SignatureTypeVisitor extends SignatureVisitor {
		private Consumer<TypeSymbol> types;
		private TypeArgSymbol argSymbol;
		private TypeClassSymbol typeSymbol;

		private FormalTypeParameterSymbol paramSymbol;
		private boolean paramIface;


		private boolean array;

		/**
		 * Creates a signature symbol visitor to parse any signature types
		 * except formal symbol parameters or symbol wildcards
		 *
		 * @param api   ASM API version
		 * @param types Accepts new symbol before it is being visited
		 */
		SignatureTypeVisitor(int api, Consumer<TypeSymbol> types) {
			super(api);
			this.types = types;
		}

		/**
		 * Creates a signature symbol visitor to parse a formal symbol parameter
		 *
		 * @param api    ASM API version
		 * @param symbol The formal symbol parameter that is being visited
		 * @param iface  Whether a class bound or interface bound is being visited
		 */
		SignatureTypeVisitor(int api, FormalTypeParameterSymbol symbol, boolean iface) {
			super(api);
			this.paramSymbol = symbol;
			this.paramIface = iface;
		}

		/**
		 * Creates a signature symbol visitor to parse a symbol argument
		 *
		 * @param api    ASM API version
		 * @param symbol The symbol arg that is being visited
		 */
		private SignatureTypeVisitor(int api, TypeArgSymbol symbol) {
			super(api);
			this.argSymbol = symbol;
		}

		@Override
		public SignatureVisitor visitArrayType() {
			this.array = true;
			return this;
		}

		@Override
		public void visitTypeVariable(String name) {
			TypeVariableSymbol var = new TypeVariableSymbol(name, this.array);
			this.array = false;
			if (this.types != null) this.types.accept(var);
			if (this.argSymbol != null) this.argSymbol.setSymbol(var);
			if (this.paramSymbol != null) this.paramSymbol.extendsType = var;
			//Type variables cannot have arguments, no need to propagate through this.typeSymbol
		}

		@Override
		public void visitClassType(String name) {
			TypeClassSymbol type = new TypeClassSymbol(Type.getObjectType(name), this.array, new ArrayList<>());
			this.array = false;
			this.typeSymbol = type;
			if (this.types != null) this.types.accept(type);
			if (this.argSymbol != null) this.argSymbol.setSymbol(type);
			if (this.paramSymbol != null) {
				if (this.paramIface) {
					this.paramSymbol.implementsTypes.add(this.typeSymbol);
				} else {
					this.paramSymbol.extendsType = this.typeSymbol;
				}
			}
		}

		@Override
		public void visitInnerClassType(String name) {
			//Append inner class name
			this.typeSymbol.setType(Type.getObjectType(this.typeSymbol.getType().getInternalName() + "$" + name));
		}

		@Override
		public void visitBaseType(char descriptor) {
			TypeClassSymbol type = new TypeClassSymbol(Type.getType(String.valueOf(descriptor)), this.array, new ArrayList<>());
			this.array = false;
			this.typeSymbol = type;
			if (this.types != null) this.types.accept(type);
			if (this.argSymbol != null) this.argSymbol.setSymbol(type);
		}

		@Override
		public void visitTypeArgument() {
			TypeArgSymbol arg = new TypeArgSymbol('*');
			this.typeSymbol.addArg(arg);
		}

		@Override
		public SignatureVisitor visitTypeArgument(char wildcard) {
			TypeArgSymbol arg = new TypeArgSymbol(wildcard);
			this.typeSymbol.addArg(arg);
			return new SignatureTypeVisitor(this.api, arg);
		}
	}
}