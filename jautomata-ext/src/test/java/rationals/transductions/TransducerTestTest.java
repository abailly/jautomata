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
 * Created on 30 mars 2005
 *
 */
package rationals.transductions;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

import junit.framework.TestCase;
import rationals.NoSuchStateException;
import rationals.State;
import rationals.Transition;
import rationals.converters.ToRExpression;
import rationals.transductions.testing.UTestGenerator;
import rationals.transductions.testing.WTestGenerator;

/**
 * @author nono
 * @version $Id: TransducerTestTest.java 2 2006-08-24 14:41:48Z oqube $
 */
public class TransducerTestTest extends TestCase {

    private DeterministicTransducer t;

    /**
     * Constructor for TransducerTestTest.
     * 
     * @param arg0
     */
    public TransducerTestTest(String arg0) {
        super(arg0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        this.t = new DeterministicTransducer();
        State[] ss = new State[5];
        for (int i = 0; i < 5; i++)
            ss[i] = t.addState(i == 0 ? true : false, true);
        t.addTransition(new Transition(ss[0],new TransducerRelation( "b", null), ss[0]));
        t.addTransition(new Transition(ss[0],new TransducerRelation( "a", "x"), ss[3]));
        t.addTransition(new Transition(ss[1],new TransducerRelation( "b", "y"), ss[2]));
        t.addTransition(new Transition(ss[1],new TransducerRelation( "a", "y"), ss[4]));
        t.addTransition(new Transition(ss[2],new TransducerRelation( "b", "x"), ss[3]));
        t.addTransition(new Transition(ss[2],new TransducerRelation( "a", "x"), ss[1]));
        t.addTransition(new Transition(ss[3],new TransducerRelation( "b", "y"), ss[3]));
        t.addTransition(new Transition(ss[3],new TransducerRelation( "a", "x"), ss[4]));
        t.addTransition(new Transition(ss[4],new TransducerRelation( "b", "y"), ss[0]));
        t.addTransition(new Transition(ss[4],new TransducerRelation( "a", "y"), ss[2]));
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /*
     * Test method U
     */
    public void testMethodU() throws NoSuchStateException,
            TransductionException, FileNotFoundException, IOException {
        UTestGenerator tester = new UTestGenerator();
        State init = (State) t.initials().iterator().next();
        Set s = tester.testSuite(t);
        ToRExpression re = new ToRExpression();
        System.err.println("======================================");
        System.err.println("UIO Map = "+t.makeUIOSet());
        System.err.println("UIO Test Set");
        int sz = 0;
        for (Iterator iter = s.iterator(); iter.hasNext();) {
            Object[] l = (Object[]) iter.next();
            Object[] expect = t.image(init, l);
            sz += l.length;
            System.err.println("Input : " + Arrays.asList(l) + ", output : " + (expect!=null ? Arrays.asList(expect).toString() : "[]"));
        }
        System.err.println("\nTotal size = " + sz);
        System.err.println("======================================");
    }

    /*
     * Test method W with same number of state
     */
    public void testMethodW1() {
        WTestGenerator tester = new WTestGenerator(0);
        State init = (State) t.initials().iterator().next();
        Set s = tester.testSuite(t);
        ToRExpression re = new ToRExpression();
        System.err.println("======================================");
        System.err.println("W Set = "+ t.makeCharacterizingSet());
        System.err.println("W Test Set");
        int sz = 0;
        for (Iterator iter = s.iterator(); iter.hasNext();) {
            Object[] l = (Object[]) iter.next();
            Object[] expect = t.image(init, l);
            sz += l.length;
            System.err.println("Input : " + Arrays.asList(l) + ", output : " + (expect!=null ? Arrays.asList(expect).toString() : "[]"));
        }
        System.err.println("\nTotal size = " + sz);
        System.err.println("======================================");
    }

    /*
     * test method W with two more states
     */
    public void testMethodW2() {
        WTestGenerator tester = new WTestGenerator(2);
        State init = (State) t.initials().iterator().next();
        Set s = tester.testSuite(t);
        ToRExpression re = new ToRExpression();
        System.err.println("======================================");
        System.err.println("W Test Set");
        int sz = 0;
        for (Iterator iter = s.iterator(); iter.hasNext();) {
            Object[] l = (Object[]) iter.next();
            Object[] expect = t.image(init, l);
            sz += l.length;
            System.err.println("Input : " + Arrays.asList(l) + ", output : " + (expect!=null ? Arrays.asList(expect).toString() : "[]"));
        }
        System.err.println("\nTotal size = " + sz);
        System.err.println("======================================");

    }
}
