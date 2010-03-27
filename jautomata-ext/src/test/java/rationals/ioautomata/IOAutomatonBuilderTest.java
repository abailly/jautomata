package rationals.ioautomata;

import java.util.concurrent.LinkedBlockingQueue;

import junit.framework.TestCase;

import org.hamcrest.Matchers;

import rationals.ioautomata.testing.AsynchIOAutomatonSMAdapter;
import rationals.ioautomata.testing.SynchIOAutomatonSMAdapter;

public class IOAutomatonBuilderTest extends TestCase {

	public void testInputAndOutputLabels() {
		IOAutomaton<IOTransitionBuilder> a = new IOAutomaton<IOTransitionBuilder>(
				new IOTransitionBuilder());
		a.from("init").receive("req").go("rcv").from("rcv").send("rep").go(
				"init");
		a.state("init").setInitial(true);
		System.err.println(a);
		SynchIOAutomatonSMAdapter sm = new SynchIOAutomatonSMAdapter(a);
		new Thread(sm).start();
		sm.input("req");
		assertEquals("unexpected output", "rep", sm.output());
	}

	public void testInputMatcher() {
		IOAutomaton<IOTransitionBuilder> a = new IOAutomaton<IOTransitionBuilder>();
		a.setBuilder(new IOTransitionBuilder());
		a.from("init").receive(Matchers.equalToIgnoringCase("Toto")).go("rcv")
				.from("rcv").send("rep").go("init");
		a.state("init").setInitial(true);
		System.err.println(a);
		SynchIOAutomatonSMAdapter sm = new SynchIOAutomatonSMAdapter(a);
		new Thread(sm).start();
		sm.input("toTO");
		assertEquals("unexpected output", "rep", sm.output());
	}

	int i = 0;

	public void testCallsOnInternalTransitions() {
		IOAutomaton<IOTransitionBuilder> a = new IOAutomaton<IOTransitionBuilder>();
		final Exception e = new Exception("e");
		a.setBuilder(new IOTransitionBuilder());
		Function<Object, String> fun = new Function<Object, String>() {
			public String apply(Object a) throws Exception {
				if (i++ == 1)
					throw e;
				return "toto";
			}
		};
		AsynchIOAutomatonSMAdapter sm = new AsynchIOAutomatonSMAdapter(a,
				new LinkedBlockingQueue(), new LinkedBlockingQueue());
		a.from("init").receive(1).go("rcv").from("rcv").on(fun).go("ret").from(
				"ret").receive("toto").go("rcv").receive(
				Matchers.instanceOf(Exception.class)).go("end").from("end")
				.send(sm.lastInput).go("end");
		a.state("init").setInitial(true);
		System.err.println(a);
		sm.setInternalHandler(fun);
		new Thread(sm).start();
		sm.input(1);
		Object out = null;
		do {
			try {
				synchronized (this) {
					wait(200);
				}
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			out = sm.output();
		} while (out == null);
		assertEquals("should have received exception", e, out);
		assertEquals("should have done more calls", 2, i);
	}

}
