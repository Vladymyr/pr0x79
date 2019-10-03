package tcb.pr0x79;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Used by internal methods for bytecode generation
 */
@Retention(RUNTIME)
@Target({FIELD, METHOD, CONSTRUCTOR})
public @interface Internal {

	String id();

}
