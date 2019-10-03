package tcb.pr0x79.proxy.accessors;

import tcb.pr0x79.program.SomeClassBody;
import tcb.pr0x79.accessor.*;

import java.util.List;
import java.util.Map;

@ClassAccessor(classIdentifier = "SomeClass")
public interface SomeClassAccessor extends Accessor {

	/*
	 * This method runs SomeClass#print(String)
	 */
	@MethodAccessor(methodIdentifier = "SomeClass_print")
	Map printAccessor(String input);

	/*
	 * This method intercepts SomeClass#print(String) right at the beginning of the method and
	 * sets the "input" parameter value to "intercepted input!"
	 */
	@Interceptor(methodIdentifier = "SomeClass_print", instructionIdentifier = "start",
			exitInstructionIdentifiers = {"first_return-1", "first_return-2"})
	default <M extends T, T extends SomeClassBody.SomeInnerInterface> void interceptPrint(@LocalVar(instructionIdentifier = "local_var_1") String input,
	                                                                                      InterceptorContext<Map<String, List<M>>> context) {
		System.out.println("\n--------Interception--------");
		System.out.println("SomeClass#print(String) intercepted!");
		System.out.println("Parameter \"input\" is: " + input);
		System.out.println("Changing parameter \"input\" to: \"intercepted input!\"");
		System.out.println("-----------------------------\n");

		//context.exitAt(0);

		//context.returnWith(null);
	}

	/*
	 * With @FieldAccessor field getters and setters can be generated.
	 * The names of these methods do not matter, but it is important that they either have one parameter and no return symbol (setter),
	 * or a return symbol and no parameter (getter).
	 *
	 * In this case, the field SomeClass#value is get/set
	 */
	@FieldAccessor(fieldIdentifier = "SomeClass_value")
	String getValue();

	@FieldAccessor(fieldIdentifier = "SomeClass_value")
	void setValue(String value);


	///// Mixins /////

	/*
	 * Mixin methods can easily be added by using default methods
	 */
	default void testMixinMethod() {
		System.out.println("Mixin method working!");
	}

	@FieldGenerator(fieldNameIdentifier = "generatedFieldName")
	float getGeneratedFieldValue();

	/*
	 * Mixin fields can be done with a getter and setter using @FieldGenerator.
	 * Again, the names of the methods do not matter.
	 */
	@FieldGenerator(fieldNameIdentifier = "generatedFieldName")
	void setGeneratedFieldValue(float value);
}
