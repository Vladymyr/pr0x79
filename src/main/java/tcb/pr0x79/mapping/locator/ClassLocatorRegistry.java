package tcb.pr0x79.mapping.locator;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import tcb.pr0x79.Bootstrapper;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.*;

public class ClassLocatorRegistry {
	private final Bootstrapper bootstrapper;

	private final Map<String, ClassLocator> classLocators = new TreeMap<>();
	private final Map<String, Reference<ClassNode>> jreClassCache = new WeakHashMap<>();

	public ClassLocatorRegistry(Bootstrapper bootstrapper) {
		this.bootstrapper = bootstrapper;

		this.registerClassLocator("default", (loader, internalClassName, flags) -> {
			String clsName = internalClassName + ".class";
			if (jreClassCache.containsKey(clsName)) {
				return jreClassCache.get(clsName).get();
			}

			try (InputStream stream = loader.getResourceAsStream(clsName)) {
				ClassNode cls = new ClassNode();
				new ClassReader(stream).accept(cls, flags);
				if (clsName.startsWith("java/")) {
					jreClassCache.put(clsName, new SoftReference<>(cls));
				}

				return cls;
			}
		});
	}

	/**
	 * Registers a class locator that maps an internal class name
	 * to a {@link ClassNode}.
	 *
	 * @param id      The ID of the locator
	 * @param locator The locator that maps an internal class name to a {@link ClassNode}
	 */
	public synchronized void registerClassLocator(String id, ClassLocator locator) {
		this.checkBootstrapperState();

		this.classLocators.put(id, locator);
	}

	/**
	 * Unregisters a class locator
	 *
	 * @param id The ID of the locator
	 * @return
	 */
	public synchronized ClassLocator unregisterClassLocator(String id) {
		this.checkBootstrapperState();

		return this.classLocators.remove(id);
	}

	/**
	 * Returns all registered class locator IDs
	 *
	 * @return
	 */
	public synchronized Set<String> getClassLocators() {
		return new HashSet<>(this.classLocators.keySet());
	}

	/**
	 * Returns a {@link ClassNode} of the specified class
	 *
	 * @param loader            The classloader
	 * @param internalClassName The internal name of the class
	 * @param flags             The {@link ClassReader} flags
	 * @return
	 */
	public synchronized ClassNode getClass(ClassLoader loader, String internalClassName, int flags) {
		for (ClassLocator locator : this.classLocators.values()) {
			try {
				ClassNode node = locator.locate(loader, internalClassName, flags);
				if (node != null) {
					return node;
				}
			} catch (IOException ignored) {
			}
		}

		return null;
	}

	/**
	 * Validates the boostrapper state and throws an exception if the bootstrapper is no longer initializing
	 */
	private void checkBootstrapperState() {
		if (!this.bootstrapper.isInitializing()) {
			throw new RuntimeException("Class locators must be (un-)registered during the bootstrap initialization");
		}
	}
}
