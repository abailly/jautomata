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
package rationals.transductions;

import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;
import rationals.NoSuchStateException;
import rationals.State;
import rationals.Transition;

/**
 * @author nono
 * @version $Id: DeterministicTransducerTest.java 2 2006-08-24 14:41:48Z oqube $
 */
public class DeterministicTransducerTest extends TestCase {

    /**
     * @param arg0
     */
    public DeterministicTransducerTest(String arg0) {
        super(arg0);
    }

    /*
     * example from "A new technique for generating protocol tests", Sabnani and
     * Dahbura, 1985
     */
    public void testUIO() throws NoSuchStateException, TransductionException {
        DeterministicTransducer t = new DeterministicTransducer();
        State[] ss = new State[7];
        ss[0] = t.addState(true, false);
        for (int i = 1; i < 7; i++)
            ss[i] = t.addState(false, true);
        t.addTransition(new Transition(ss[0], new TransducerRelation("a", "b"), ss[1]));
        t.addTransition(new Transition(ss[1], new TransducerRelation("c", "d"), ss[2]));
        t.addTransition(new Transition(ss[2], new TransducerRelation("z", "b"), ss[4]));
        t.addTransition(new Transition(ss[4], new TransducerRelation("a", "b"), ss[0]));
        t.addTransition(new Transition(ss[1], new TransducerRelation("a", "f"), ss[3]));
        t.addTransition(new Transition(ss[3], new TransducerRelation("z", "b"), ss[5]));
        t.addTransition(new Transition(ss[5], new TransducerRelation("x", "d"), ss[6]));
        t.addTransition(new Transition(ss[6], new TransducerRelation("a", "f"), ss[1]));
        Map m = t.makeUIOSet();
        System.err.println("UIO Set "+m);
        assertTrue(t.verifyUIO(m));
    }

    /*
     * example from "An optimization technique for protocole conformance test
     * generation based on UIO sequences and rural chinese postman tour", Aho,
     * Dahbura, Lee and Uyar, 1991
     */
    public void testUIO2() throws NoSuchStateException, TransductionException {
        DeterministicTransducer t = new DeterministicTransducer();
        State[] ss = new State[5];
        for (int i = 0; i < 5; i++)
            ss[i] = t.addState(i == 0 ? true : false, true);
        t.addTransition(new Transition(ss[0], new TransducerRelation("a", "x"), ss[0]));
        t.addTransition(new Transition(ss[0], new TransducerRelation("b", "x"), ss[1]));
        t.addTransition(new Transition(ss[0], new TransducerRelation("c", "y"), ss[3]));
        t.addTransition(new Transition(ss[1], new TransducerRelation("b", "y"), ss[2]));
        t.addTransition(new Transition(ss[1], new TransducerRelation("a", "x"), ss[4]));
        t.addTransition(new Transition(ss[2], new TransducerRelation("b", "x"), ss[4]));
        t.addTransition(new Transition(ss[2], new TransducerRelation("c", "y"), ss[4]));
        t.addTransition(new Transition(ss[3], new TransducerRelation("b", "x"), ss[2]));
        t.addTransition(new Transition(ss[3], new TransducerRelation("a", "x"), ss[4]));
        t.addTransition(new Transition(ss[4], new TransducerRelation("a", "z"), ss[3]));
        t.addTransition(new Transition(ss[4],new TransducerRelation( "c", "z"), ss[0]));
        Map m = t.makeUIOSet();
        assertTrue(t.verifyUIO(m));
    }

    /*
     * example from <p> " <em> Test selection based on finite state models </em> ",
     * Fujiwara, Bochmann, Khendek, Amalou and Ghedamsi, IEEE TSE 17(6) 1991
     */
    public void testW() throws NoSuchStateException {
        DeterministicTransducer t = new DeterministicTransducer();
        State[] ss = new State[3];
        for (int i = 0; i < 3; i++)
            ss[i] = t.addState(i == 0 ? true : false, true);
        t.addTransition(new Transition(ss[0], new TransducerRelation("a", "e"), ss[1]));
        t.addTransition(new Transition(ss[0], new TransducerRelation("c", "e"), ss[2]));
        t.addTransition(new Transition(ss[0], new TransducerRelation("b", "f"), ss[1]));
        t.addTransition(new Transition(ss[1], new TransducerRelation("a", "f"), ss[0]));
        t.addTransition(new Transition(ss[1], new TransducerRelation("b", "f"), ss[2]));
        t.addTransition(new Transition(ss[1], new TransducerRelation("c", "f"), ss[1]));
        t.addTransition(new Transition(ss[2], new TransducerRelation("a", "f"), ss[2]));
        t.addTransition(new Transition(ss[2], new TransducerRelation("b", "e"), ss[0]));
        t.addTransition(new Transition(ss[2], new TransducerRelation("c", "e"), ss[1]));
        Set s = t.makeCharacterizingSet();
        System.err.println("W Set "+s);
        assertTrue(t.verifyW(s));
    	}

    /*
     * example from <p> " <em> Formal Methods for protocol testing </em> ",
     * Sidhu and Leung, IEEE TSE 15(4) 1989
     *  
     */
    public void testW2() throws NoSuchStateException, TransductionException {
        DeterministicTransducer t = new DeterministicTransducer();
        State[] ss = new State[5];
        for (int i = 0; i < 5; i++)
            ss[i] = t.addState(i == 0 ? true : false, true);
        t.addTransition(new Transition(ss[0], new TransducerRelation("b", null), ss[0]));
        t.addTransition(new Transition(ss[0], new TransducerRelation("a", "x"), ss[3]));
        t.addTransition(new Transition(ss[1], new TransducerRelation("b", "y"), ss[2]));
        t.addTransition(new Transition(ss[1], new TransducerRelation("a", "y"), ss[4]));
        t.addTransition(new Transition(ss[2], new TransducerRelation("b", "x"), ss[3]));
        t.addTransition(new Transition(ss[2], new TransducerRelation("a", "x"), ss[1]));
        t.addTransition(new Transition(ss[3], new TransducerRelation("b", "y"), ss[3]));
        t.addTransition(new Transition(ss[3], new TransducerRelation("a", "x"), ss[4]));
        t.addTransition(new Transition(ss[4], new TransducerRelation("b", "y"), ss[0]));
        t.addTransition(new Transition(ss[4], new TransducerRelation("a", "y"), ss[2]));
        Set s = t.makeCharacterizingSet();
        System.err.println("W Set "+s);
        /* verify W */
        assertTrue(t.verifyW(s));
    }

}