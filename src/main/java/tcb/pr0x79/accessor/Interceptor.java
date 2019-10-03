package tcb.pr0x79.accessor;

import tcb.pr0x79.Internal;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * This annotation can be added to default methods in an {@link Accessor}.
 * The parent {@link Accessor} will generate the code for the invocation
 * of the proxied method. The return symbol of the method must be void, and
 * it must have a {@link InterceptorContext} parameter.
 *
 * @see {@link Accessor}, {@link InterceptorContext}, {@link LocalVar}
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface Interceptor {
	/**
	 * The IDs of the instruction identifiers that are responsible
	 * for identifying the exit instructions used by {@link InterceptorContext#exitAt(int)}
	 *
	 * @return
	 */
	@Internal(id = "exit_instruction_identifiers") String[] exitInstructionIdentifiers() default {};

	/**
	 * The ID of the method identifier that is responsible
	 * for identifying the method
	 *
	 * @return
	 */
	@Internal(id = "method_identifier") String methodIdentifier();

	/**
	 * The ID of the instruction identifier that is responsible
	 * for identifying the instruction where the interceptor is injected
	 *
	 * @return
	 */
	@Internal(id = "instruction_identifier") String instructionIdentifier();
}
