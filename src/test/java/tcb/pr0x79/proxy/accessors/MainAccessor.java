package tcb.pr0x79.proxy.accessors;

import tcb.pr0x79.accessor.*;

@ClassAccessor(classIdentifier = "Main")
public interface MainAccessor extends Accessor {

	/*
	 * This method intercepts Main's constructor at the first return instruction (or at the end of the method, if no return is used)
	 */
	@Interceptor(methodIdentifier = "Main_ctor", instructionIdentifier = "first_return")
	default void ctor(InterceptorContext<Void> context) {
		System.out.println("\n--------Interception--------");
		System.out.println("Main constructor intercepted!");

		System.out.println("Getting SomeClass tcb.pr0x79.proxy object:");
		SomeClassAccessor someClassProxy = this.getSomeClassObject();
		System.out.println(someClassProxy + "\n");

		System.out.println("Testing mixin method");
		someClassProxy.testMixinMethod();
		System.out.println();

		System.out.println("Testing generated field");
		System.out.println("Current value: " + someClassProxy.getGeneratedFieldValue());
		float newValue = 1.234F;
		System.out.println("Setting value to: " + newValue);
		someClassProxy.setGeneratedFieldValue(newValue);
		System.out.println("Current value: " + someClassProxy.getGeneratedFieldValue() + "\n");

		System.out.println("Getting SomeClass#value through tcb.pr0x79.proxy: " + someClassProxy.getValue() + "\n");

		System.out.println("Running SomeClass#print through tcb.pr0x79.proxy");
		someClassProxy.printAccessor("Testing print accessor");

		System.out.println("-----------------------------\n");
	}

	/*
	 * These two methods intercept before and after the SomeClass#print call in Main#init
	 */
	@Interceptor(methodIdentifier = "Main_init", instructionIdentifier = "before_init_print")
	default void interceptInitBeforePrint(InterceptorContext<Void> context) {
		System.out.println("--------Interception--------");
		System.out.println("Main#init before SomeClass#print call intercepted!");
		System.out.println("-----------------------------");
	}

	@Interceptor(methodIdentifier = "Main_init", instructionIdentifier = "after_init_print")
	default void interceptInitAfterPrint(InterceptorContext<Void> context) {
		System.out.println("\n--------Interception--------");
		System.out.println("Main#init after SomeClass#print call intercepted!");
		System.out.println("-----------------------------");
	}

	/*
	 * This method returns the value of the field Main#obj.
	 * Since we have an accessor for SomeClass called SomeClassAccessor,
	 * we can use SomeClassAccessor as return symbol.
	 * Simply using SomeClass as return symbol would also work
	 */
	@FieldAccessor(fieldIdentifier = "Main_someClass")
	SomeClassAccessor getSomeClassObject();
}
