package tcb.pr0x79.accessor;

import tcb.pr0x79.Internal;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * This annotation must be added to all {@link Accessor}s
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface ClassAccessor {
	/**
	 * The ID of the class identifier that is responsible
	 * for identifying the class
	 *
	 * @return
	 */
	@Internal(id = "class_identifier") String classIdentifier();
}
