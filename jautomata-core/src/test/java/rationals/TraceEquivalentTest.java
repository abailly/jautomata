/*
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
package rationals;

import rationals.properties.TraceEquivalent;
import junit.framework.TestCase;

public class TraceEquivalentTest extends TestCase {

    /**
     * Constructor for TraceEquivalentTest.
     * @param arg0
     */
    public TraceEquivalentTest(String arg0) {
        super(arg0);
    }

   
    public void testTraceEquivalentAutomata() {

    }

    public void testNonTraceEquivalentAutomata() throws NoSuchStateException {
    	Automaton<String, Transition<String>, TransitionBuilder<String>> a1 = new Automaton<>();
    	State s10 = a1.addState(true, false);
    	State s11 = a1.addState(false, true);
    	State s12 = a1.addState(false, false);
    	a1.addTransition(new Transition<>(s10, "P", s11));
    	a1.addTransition(new Transition<>(s11, "Q", s12));
    	a1.addTransition(new Transition<>(s11, "S", s11));
    	a1.addTransition(new Transition<>(s12, "R", s11));
    	
    	Automaton<String, Transition<String>, TransitionBuilder<String>> a2 = new Automaton<>();
    	State s20 = a2.addState(true, false);
    	State s21 = a2.addState(false, true);
    	State s22 = a2.addState(false, false);
    	State s23 = a2.addState(false, false);
    	State s24 = a2.addState(false, false);
    	a2.addTransition(new Transition<>(s20, "P", s21));
    	a2.addTransition(new Transition<>(s21, "Q", s22));
    	a2.addTransition(new Transition<>(s23, "Q", s24));
    	a2.addTransition(new Transition<>(s22, "R", s23));
    	a2.addTransition(new Transition<>(s21, "S", s21));
    	a2.addTransition(new Transition<>(s23, "S", s21));
    	a2.addTransition(new Transition<>(s24, "R", s21));
    	

    	TraceEquivalent<String, Transition<String>, TransitionBuilder<String>> equiv = new TraceEquivalent<>();
        assertFalse(equiv.test(a1, a2));
    }
    
}
