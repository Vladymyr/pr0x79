package tcb.pr0x79.accessor;

import tcb.pr0x79.Internal;
import tcb.pr0x79.mapping.identification.type.MethodIdentifier;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * This annotation can be added to abstract methods in an {@link Accessor}.
 * The parent {@link Accessor} will generate the code for the invocation
 * of the proxied method.
 * <p>
 * The return and parameter types must match exactly the types of the method to be proxied.
 * The only exception are other {@link Accessor}s for these types, which may be used
 * instead of the original symbol.
 * <p>
 * If the method is static, the accessor method must also be static. If the accessor is static
 * the method identifier must be static (see {@link MethodIdentifier#isStatic()})
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface MethodAccessor {
	/**
	 * The ID of the method identifier that is responsible
	 * for identifying the method
	 *
	 * @return
	 */
	@Internal(id = "method_identifier") String methodIdentifier();
}
