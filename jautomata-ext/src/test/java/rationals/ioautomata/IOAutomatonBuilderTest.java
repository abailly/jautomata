/*
 * (C) Copyright 2005 Arnaud Bailly (arnaud.oqube@gmail.com),
 *     Yves Roos (yroos@lifl.fr) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the License);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an AS IS BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rationals.ioautomata;

import junit.framework.TestCase;
import org.hamcrest.BaseMatcher;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import rationals.ioautomata.testing.AsynchIOAutomatonSMAdapter;
import rationals.ioautomata.testing.SynchIOAutomatonSMAdapter;

import java.util.concurrent.LinkedBlockingQueue;


public class IOAutomatonBuilderTest extends TestCase {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields 
    //~ ----------------------------------------------------------------------------------------------------------------

    int i = 0;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    public void testInputAndOutputLabels() {
        IOAutomaton<IOTransition, IOTransitionBuilder> a = new IOAutomaton<IOTransition, IOTransitionBuilder>(new IOTransitionBuilder());
        a.from("init").receive("req").go("rcv").from("rcv").send("rep").go("init");
        a.state("init").setInitial(true);
        System.err.println(a);
        SynchIOAutomatonSMAdapter sm = new SynchIOAutomatonSMAdapter(a);
        new Thread(sm).start();
        sm.input("req");
        assertEquals("unexpected output", "rep", sm.output());
    }

    public void testInputMatcher() {
        IOAutomaton<IOTransition, IOTransitionBuilder> a = new IOAutomaton<IOTransition, IOTransitionBuilder>();
        a.setBuilder(new IOTransitionBuilder());
        a.from("init").receive(equalToIgnoringCase("Toto")).go("rcv").from("rcv").send("rep").go("init");
        a.state("init").setInitial(true);
        System.err.println(a);
        SynchIOAutomatonSMAdapter sm = new SynchIOAutomatonSMAdapter(a);
        new Thread(sm).start();
        sm.input("toTO");
        assertEquals("unexpected output", "rep", sm.output());
    }

    public void testCallsOnInternalTransitions() {
        IOAutomaton<IOTransition, IOTransitionBuilder> a = new IOAutomaton<IOTransition, IOTransitionBuilder>();
        final Exception e = new Exception("e");
        a.setBuilder(new IOTransitionBuilder());
        Function<Object, String> fun = new Function<Object, String>() {
            public String apply(Object a) throws Exception {
                if (i++ == 1)
                    throw e;
                return "toto";
            }
        };

        AsynchIOAutomatonSMAdapter sm = new AsynchIOAutomatonSMAdapter(a, new LinkedBlockingQueue(), new LinkedBlockingQueue());
        a.from("init").receive(1).go("rcv").from("rcv").on(fun).go("ret").from("ret").receive("toto").go("rcv").receive(CoreMatchers.instanceOf(Exception.class)).go("to").from("to").send(sm.lastInput).go("to");
        a.state("init").setInitial(true);
        System.err.println(a);
        sm.setInternalHandler(fun);
        new Thread(sm).start();
        sm.input(1);
        Object out;
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

    private Matcher<String> equalToIgnoringCase(final String string) {
        return new BaseMatcher<String>() {
            public boolean matches(Object o) {
                return ((String) o).equalsIgnoreCase(string);
            }

            public void describeTo(Description description) {
                description.appendText("should be equal to " + string + " ignoring case");
            }
        };
    }

}
