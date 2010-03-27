/*______________________________________________________________________________
 * 
 * Copyright 2005 Arnaud Bailly - NORSYS/LIFL
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * (1) Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 * (2) Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in
 *     the documentation and/or other materials provided with the
 *     distribution.
 *
 * (3) The name of the author may not be used to endorse or promote
 *     products derived from this software without specific prior
 *     written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Created on 31 mars 2005
 *
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