package tcb.pr0x79.mapping.identification;

import org.objectweb.asm.Type;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.BiPredicate;
import java.util.function.Function;

public final class AnnotatedElementDescription<T> {

	public final static Function<Class<?>, Field[]> DEFAULT_FIELD_COLLECTOR = Class::getDeclaredFields;
	public final static Function<Class<?>, Method[]> DEFAULT_METHOD_COLLECTOR = Class::getDeclaredMethods;

	private final static BiPredicate<AnnotatedElementDescription, Field> defaultFieldFilter = (description, field) -> field.getName().equals(description.name)
			&& Type.getDescriptor(field.getType()).equals(description.desc);
	private final static BiPredicate<AnnotatedElementDescription, Method> defaultMethodFilter = (description, method) -> method.getName().equals(description.name)
			&& Type.getMethodDescriptor(method).equals(description.desc);

	private final Function<Class<?>, T[]> collector;
	private final BiPredicate<AnnotatedElementDescription, T> filter;
	private final String name, desc;

	AnnotatedElementDescription(String name, String descriptor, Function<Class<?>, T[]> collector, BiPredicate<AnnotatedElementDescription, T> filter) {
		this.name = name;
		this.desc = descriptor;
		this.collector = collector;
		this.filter = filter;
	}

	public static AnnotatedElementDescription<Field> fieldDescription(String name, String descriptor) {
		return new AnnotatedElementDescription<>(name, descriptor, DEFAULT_FIELD_COLLECTOR, defaultFieldFilter);
	}

	public static AnnotatedElementDescription<Method> methodDescription(String name, String descriptor) {
		return new AnnotatedElementDescription<>(name, descriptor, DEFAULT_METHOD_COLLECTOR, defaultMethodFilter);
	}

	public String getName() {
		return this.name;
	}

	public String getDescriptor() {
		return this.desc;
	}

	public T reflect(Class<?> cls) {
		for (T element : this.collector.apply(cls)) {
			if (this.filter.test(this, element)) {
				return element;
			}
		}

		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((desc == null) ? 0 : desc.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (getClass() != obj.getClass()) {
			return false;
		}

		AnnotatedElementDescription other = (AnnotatedElementDescription) obj;
		if (desc == null) {
			if (other.desc != null) {
				return false;
			}
		} else if (!desc.equals(other.desc)) {
			return false;
		} else if (name == null) {
			return other.name == null;
		} else {
			return name.equals(other.name);
		}

		return false;
	}
}
