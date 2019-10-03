package tcb.pr0x79.accessor;

import tcb.pr0x79.Internal;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * This annotation must be added to all the parameters of a {@link Interceptor}
 * method. The parameters will have the value of the identified local variable
 * at the time of the call. Any changes made to the parameters in the method body
 * will also change the local variables of the intercepted method.
 */
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface LocalVar {
	/**
	 * The ID of the local variable instruction identifier that is responsible
	 * for identifying the local variable to be imported
	 *
	 * @return
	 */
	@Internal(id = "instruction_identifier") String instructionIdentifier();
}
