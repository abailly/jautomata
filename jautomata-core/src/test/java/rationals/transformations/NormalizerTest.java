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
package rationals.transformations;

import java.util.Arrays;

import junit.framework.TestCase;
import rationals.Automaton;
import rationals.State;
import rationals.Transition;
import rationals.properties.ContainsEpsilon;

/**
 * @author nono
 * @version $Id: NormalizerTest.java 2 2006-08-24 14:41:48Z oqube $
 */
public class NormalizerTest extends TestCase {

    private Automaton automaton;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        automaton = new Automaton();
        State s1 = automaton.addState(true, true);
        State s2 = automaton.addState(false, false);
        State s3 = automaton.addState(false, true);
        automaton.addTransition(new Transition(s1, "c", s1));
        automaton.addTransition(new Transition(s1, "a", s2));
        automaton.addTransition(new Transition(s2, "b", s3));
        automaton.addTransition(new Transition(s3, "a", s2));
        automaton.addTransition(new Transition(s2, "b", s1));
    }

    /**
     * Constructor for NormalizerTest.
     * 
     * @param arg0
     */
    public NormalizerTest(String arg0) {
        super(arg0);
    }

    public void test1() {
        Normalizer norm = new Normalizer();
        Automaton b = norm.transform(automaton);
        assertTrue(new ContainsEpsilon().test(b));
        Object[] word = new Object[] { "a", "b", "a", "b" };
        assertTrue(b.accept(Arrays.asList(word)));
    }

    public void test2() {
        Normalizer norm = new Normalizer();
        Automaton b = norm.transform(automaton);
        assertTrue(new ContainsEpsilon().test(b));
        Object[] word3 = new Object[] { };
        assertTrue(b.accept(Arrays.asList(word3)));
    }
    public void test3() {
        Normalizer norm = new Normalizer();
        Automaton b = norm.transform(automaton);
        assertTrue(new ContainsEpsilon().test(b));
        Object[] word2 = new Object[] { "c","c","a", "b", "a", "b", "a", "b" };
        assertTrue(b.accept(Arrays.asList(word2)));
    }

    public void test4() {
        Normalizer norm = new Normalizer();
        Automaton b = norm.transform(automaton);
        assertTrue(new ContainsEpsilon().test(b));
        Object[] word1 = new Object[] { "a", "b", "a", "b", "a" };
        assertTrue(!b.accept(Arrays.asList(word1)));
    }

    public void test5() {
        Normalizer norm = new Normalizer();
        Automaton b = norm.transform(automaton);
        assertTrue(new ContainsEpsilon().test(b));
        Object[] word2 = new Object[] { "c","c","c"};
        assertTrue(b.accept(Arrays.asList(word2)));
    }
}
