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
package rationals;

import java.util.Set;

import junit.framework.TestCase;
import rationals.transformations.TransformationsToolBox;

/**
 * @author nono
 * @version $Id: ToolboxTest.java 2 2006-08-24 14:41:48Z oqube $
 */
public class ToolboxTest extends TestCase {

    /**
     * Constructor for ToolboxTest.
     * @param arg0
     */
    public ToolboxTest(String arg0) {
        super(arg0);
    }

    public void testClosure1() throws NoSuchStateException{
        Automaton a = new Automaton();
        State s1 = a.addState(true,false);
        State s2 = a.addState(false,false);
        State s3 = a.addState(false,true);
        a.addTransition(new Transition(s1,null,s1));
        a.addTransition(new Transition(s1,"a",s2));
        a.addTransition(new Transition(s2,null,s3));
        a.addTransition(new Transition(s3,null,s1));
        a.addTransition(new Transition(s3,"b",s3));
        Set st = a.getStateFactory().stateSet();
        st.add(s2);
        Set r = TransformationsToolBox.epsilonClosure(st,a);
        assertTrue(r.contains(s1));
        assertTrue(r.contains(s3));
        assertTrue(r.contains(s2));
    }
}
