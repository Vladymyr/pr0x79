package tcb.pr0x79.data;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import tcb.pr0x79.BytecodeInstrumentation;
import tcb.pr0x79.exception.InstrumentorException;
import tcb.pr0x79.exception.accessor.method.InvalidMethodModifierException;
import tcb.pr0x79.exception.accessor.method.InvalidReturnTypeException;
import tcb.pr0x79.mapping.identification.AnnotatedElementDescription;
import tcb.pr0x79.mapping.identification.type.ClassIdentifier;
import tcb.pr0x79.mapping.identification.type.FieldIdentifier;
import tcb.pr0x79.mapping.identification.type.MethodIdentifier;
import tcb.pr0x79.mapping.Mapper;
import tcb.pr0x79.mapping.MapperRegistry;
import tcb.pr0x79.signature.Signature;
import tcb.pr0x79.signature.symbol.TypeClassSymbol;
import tcb.pr0x79.signature.symbol.TypeSymbol;
import tcb.pr0x79.accessor.*;

import javax.lang.model.SourceVersion;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Stores the data for a registered class accessor and its
 * field and method accessors and field generators
 */
public final class ClassAccessorData {
	private final String identifierId;
	private final String accessorClass;
	private final ClassIdentifier classIdentifier;

	private final List<ElementAccessorData<MethodNode, MethodIdentifier>> methodAccessors = new ArrayList<>();
	private final List<ElementAccessorData<MethodNode, FieldIdentifier>> fieldAccessors = new ArrayList<>();
	private final List<ElementAccessorData<MethodNode, Type>> fieldGenerators = new ArrayList<>();
	private final List<MethodInterceptorData> methodInterceptors = new ArrayList<>();

	private String identified;

	public ClassAccessorData(String identifierId, MapperRegistry mapperRegistry, String accessorClass, ClassNode clsNode, ClassIdentifier classIdentifier, BytecodeInstrumentation instrumentor) {
		this.identifierId = identifierId;
		this.accessorClass = accessorClass;
		this.classIdentifier = classIdentifier;

		for (MethodNode method : clsNode.methods) {
			boolean isAccessorOrInterceptor = false;

			isAccessorOrInterceptor |= this.identifyMethodAccessor(method, mapperRegistry);
			isAccessorOrInterceptor |= this.identifyFieldAccessor(method, mapperRegistry);
			isAccessorOrInterceptor |= this.identifyFieldGenerator(method, mapperRegistry);
			isAccessorOrInterceptor |= this.identifyMethodInterceptor(method, mapperRegistry, clsNode.name, identifierId);

			if (!isAccessorOrInterceptor && (method.access & Opcodes.ACC_ABSTRACT) != 0 && (method.access & Opcodes.ACC_STATIC) == 0) {
				throw new InstrumentorException(String.format("Class accessor %s has an abstract method: %s", accessorClass, method.name + method.desc));
			}
		}
	}

	/**
	 * Returns the identified class
	 *
	 * @return
	 */
	public String getIdentifiedClass() {
		return this.identified;
	}

	public void setIdentifiedClass(String cls) {
		this.identified = cls;
	}

	/**
	 * Identifies and validates a method accessor
	 *
	 * @param method
	 * @param mapperRegistry
	 * @return
	 */
	private boolean identifyMethodAccessor(MethodNode method, MapperRegistry mapperRegistry) {
		String methodIdentifierId = BytecodeInstrumentation.getAnnotationValue(method.visibleAnnotations, MethodAccessor.class, BytecodeInstrumentation.getInternal(MethodAccessor.class, "method_identifier", Class::getDeclaredMethods).getName(), String.class, null, null);
		if (methodIdentifierId != null) {
			if ((method.access & Opcodes.ACC_ABSTRACT) == 0) {
				throw new InstrumentorException(String.format("Method accessor %s#%s is a default method", accessorClass, method.name + method.desc));
			}
			if ((method.access & Opcodes.ACC_STATIC) != 0) {
				throw new InstrumentorException(String.format("Method accessor %s#%s is a static method", accessorClass, method.name + method.desc));
			}
			MethodIdentifier methodIdentifier = mapperRegistry.getMethodIdentifier(methodIdentifierId, Mapper.SearchType.ACCESSOR);
			if (methodIdentifier == null) {
				throw new InstrumentorException(String.format("Method identifier %s[%s] is not mapped", accessorClass, methodIdentifierId));
			}
			this.methodAccessors.add(new ElementAccessorData<MethodNode, MethodIdentifier>(methodIdentifierId, method, methodIdentifier));
			return true;
		}
		return false;
	}

	/**
	 * Identifies and validates a method interceptor
	 *
	 * @param method
	 * @param mapperRegistry
	 * @param className
	 * @param classIdentifierId
	 * @return
	 */
	private boolean identifyMethodInterceptor(MethodNode method, MapperRegistry mapperRegistry, String className, String classIdentifierId) {
		AnnotationNode interceptorAnnotation = null;

		if (method.visibleAnnotations != null) {
			for (AnnotationNode annotation : method.visibleAnnotations) {
				if (annotation.desc.equals(Type.getDescriptor(Interceptor.class))) {
					interceptorAnnotation = annotation;
				}
			}
		}

		if (interceptorAnnotation != null) {
			if (Type.getReturnType(method.desc).getSort() != Type.VOID) {
				throw new InvalidReturnTypeException(String.format("Return symbol of method interceptor %s#%s is not void", className, method.name + method.desc), null, className, AnnotatedElementDescription.methodDescription(method.name, method.desc), void.class.getName(), Type.getReturnType(method.desc).getClassName());
			}

			if ((method.access & Opcodes.ACC_STATIC) != 0) {
				throw new InvalidMethodModifierException(String.format("Method interceptor %s#%s is a static method", className, method.name + method.desc), null, className, AnnotatedElementDescription.methodDescription(method.name, method.desc), Modifier.STATIC);
			}

			TypeClassSymbol contextSig = null;
			int contextParam = -1;
			List<LocalVarData> methodLocalVars = new ArrayList<>();
			Type[] params = Type.getArgumentTypes(method.desc);
			for (int i = 0; i < params.length; i++) {
				if (params[i].getClassName().equals(InterceptorContext.class.getName())) {
					if (contextParam >= 0) {
						throw new InstrumentorException(String.format("Method interceptor %s#%s has multiple InterceptorContext parameters", className, method.name + method.desc));
					}
					contextParam = i;

					if (method.signature != null) {
						Signature sig = Signature.parse(method.signature);
						for (TypeSymbol paramSig : sig.parameters) {
							if (!paramSig.isVariable() && paramSig.getAsClass().getType().getClassName().equals(InterceptorContext.class.getName())) {
								contextSig = paramSig.getAsClass();
							}
						}
					}

					continue;
				}
				AnnotationNode localVarAnnotation = null;
				if (method.visibleParameterAnnotations != null && i < method.visibleParameterAnnotations.length) {
					List<AnnotationNode> paramAnnotations = method.visibleParameterAnnotations[i];
					if (paramAnnotations != null) {
						for (AnnotationNode annotation : paramAnnotations) {
							if (annotation.desc.equals(Type.getDescriptor(LocalVar.class))) {
								localVarAnnotation = annotation;
								break;
							}
						}
					}
				}
				if (localVarAnnotation == null) {
					throw new InstrumentorException(String.format("Parameter %d for method interceptor %s#%s does not have an @LocalVar annotation and is not InterceptorContext", i, className, method.name + method.desc));
				}
				String instructionIdentifierId = BytecodeInstrumentation.getAnnotationValue(localVarAnnotation, BytecodeInstrumentation.getInternal(LocalVar.class, "instruction_identifier", Class::getDeclaredMethods).getName(), String.class);
				if (instructionIdentifierId == null) {
					throw new InstrumentorException(String.format("@LocalVar for parameter %d of method %s#%s has invalid arguments", i, className, method.name + method.desc));
				}
				LocalVarData localVar = new LocalVarData(method.name, method.desc, i, Type.getObjectType(className).getClassName(), instructionIdentifierId);
				methodLocalVars.add(localVar);
			}

			if (contextParam < 0) {
				throw new InstrumentorException(String.format("Method interceptor %s#%s has no InterceptorContext parameter", className, method.name + method.desc));
			}

			if (contextSig == null || contextSig.getArgs().isEmpty()) {
				throw new InstrumentorException(String.format("Method interceptor %s#%s InterceptorContext parameter has no argument", className, method.name + method.desc));
			}

			String methodIdentifierId = BytecodeInstrumentation.getAnnotationValue(interceptorAnnotation, BytecodeInstrumentation.getInternal(Interceptor.class, "method_identifier", Class::getDeclaredMethods).getName(), String.class);
			String instructionIdentifierId = BytecodeInstrumentation.getAnnotationValue(interceptorAnnotation, BytecodeInstrumentation.getInternal(Interceptor.class, "instruction_identifier", Class::getDeclaredMethods).getName(), String.class);
			List<?> exitInstructionIdentifierIdObjs = BytecodeInstrumentation.getAnnotationValue(interceptorAnnotation, BytecodeInstrumentation.getInternal(Interceptor.class, "exit_instruction_identifiers", Class::getDeclaredMethods).getName(), ArrayList.class);
			String[] exitInstructionIdentifierIds = new String[exitInstructionIdentifierIdObjs == null ? 0 : exitInstructionIdentifierIdObjs.size()];
			if (exitInstructionIdentifierIdObjs != null) {
				for (int i = 0; i < exitInstructionIdentifierIdObjs.size(); i++) {
					exitInstructionIdentifierIds[i] = (String) exitInstructionIdentifierIdObjs.get(i);
				}
			}

			if (methodIdentifierId == null || instructionIdentifierId == null) {
				throw new InstrumentorException(String.format("Method interceptor for method %s#%s has invalid arguments", className, method.name + method.desc));
			}

			boolean checkReturnTypeSignature = true;
			if (method.visibleParameterAnnotations != null && contextParam < method.visibleParameterAnnotations.length) {
				checkReturnTypeSignature = BytecodeInstrumentation.getAnnotationValue(method.visibleParameterAnnotations[contextParam], UncheckedSignature.class, BytecodeInstrumentation.getInternal(UncheckedSignature.class, "out", Class::getDeclaredMethods).getName(), Boolean.class, true, false);
			}

			MethodInterceptorData methodInterceptor = new MethodInterceptorData(
					classIdentifierId, methodIdentifierId, instructionIdentifierId,
					exitInstructionIdentifierIds, Type.getObjectType(className).getClassName(),
					method, methodLocalVars, contextParam,
					contextSig, checkReturnTypeSignature);
			methodInterceptor.initIdentifiers(mapperRegistry);
			return this.methodInterceptors.add(methodInterceptor);
		}

		return false;
	}

	/**
	 * Identifies and validates a field accessor
	 *
	 * @param method
	 * @param mapperRegistry
	 * @return
	 */
	private boolean identifyFieldAccessor(MethodNode method, MapperRegistry mapperRegistry) {
		String fieldIdentifierId = BytecodeInstrumentation.getAnnotationValue(method.visibleAnnotations, FieldAccessor.class, BytecodeInstrumentation.getInternal(FieldAccessor.class, "field_identifier", Class::getDeclaredMethods).getName(), String.class, null, null);
		if (fieldIdentifierId != null) {
			if ((method.access & Opcodes.ACC_ABSTRACT) == 0) {
				throw new InstrumentorException(String.format("Field accessor %s#%s is a default method", accessorClass, method.name + method.desc));
			}

			if ((method.access & Opcodes.ACC_STATIC) != 0) {
				throw new InstrumentorException(String.format("Field accessor %s#%s is a static method", accessorClass, method.name + method.desc));
			}

			if (method.exceptions.size() > 0) {
				throw new InstrumentorException(String.format("Field accessor %s#%s throws Exceptions", accessorClass, method.name + method.desc));
			}

			FieldIdentifier fieldIdentifier = mapperRegistry.getFieldIdentifier(fieldIdentifierId, Mapper.SearchType.ACCESSOR);
			if (fieldIdentifier == null) {
				throw new InstrumentorException(String.format("Field identifier %s#%s[%s] is not mapped", accessorClass, method.name + method.desc, fieldIdentifierId));
			}

			Type[] params = Type.getArgumentTypes(method.desc);
			Type returnType = Type.getReturnType(method.desc);
			if (params.length > 0) {
				if (params.length != 1) {
					throw new InstrumentorException(String.format("Field accessor (setter?) %s#%s does not have exactly one parameter", accessorClass, method.name + method.desc));
				}

				if (returnType.getSort() != Type.VOID && !returnType.getClassName().equals(accessorClass) && !returnType.getClassName().equals(params[0].getClassName())) {
					throw new InstrumentorException(String.format("Field accessor (setter?) %s#%s does not have return symbol void, %s or %s", accessorClass, method.name + method.desc, accessorClass, params[0].getClassName()));
				}

				this.fieldAccessors.add(new ElementAccessorData<>(fieldIdentifierId, true, method, fieldIdentifier));
			} else {
				this.fieldAccessors.add(new ElementAccessorData<>(fieldIdentifierId, false, method, fieldIdentifier));
			}
			return true;
		}
		return false;
	}

	/**
	 * Identifies and validates a field generator
	 *
	 * @param method
	 * @param mapperRegistry
	 * @return
	 */
	private boolean identifyFieldGenerator(MethodNode method, MapperRegistry mapperRegistry) {
		String fieldNameIdentifier = BytecodeInstrumentation.getAnnotationValue(method.visibleAnnotations, FieldGenerator.class, BytecodeInstrumentation.getInternal(FieldGenerator.class, "field_name_identifier", Class::getDeclaredMethods).getName(), String.class, null, null);
		if (fieldNameIdentifier != null) {
			if ((method.access & Opcodes.ACC_ABSTRACT) == 0) {
				throw new InstrumentorException(String.format("Field generator %s#%s is a default method", accessorClass, method.name + method.desc));
			}

			if ((method.access & Opcodes.ACC_STATIC) != 0) {
				throw new InstrumentorException(String.format("Field generator %s#%s is a static method", accessorClass, method.name + method.desc));
			}

			if (method.exceptions.size() > 0) {
				throw new InstrumentorException(String.format("Field generator %s#%s throws Exceptions", accessorClass, method.name + method.desc));
			}

			FieldIdentifier identifier = mapperRegistry.getFieldIdentifier(fieldNameIdentifier, Mapper.SearchType.NAME_GENERATOR);
			if (identifier == null) {
				throw new InstrumentorException(String.format("Field name identifier %s#%s[%s] is not mapped", accessorClass, method.name + method.desc, fieldNameIdentifier));
			}

			if (!identifier.isStatic()) {
				throw new InstrumentorException(String.format("Field name identifier %s#%s[%s] is not static", accessorClass, method.name + method.desc, fieldNameIdentifier));
			}

			Set<AnnotatedElementDescription<Field>> fieldDescriptions = identifier.getData();
			if (fieldDescriptions.size() == 0) {
				throw new InstrumentorException(String.format("Field name identifier %s#%s[%s] did not return any field", accessorClass, method.name + method.desc, fieldNameIdentifier));
			}

			if (fieldDescriptions.size() > 1) {
				throw new InstrumentorException(String.format("Field name identifier %s#%s[%s] returned more than one field", accessorClass, method.name + method.desc, fieldNameIdentifier));
			}

			String fieldName = fieldDescriptions.iterator().next().getName();
			if (!SourceVersion.isName(fieldName)) {
				throw new InstrumentorException(String.format("Field name identifier %s#%s[%s] returned an invalid field name: %s", accessorClass, method.name + method.desc, fieldNameIdentifier, fieldName));
			}

			Type[] params = Type.getArgumentTypes(method.desc);
			Type returnType = Type.getReturnType(method.desc);
			if (params.length > 0) {
				if (params.length != 1) {
					throw new InstrumentorException(String.format("Field generator (setter?) %s#%s does not have exactly one parameter", accessorClass, method.name + method.desc));
				}

				if (returnType.getSort() != Type.VOID && !returnType.getClassName().equals(accessorClass) && !returnType.getClassName().equals(params[0].getClassName())) {
					throw new InstrumentorException(String.format("Field generator (setter?) %s#%s does not have return symbol void, %s or %s", accessorClass, method.name + method.desc, accessorClass, params[0].getClassName()));
				}

				this.fieldGenerators.add(new ElementAccessorData<>(fieldName, true, method, params[0]));
			} else {
				this.fieldGenerators.add(new ElementAccessorData<>(fieldName, method, returnType));
			}
			return true;
		}
		return false;
	}

	/**
	 * Returns the ID of the class identifier
	 *
	 * @return
	 */
	public String getIdentifierId() {
		return this.identifierId;
	}

	/**
	 * Returns (non internal!) name of the accessor class
	 *
	 * @return
	 */
	public String getAccessorClass() {
		return this.accessorClass;
	}

	/**
	 * Returns the class identifier of the class to be instrumented
	 *
	 * @return
	 */
	public ClassIdentifier getClassIdentifier() {
		return this.classIdentifier;
	}

	/**
	 * Returns a list of all method accessors
	 *
	 * @return
	 */
	public List<ElementAccessorData<MethodNode, MethodIdentifier>> getMethodAccessors() {
		return Collections.unmodifiableList(this.methodAccessors);
	}

	/**
	 * Returns a list of all method interceptors
	 *
	 * @return
	 */
	public List<MethodInterceptorData> getMethodInterceptors() {
		return Collections.unmodifiableList(this.methodInterceptors);
	}

	/**
	 * Returns a list of all field accessors
	 *
	 * @return
	 */
	public List<ElementAccessorData<MethodNode, FieldIdentifier>> getFieldAccessors() {
		return Collections.unmodifiableList(this.fieldAccessors);
	}

	/**
	 * Returns a list of all field generators
	 *
	 * @return
	 */
	public List<ElementAccessorData<MethodNode, Type>> getFieldGenerators() {
		return Collections.unmodifiableList(this.fieldGenerators);
	}
}
