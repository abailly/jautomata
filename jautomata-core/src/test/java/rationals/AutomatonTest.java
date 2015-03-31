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
package rationals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

/**
 * @version $Id: AutomatonTest.java 2 2006-08-24 14:41:48Z oqube $
 */
public class AutomatonTest extends TestCase {

    private Automaton<String, Transition<String>, TransitionBuilder<String>> automaton;
    private State[] ss;

    /**
     * Constructor for AutomatonTest.
     * @param arg0
     */
    public AutomatonTest(String arg0) {
        super(arg0);
    }

    protected void setUp() throws Exception {
        super.setUp();
        automaton = new Automaton<>();
        /* states */
        ss = new State[5];
        ss[0] = automaton.addState(true,false);
        ss[1] = automaton.addState(false,false);
        ss[2] = automaton.addState(false,false);
        ss[3] = automaton.addState(false,false);
        ss[4] = automaton.addState(false,true);
        /* transition */
        automaton.addTransition(new Transition<>(ss[0],"a",ss[0]));
        automaton.addTransition(new Transition<>(ss[0],"b",ss[1]));
        automaton.addTransition(new Transition<>(ss[1],"b",ss[0]));
        automaton.addTransition(new Transition<>(ss[1],"a",ss[2]));
        automaton.addTransition(new Transition<>(ss[2],"b",ss[3]));
        automaton.addTransition(new Transition<>(ss[2],"a",ss[1]));
        automaton.addTransition(new Transition<>(ss[3],"a",ss[2]));
        automaton.addTransition(new Transition<>(ss[3],"b",ss[4]));
        automaton.addTransition(new Transition<>(ss[4],"b",ss[0]));
        automaton.addTransition(new Transition<>(ss[4],"a",ss[4]));
    }
    
    public void testAddState() {
        State s = automaton.addState(false,false);
        assertTrue(automaton.states().contains(s));
        assertTrue(!automaton.initials().contains(s));
        assertTrue(!automaton.terminals().contains(s));
    }

    public void testAlphabet() throws NoSuchStateException {
        Set<String> alph = new HashSet<>();
        alph.add("a");
        alph.add("b");
        alph.add("c");
        automaton.addTransition(new Transition<>(ss[0],"c",ss[3]));
        assertTrue(automaton.alphabet().equals(alph));        
    }

    public void testStates() {
        State s = automaton.addState(false,false);
        assertTrue(automaton.states().contains(s) && automaton.states().size() == 6);
    }

    public void testInitials() {
        State s = automaton.addState(true,false);
        assertTrue(automaton.states().contains(s));
        assertTrue(automaton.initials().contains(s));
        assertTrue(!automaton.terminals().contains(s));
    }

    public void testTerminals() {
        State s = automaton.addState(false,true);
        assertTrue(automaton.states().contains(s));
        assertTrue(!automaton.initials().contains(s));
        assertTrue(automaton.terminals().contains(s));
    }

    /*
     * Class under test for Set accessibleStates()
     */
    public void testAccessibleStates() throws NoSuchStateException {
        State s5 = automaton.addState(false,false);
        State s6 = automaton.addState(false,false);
        automaton.addTransition(new Transition<>(ss[0],"c",s5));
        automaton.addTransition(new Transition<>(s5,"c",s6));
        automaton.addTransition(new Transition<>(s6,"a",s5));
        Set<State> acc = automaton.accessibleStates();
        assertTrue(acc.contains(s5) && acc.contains(s6));
    }

    /*
     * Class under test for Set coAccessibleStates()
     */
    public void testCoAccessibleStates() throws NoSuchStateException {
        State s5 = automaton.addState(false,false);
        State s6 = automaton.addState(false,false);
        automaton.addTransition(new Transition<>(s5,"c",ss[4]));
        automaton.addTransition(new Transition<>(s6,"c",s5));
        automaton.addTransition(new Transition<>(s5,"a",s6));
        Set<State> acc = automaton.coAccessibleStates();
        assertTrue(acc.contains(s5) && acc.contains(s6));
    }

    public void testAccessibleAndCoAccessibleStates() throws NoSuchStateException {
        Set<State> acc = automaton.accessibleAndCoAccessibleStates();
        State s5 = automaton.addState(false,false);
        State s6 = automaton.addState(false,false);
        automaton.addTransition(new Transition<>(ss[0],"c",s5));
        automaton.addTransition(new Transition<>(s5,"c",s6));
        automaton.addTransition(new Transition<>(s6,"a",s5));
        assertTrue(automaton.states().containsAll(acc));
        assertTrue(!acc.contains(s5) && !acc.contains(s6));
    }

    /*
     * Class under test for Set delta()
     */
    public void testDelta() {
        //TODO Implement delta().
    }

    public void testAddTransition() {
        //TODO Implement addTransition().
    }

    public void testAcceptDFA() throws NoSuchStateException {
		Automaton<String, Transition<String>, TransitionBuilder<String>> t = new Automaton<>();
		State s1 = t.addState(true, true);
		State s2 = t.addState(false, false);
		State s3 = t.addState(false, false);
		t.addTransition(new Transition<>(s1, "a", s2));
		t.addTransition(new Transition<>(s2, "b", s3));
		t.addTransition(new Transition<>(s3, "c", s1));
		// check accept words
		List<String> exp = Arrays.asList(new String[] {  "a", "b","c", "a", "b", "c" });
		assertTrue("Automaton does not accept 'abcabc'",t.accept(exp));
		exp = Arrays.asList(new String[] {  "a", "b","c", "b", "c" });
		assertTrue("Automaton does accept 'abcbc'",!t.accept(exp));
    }

    public void testAcceptNFA1() throws NoSuchStateException {
		Automaton<String, Transition<String>, TransitionBuilder<String>> t = new Automaton<>();
		State s1 = t.addState(true, true);
		State s2 = t.addState(false, false);
		State s3 = t.addState(false, false);
		State s4 = t.addState(false, false);
		t.addTransition(new Transition<>(s1, "a", s2));
		t.addTransition(new Transition<>(s2, "b", s3));
		t.addTransition(new Transition<>(s3, "c", s4));
		t.addTransition(new Transition<String>(s4, null, s1));
		// check accept words
		List<String> exp = Arrays.asList(new String[] {  "a", "b","c", "a", "b", "c" });
		assertTrue("Automaton does not accept 'abcabc'",t.accept(exp));
		exp = Arrays.asList(new String[] {  "a", "b","c", "b", "c" });
		assertTrue("Automaton does accept 'abcbc'",!t.accept(exp));
    }
}
