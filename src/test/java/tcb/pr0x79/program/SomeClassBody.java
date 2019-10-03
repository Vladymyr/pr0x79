package tcb.pr0x79.program;

import java.util.List;
import java.util.Map;

public class SomeClassBody extends SomeClassSignature {
	public SomeClass create(String val) {
		return new SomeClass(val);
	}

	public interface SomeInnerInterface {

	}

	public class SomeClass implements SomeInnerInterface {
		private String value;

		public SomeClass(String value) {
			this.value = value;
		}

		public <M extends SomeInnerInterface> Map<String, List<M>> print(String input) {
			System.out.println("Running SomeClass#print(String)");
			System.out.println("Input: " + input);
			System.out.println("Value: " + this.value);

			return null;
		}
	}
}
