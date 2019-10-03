package tcb.pr0x79.proxy;

import tcb.pr0x79.Bootstrapper;

import java.lang.instrument.Instrumentation;

public class Agent {
	/*
	 * This is the java agent.
	 * Compile the code as jar.
	 * The full name of this class has to be specified in the MANIFEST.MF
	 * like this:
	 *
	 * Premain-Class: tcb.pr0x79.proxy.Agent
	 */

	public static void premain(String args, Instrumentation inst) {
		System.out.println("Agent premain called!\n");

		//The second parameter specifies all instrumentors.
		//It is important that you do _not_ load any of the instrumentor classes yourself before the Bootstrapper does
		Bootstrapper.initialize(inst, Instrumentor.class);
	}
}
