/*
 * (C) Copyright $YEAR Arnaud Bailly (arnaud.oqube@gmail.com),
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
package rationals.properties;

import junit.framework.TestCase;
import rationals.Automaton;
import rationals.State;
import rationals.Transition;

/**
 * @author nono
 * @version $Id: TraceEquivalenceTest.java 2 2006-08-24 14:41:48Z oqube $
 */
public class TraceEquivalenceTest extends TestCase {

    /**
     * Constructor for TraceEquivalenceTest.
     * 
     * @param arg0
     */
    public TraceEquivalenceTest(String arg0) {
        super(arg0);
    }

    /*
     * simple trace equivalence test
     */
    public void testTraceEq() throws Throwable {
        Automaton a = new Automaton();
        State a1 = a.addState(true, false);
        State a2 = a.addState(false, true);
        State a3 = a.addState(false, true);
        a.addTransition(new Transition(a1, "a", a2));
        a.addTransition(new Transition(a1, "a", a3));
        a.addTransition(new Transition(a2, "b", a3));
        a.addTransition(new Transition(a3, "b", a2));
        Automaton b = new Automaton();
        State b1 = b.addState(true, false);
        State b2 = b.addState(false, true);
        b.addTransition(new Transition(b1, "a", b2));
        b.addTransition(new Transition(b2, "b", b2));
        TraceEquivalence equiv = new TraceEquivalence();
        AreEquivalent eq = new AreEquivalent(equiv);
        assertTrue(eq.test(a, b));
    }

}
