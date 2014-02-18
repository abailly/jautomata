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

import java.util.Iterator;
import java.util.Set;

import junit.framework.TestCase;
import rationals.properties.isEmpty;
import rationals.transformations.EpsilonTransitionRemover;
import rationals.transformations.Reducer;

/**
 * @author nono
 * @version $Id: EpsilonRemoverTest.java 2 2006-08-24 14:41:48Z oqube $
 */
public class EpsilonRemoverTest extends TestCase {

    /**
     * Constructor for EspilonRemoverTest.
     * 
     * @param arg0
     */
    public EpsilonRemoverTest(String arg0) {
        super(arg0);
    }

    public void testEpsilon() throws NoSuchStateException {
        Automaton a = new Automaton();
        State s1 = a.addState(true, false);
        State s2 = a.addState(false, false);
        State s3 = a.addState(false, true);
        a.addTransition(new Transition(s1, null, s1));
        a.addTransition(new Transition(s1, "a", s2));
        a.addTransition(new Transition(s2, null, s3));
        a.addTransition(new Transition(s3, null, s1));
        a.addTransition(new Transition(s3, "b", s3));
        Automaton b = new EpsilonTransitionRemover().transform(a);
        assertTrue(!b.alphabet().contains(null));
        /* check there is no transition with null labels */
        Set s = b.delta();
        assertNoEpsilon(s);

        b = new Reducer().transform(b);
        System.err.println(b);
    }

    public void testEpsilon2() throws NoSuchStateException {
        Automaton a = new Automaton();
        State s1 = a.addState(true, false);
        State s2 = a.addState(false, false);
        State s3 = a.addState(false, true);
        a.addTransition(new Transition(s1, null, s2));
        a.addTransition(new Transition(s1, "a", s2));
        a.addTransition(new Transition(s2, "a", s3));
        a.addTransition(new Transition(s3, null, s2));
        Automaton b = new EpsilonTransitionRemover().transform(a);
        assertTrue(!b.alphabet().contains(null));
        /* check there is no transition with null labels */
        Set s = b.delta();
        assertNoEpsilon(s);

        b = new Reducer().transform(b);
        System.err.println(b);
    }

    public void testEpsilon3() throws NoSuchStateException {
        Automaton a = new Automaton();
        State s1 = a.addState(true, false);
        State s2 = a.addState(false, false);
        State s3 = a.addState(false, true);
        a.addTransition(new Transition(s1, "a", s2));
        a.addTransition(new Transition(s2, null, s3));
        Automaton b = new EpsilonTransitionRemover().transform(a);
        assertTrue(!b.alphabet().contains(null));
        /* check there is no transition with null labels */
        Set s = b.delta();
        assertNoEpsilon(s);
        b = new Reducer().transform(b);
        System.err.println(b);
    }

    
    /**
     * @param s
     */
    private void assertNoEpsilon(Set s) {
        for (Iterator i = s.iterator(); i.hasNext();) {
            Transition tr = (Transition) i.next();
            assertTrue("Transition " + tr + " labelled with epsilon", tr
                    .label() != null);
        }
    }
    
}
