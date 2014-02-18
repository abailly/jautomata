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
package rationals.properties;

import junit.framework.TestCase;
import rationals.Automaton;
import rationals.NoSuchStateException;
import rationals.State;
import rationals.Transition;
import rationals.converters.ConverterException;
import rationals.converters.Expression;
import rationals.transformations.ToDFA;

/**
 * @author nono
 * @version $Id: IsDeterministicTest.java 2 2006-08-24 14:41:48Z oqube $
 */
public class IsDeterministicTest extends TestCase {

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * Constructor for IsDeterministicTest.
     * @param arg0
     */
    public IsDeterministicTest(String arg0) {
        super(arg0);
    }

    public void testTrue() throws ConverterException {
        Automaton a = new ToDFA().transform(new Expression().fromString("a(b+a*c)bc*"));
        assertTrue(new IsDeterministic().test(a));
    }
    
    public void testFalse1() throws NoSuchStateException {
        Automaton a = new Automaton();
        State s1 = a.addState(true,false);
        State s2 = a.addState(true,false);
        State s3 = a.addState(false,true);
        a.addTransition(new Transition(s1,"a",s2));
        a.addTransition(new Transition(s2,"b",s1));
        a.addTransition(new Transition(s1,"b",s3));
        a.addTransition(new Transition(s3,"a",s2));
        assertTrue(!new IsDeterministic().test(a));
        
    }

    public void testFalse2() throws NoSuchStateException {
        Automaton a = new Automaton();
        State s1 = a.addState(true,false);
        State s2 = a.addState(false,false);
        State s3 = a.addState(false,true);
        a.addTransition(new Transition(s1,"a",s2));
        a.addTransition(new Transition(s2,"b",s1));
        a.addTransition(new Transition(s2,"b",s3));
        a.addTransition(new Transition(s3,"a",s2));
        assertTrue(!new IsDeterministic().test(a));
    }
}
