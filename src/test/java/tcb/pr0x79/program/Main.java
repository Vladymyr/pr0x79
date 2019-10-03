package tcb.pr0x79.program;

import org.objectweb.asm.Type;
import tcb.pr0x79.program.SomeClassBody.SomeClass;
import tcb.pr0x79.program.SomeClassBody.SomeInnerInterface;

/*
 * This is the tcb.pr0x79.program that will be intercepted.
 * Compile as a jar with this class as Main class.
 * Run with -javaagent:agent.jar (-javaagent arg must be before -jar!)
 */
public class Main implements SomeInnerInterface {

	private SomeClass someClass;

	public Main() {
		this.init();
	}

	public static void main(String[] args) {
		System.out.println("Starting tcb.pr0x79.program!\n");

		new Main();
	}

	private void init() {
		this.someClass = new SomeClassBody().create("Hello World!");

		this.someClass.print("Testing input value");

		System.out.println("INNER CLASS: " + Type.getType(SomeClass.class).getInternalName());

		Type type = Type.getType(Integer.class);
		System.out.println("INTEGER SORT: " + type.getSort());
	}

	interface ArrItf {

	}

	public static class MainSubClass extends Main {

	}

	static class ArrTest implements ArrItf {

	}
}
