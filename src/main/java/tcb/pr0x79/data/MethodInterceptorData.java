package tcb.pr0x79.data;

import org.objectweb.asm.tree.MethodNode;
import tcb.pr0x79.Internal;
import tcb.pr0x79.mapping.Mapper;
import tcb.pr0x79.mapping.MapperRegistry;
import tcb.pr0x79.accessor.Accessor;
import tcb.pr0x79.exception.InstrumentorException;
import tcb.pr0x79.mapping.identification.AnnotatedElementDescription;
import tcb.pr0x79.mapping.identification.type.ClassIdentifier;
import tcb.pr0x79.mapping.identification.type.MethodIdentifier;
import tcb.pr0x79.mapping.identification.type.InstructionTypeIdentifier;
import tcb.pr0x79.signature.symbol.TypeClassSymbol;

import java.util.Collections;
import java.util.List;

public final class MethodInterceptorData {
	private final List<LocalVarData> localVars;
	private final String accessorClass, classIdentifierId, methodIdentifierId, instructionIdentifierId;
	private final String[] exitInstructionIdentifierIds;
	private final String interceptorMethod, interceptorMethodDesc, interceptorMethodSig;
	private final int contextParam;
	private final TypeClassSymbol contextSig;
	private final boolean checkReturnTypeSignature;
	private MethodIdentifier methodIdentifier;
	private InstructionTypeIdentifier instructionIdentifier;
	private InstructionTypeIdentifier[] exitInstructionIdentifiers;
	private ClassIdentifier classIdentifier;
	private String identifiedClass;
	private AnnotatedElementDescription identifiedMethod;

	MethodInterceptorData(String classIdentifierId, String methodIdentifierId, String instructionIdentifierId, String[] exitInstructionIdentifierIds,
	                      String accessorClass, MethodNode interceptorMethodNode, List<LocalVarData> localVars, int contextParam,
	                      TypeClassSymbol contextSig, boolean checkReturnTypeSignature) {
		this.classIdentifierId = classIdentifierId;
		this.methodIdentifierId = methodIdentifierId;
		this.accessorClass = accessorClass;
		this.instructionIdentifierId = instructionIdentifierId;
		this.exitInstructionIdentifierIds = exitInstructionIdentifierIds;
		this.interceptorMethod = interceptorMethodNode.name;
		this.interceptorMethodDesc = interceptorMethodNode.desc;
		this.interceptorMethodSig = interceptorMethodNode.signature;
		this.localVars = localVars;
		this.contextParam = contextParam;
		this.contextSig = contextSig;
		this.checkReturnTypeSignature = checkReturnTypeSignature;
	}

	/**
	 * Returns the identified class
	 *
	 * @return
	 */
	public String getIdentifiedClass() {
		return this.identifiedClass;
	}

	public void setIdentifiedClass(String cls) {
		this.identifiedClass = cls;
	}

	/**
	 * Returns whether the return symbol signature should be checked
	 *
	 * @return
	 */
	public boolean getCheckReturnTypeSignature() {
		return this.checkReturnTypeSignature;
	}

	/**
	 * Returns the identified method
	 *
	 * @return
	 */
	public AnnotatedElementDescription getIdentifiedMethod() {
		return this.identifiedMethod;
	}

	public void setIdentifiedMethod(AnnotatedElementDescription method) {
		this.identifiedMethod = method;
	}

	/**
	 * Returns the index of the context parameter
	 *
	 * @return
	 */
	public int getContextParameter() {
		return this.contextParam;
	}

	/**
	 * Returns signature of the context parameter (including the InterceptorContext symbol!)
	 *
	 * @return
	 */
	public TypeClassSymbol getContextSignature() {
		return this.contextSig;
	}

	/**
	 * Returns the list of local variables that are imported and exported
	 *
	 * @return
	 */
	public List<LocalVarData> getLocalVars() {
		return Collections.unmodifiableList(this.localVars);
	}

	/**
	 * Returns the (not internal!) name of the {@link Accessor} class
	 *
	 * @return
	 */
	public String getAccessorClass() {
		return this.accessorClass;
	}

	/**
	 * Returns the class identifier ID for the class with the method to be intercepted
	 *
	 * @return
	 */
	public String getClassIdentifierId() {
		return this.classIdentifierId;
	}

	/**
	 * Returns the class identifier for the class with the method to be intercepted
	 *
	 * @return
	 */
	public ClassIdentifier getClassIdentifier() {
		return this.classIdentifier;
	}

	/**
	 * Returns the method identifier ID for the method to be intercepted
	 *
	 * @return
	 */
	public String getMethodIdentifierId() {
		return this.methodIdentifierId;
	}

	/**
	 * Returns the method identifier for the method to be intercepted
	 *
	 * @return
	 */
	public MethodIdentifier getMethodIdentifier() {
		return this.methodIdentifier;
	}

	/**
	 * Returns the instruction identifier ID where the interception is inserted
	 *
	 * @return
	 */
	public String getInstructionIdentifierId() {
		return this.instructionIdentifierId;
	}

	/**
	 * Returns the instruction identifier where the interception is inserted
	 *
	 * @return
	 */
	public InstructionTypeIdentifier getInstructionIdentifier() {
		return this.instructionIdentifier;
	}

	/**
	 * Returns the exit instruction identifier IDs
	 *
	 * @return
	 */
	public String[] getExitInstructionIdentifierIds() {
		return this.exitInstructionIdentifierIds;
	}

	/**
	 * Returns the instruction identifiers where the interception will return
	 *
	 * @return
	 */
	public InstructionTypeIdentifier[] getExitInstructionIdentifiers() {
		return this.exitInstructionIdentifiers;
	}

	/**
	 * Returns the interceptor method name
	 *
	 * @return
	 */
	public String getInterceptorMethod() {
		return this.interceptorMethod;
	}

	/**
	 * Returns the interceptor method descriptor
	 *
	 * @return
	 */
	public String getInterceptorMethodDesc() {
		return this.interceptorMethodDesc;
	}

	/**
	 * Returns the interceptor method signature
	 *
	 * @return
	 */
	public String getInterceptorMethodSignature() {
		return this.interceptorMethodSig;
	}

	/**
	 * Initializes the identifiers
	 *
	 * @param mapperRegistry
	 */
	public void initIdentifiers(MapperRegistry mapperRegistry) {
		this.classIdentifier = mapperRegistry.getClassIdentifier(this.classIdentifierId, Mapper.SearchType.ACCESSOR);
		if (this.classIdentifier == null) {
			throw new InstrumentorException(String.format("Class identifier %s[%s] is not mapped", this.accessorClass, this.classIdentifierId));
		}

		this.methodIdentifier = mapperRegistry.getMethodIdentifier(this.methodIdentifierId, Mapper.SearchType.INTERCEPTOR);
		if (this.methodIdentifier == null) {
			throw new InstrumentorException(String.format("Method identifier %s#%s[%s] is not mapped", this.accessorClass, this.interceptorMethod + this.interceptorMethodDesc, this.methodIdentifierId));
		}

		this.instructionIdentifier = mapperRegistry.getInstructionIdentifier(this.instructionIdentifierId, Mapper.SearchType.INTERCEPTOR_ENTRY);
		if (this.instructionIdentifier == null) {
			throw new InstrumentorException(String.format("Instruction identifier %s#%s[%s] is not mapped", this.accessorClass, this.interceptorMethod + this.interceptorMethodDesc, this.instructionIdentifierId));
		}

		if (this.instructionIdentifier.getType() != InstructionTypeIdentifier.InstructionType.INSTRUCTION) {
			throw new InstrumentorException(String.format("Instruction identifier %s#%s[%s] is not of symbol INSTRUCTION", this.accessorClass, this.interceptorMethod + this.interceptorMethodDesc, this.instructionIdentifierId));
		}

		this.exitInstructionIdentifiers = new InstructionTypeIdentifier[this.getExitInstructionIdentifierIds().length];
		int i = 0;
		for (String exitInstructionIdentifierId : this.exitInstructionIdentifierIds) {
			this.exitInstructionIdentifiers[i] = mapperRegistry.getInstructionIdentifier(exitInstructionIdentifierId, Mapper.SearchType.INTERCEPTOR_EXIT);
			if (this.exitInstructionIdentifiers[i] == null) {
				throw new InstrumentorException(String.format("Exit instruction identifier %s#%s[%s] is not mapped", this.accessorClass, this.interceptorMethod + this.interceptorMethodDesc, exitInstructionIdentifierId));
			}
			if (this.exitInstructionIdentifiers[i].getType() != InstructionTypeIdentifier.InstructionType.INSTRUCTION) {
				throw new InstrumentorException(String.format("Exit instruction identifier %s#%s[%s] is not of symbol INSTRUCTION", this.accessorClass, this.interceptorMethod + this.interceptorMethodDesc, exitInstructionIdentifierId));
			}
			i++;
		}

		for (LocalVarData localVar : this.localVars) {
			localVar.initIdentifier(mapperRegistry);
		}
	}

	public static final class InterceptorContext implements tcb.pr0x79.accessor.InterceptorContext<Object> {
		private final Object[] params;
		private int exit;
		private Object returnVal;
		private boolean returning;
		private boolean exiting;

		@Internal(id = "ctor")
		public InterceptorContext(int params) {
			this.params = new Object[params];
		}

		@Override
		public void exitAt(int index) {
			this.exit = index;
			this.exiting = true;
		}

		@Override
		public void cancelExit() {
			this.exiting = false;
		}

		@Override
		public int getExit() {
			return this.exit;
		}

		@Override
		public boolean isExiting() {
			return this.exiting;
		}

		@Override
		public void returnWith(Object obj) {
			this.returnVal = obj;
			this.returning = true;
		}

		@Override
		public void cancelReturn() {
			this.returning = false;
		}

		@Override
		public Object getReturn() {
			return this.returnVal;
		}

		@Override
		public boolean isReturning() {
			return this.returning;
		}

		@Override
		public Object[] getLocalVariables() {
			return this.params;
		}
	}
}
