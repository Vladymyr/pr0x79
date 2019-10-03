package tcb.pr0x79.accessor;

import tcb.pr0x79.Internal;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * This annotation can be added to field accessors, method accessors and interceptor parameters
 * to disable signature compatibility checking.
 *
 * @see Interceptor
 * @see Accessor
 */
@Retention(RUNTIME)
@Target({METHOD, TYPE, PARAMETER})
public @interface UncheckedSignature {
	/**
	 * Whether signature compatibility should be checked for inputs (i.e. field getters and interceptor parameters)
	 *
	 * @return
	 */
	@Internal(id = "in") boolean in() default false;

	/**
	 * Whether signature compatibility should be checked for outputs (i.e. return types, field setters and interceptor parameters)
	 *
	 * @return
	 */
	@Internal(id = "out") boolean out() default false;
}
