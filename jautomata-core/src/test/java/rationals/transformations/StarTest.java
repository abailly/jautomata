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
package rationals.transformations;

import java.util.Arrays;

import junit.framework.TestCase;
import rationals.Automaton;
import rationals.State;
import rationals.Transition;
import rationals.properties.ContainsEpsilon;

/**
 * @author nono
 * @version $Id: StarTest.java 2 2006-08-24 14:41:48Z oqube $
 */
public class StarTest extends TestCase {

    private Automaton automaton;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        automaton = new Automaton();
        State s1 = automaton.addState(true,false);
        State s2 = automaton.addState(false,false);
        State s3 = automaton.addState(false,true);
        automaton.addTransition(new Transition(s1,"a",s2));
        automaton.addTransition(new Transition(s2,"b",s3));        
    }

    /**
     * Constructor for StarTest.
     * @param arg0
     */
    public StarTest(String arg0) {
        super(arg0);
    }

    public void test() {
        Star star = new Star();
        Automaton b = star.transform(automaton);
        assertTrue(new ContainsEpsilon().test(b));
        Object[] word = new Object[]{"a","b","a","b"};
        Object[] word1 = new Object[]{"a","b","a","b","a"};
        assertTrue(b.accept(Arrays.asList(word)));
        assertTrue(!b.accept(Arrays.asList(word1)));
    }
    
}
