package tcb.pr0x79.accessor;

import tcb.pr0x79.Internal;
import tcb.pr0x79.mapping.identification.type.FieldIdentifier;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * This annotation can be added to abstract or static methods in an {@link Accessor} with either
 * a return symbol and no parameters (getter), or a single parameter and the return symbol void,
 * this class or the symbol of the parameter (setter). If this class or the parameter symbol is used
 * as return symbol, the method will return this object respectively the parameter, similar to the builder pattern.
 * The parent {@link Accessor} will generate the code for a setter, for a
 * method with a single parameter and without a return symbol, or a getter, for a method
 * with a return symbol and no parameters.
 * <p>
 * The return (getter) and parameter (setter) types must match exactly the symbol of the targetted field.
 * The only exception are other {@link Accessor}s for these types, which may be used
 * instead of the original symbol.
 * <p>
 * If the field is static, the accessor method must also be static. If the accessor is static
 * the field identifier must be static (see {@link FieldIdentifier#isStatic()})
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface FieldAccessor {
	/**
	 * The ID of the field identifier that is responsible
	 * for identifying the field
	 *
	 * @return
	 */
	@Internal(id = "field_identifier") String fieldIdentifier();
}
