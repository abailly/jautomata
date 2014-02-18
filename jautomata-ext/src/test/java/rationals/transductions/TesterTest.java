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
package rationals.transductions;

import java.util.Set;

import junit.framework.TestCase;
import rationals.State;
import rationals.Transition;
import rationals.transductions.testing.TestFailure;
import rationals.transductions.testing.TransducerTester;
import rationals.transductions.testing.UTestGenerator;
import rationals.transductions.testing.WTestGenerator;

/**
 * @author nono
 * @version $Id: TesterTest.java 2 2006-08-24 14:41:48Z oqube $
 */
public class TesterTest extends TestCase {

    private DeterministicTransducer t;

    private DeterministicTransducer tTransErr;

    private DeterministicTransducer tOutErr;

    /**
     * Constructor for TesterTest.
     * 
     * @param arg0
     */
    public TesterTest(String arg0) {
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
        /* slightly different from t : transfer error */
        this.tTransErr = new DeterministicTransducer();
        ss = new State[5];
        for (int i = 0; i < 5; i++)
            ss[i] = tTransErr.addState(i == 0 ? true : false, true);
        tTransErr.addTransition(new Transition(ss[0],new TransducerRelation( "b", null), ss[0]));
        tTransErr.addTransition(new Transition(ss[0],new TransducerRelation( "a", "x"), ss[3]));
        tTransErr.addTransition(new Transition(ss[1],new TransducerRelation( "b", "y"), ss[2]));
        tTransErr.addTransition(new Transition(ss[1],new TransducerRelation( "a", "y"), ss[4]));
        tTransErr.addTransition(new Transition(ss[2],new TransducerRelation( "b", "x"), ss[3]));
        tTransErr.addTransition(new Transition(ss[2],new TransducerRelation( "a", "x"), ss[1]));
        tTransErr.addTransition(new Transition(ss[3],new TransducerRelation( "b", "y"), ss[2]));
        tTransErr.addTransition(new Transition(ss[3],new TransducerRelation( "a", "x"), ss[4]));
        tTransErr.addTransition(new Transition(ss[4],new TransducerRelation( "b", "y"), ss[0]));
        tTransErr.addTransition(new Transition(ss[4],new TransducerRelation( "a", "y"), ss[2]));
        /* output error */
        this.tOutErr = new DeterministicTransducer();
        ss = new State[5];
        for (int i = 0; i < 5; i++)
            ss[i] = tOutErr.addState(i == 0 ? true : false, true);
        tOutErr.addTransition(new Transition(ss[0],new TransducerRelation( "b", null), ss[0]));
        tOutErr.addTransition(new Transition(ss[0],new TransducerRelation( "a", "x"), ss[3]));
        tOutErr.addTransition(new Transition(ss[1],new TransducerRelation( "b", "y"), ss[2]));
        tOutErr.addTransition(new Transition(ss[1],new TransducerRelation( "a", "y"), ss[4]));
        tOutErr.addTransition(new Transition(ss[2],new TransducerRelation( "b", "x"), ss[3]));
        tOutErr.addTransition(new Transition(ss[2],new TransducerRelation( "a", "x"), ss[1]));
        tOutErr.addTransition(new Transition(ss[3],new TransducerRelation( "b", "y"), ss[2]));
        tOutErr.addTransition(new Transition(ss[3],new TransducerRelation( "a", "x"), ss[4]));
        tOutErr.addTransition(new Transition(ss[4],new TransducerRelation( "b", "x"), ss[0]));
        tOutErr.addTransition(new Transition(ss[4],new TransducerRelation( "a", "y"), ss[2]));
    }

    /*
     * Test that the same transducer passes its tests !
     */
    public void testEqualsUIO() throws TransductionException {
        UTestGenerator tgen = new UTestGenerator();
        State init = (State) t.initials().iterator().next();
        Set s = tgen.testSuite(t);
        TransducerTester tester = new TransducerTester();
        try {
            tester.testDeterministic(t, t, s);
        } catch (TestFailure e) {
            fail("Caught test failure " + e);
        }
    }

    public void testFailUIOTErr() throws TransductionException {
        UTestGenerator tgen = new UTestGenerator();
        State init = (State) t.initials().iterator().next();
        Set s = tgen.testSuite(t);
        TransducerTester tester = new TransducerTester();
        try {
            tester.testDeterministic(t, tTransErr, s);
            fail();
        } catch (TestFailure e) {
            System.out.println("Failure trace = " + e.getFailureSet());
        }
    }

    public void testFailUIOOutErr() throws TransductionException {
        UTestGenerator tgen = new UTestGenerator();
        State init = (State) t.initials().iterator().next();
        Set s = tgen.testSuite(t);
        TransducerTester tester = new TransducerTester();
        try {
            tester.testDeterministic(t, tOutErr, s);
            fail();
        } catch (TestFailure e) {
            System.out.println("Failure trace = " + e.getFailureSet());
        }
    }

    /*
     * Test that the same transducer passes its tests !
     */
    public void testEqualsW() throws TransductionException {
        WTestGenerator tgen = new WTestGenerator(0);
        State init = (State) t.initials().iterator().next();
        Set s = tgen.testSuite(t);
        TransducerTester tester = new TransducerTester();
        try {
            tester.testDeterministic(t, t, s);
        } catch (TestFailure e) {
            fail("Caught test failure " + e);
        }
    }

    public void testFailWTErr() throws TransductionException {
        WTestGenerator tgen = new WTestGenerator(0);
        State init = (State) t.initials().iterator().next();
        Set s = tgen.testSuite(t);
        TransducerTester tester = new TransducerTester();
        try {
            tester.testDeterministic(t, tTransErr, s);
            fail();
        } catch (TestFailure e) {
            System.out.println("Failure trace = " + e.getFailureSet());
        }
    }

    public void testFailWOutErr() throws TransductionException {
        WTestGenerator tgen = new WTestGenerator(0);
        State init = (State) t.initials().iterator().next();
        Set s = tgen.testSuite(t);
        TransducerTester tester = new TransducerTester();
        try {
            tester.testDeterministic(t, tOutErr, s);
            fail();
        } catch (TestFailure e) {
            System.out.println("Failure trace = " + e.getFailureSet());
        }
    }
    
    /*
     * Test that the same transducer passes its tests !
     */
    public void testEqualsW2() throws TransductionException {
        WTestGenerator tgen = new WTestGenerator(2);
        State init = (State) t.initials().iterator().next();
        Set s = tgen.testSuite(t);
        TransducerTester tester = new TransducerTester();
        try {
            tester.testDeterministic(t, t, s);
        } catch (TestFailure e) {
            fail("Caught test failure " + e);
        }
    }


}
