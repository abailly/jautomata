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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;
import rationals.Automaton;
import rationals.NoSuchStateException;
import rationals.State;
import rationals.Transition;
import rationals.converters.ToRExpression;

/**
 * @author nono
 * @version $Id: TransducerTest.java 2 2006-08-24 14:41:48Z oqube $
 */
public class TransducerTest extends TestCase {

    /**
     * @param arg0
     */
    public TransducerTest(String arg0) {
        super(arg0);
        // TODO Auto-generated constructor stub
    }

    /*
     * compute transduction (xy)* -> (abc)*
     */
    public void testTransducer1() throws NoSuchStateException {
        Transducer t = new Transducer();
        State s1 = t.addState(true,true);
        State s2 = t.addState(false,false);
        State s3 = t.addState(false,false);
        State s4 = t.addState(false,false);
        t.addTransition(new Transition(s1,new TransducerRelation("x","a"),s2));
        t.addTransition(new Transition(s2,new TransducerRelation(null,"b"),s3));
        t.addTransition(new Transition(s3,new TransducerRelation("y","c"),s4));
        t.addTransition(new Transition(s4,new TransducerRelation(null,null),s1));
        /* recognize word */
        List l = new ArrayList();
        l.add("x");l.add("y");
        l.add("x");l.add("y");
        Automaton im = t.image(l);
        System.err.println(im);
        System.err.println(new ToRExpression().toString(im));       
    }
    
    /*
     * compute transduction (xy)* -> (ab*c)*
     */
    public void testTransducer2() throws NoSuchStateException {
        Transducer t = new Transducer();
        State s1 = t.addState(true,true);
        State s2 = t.addState(false,false);
        State s3 = t.addState(false,false);
        t.addTransition(new Transition(s1,new TransducerRelation("x","a"),s2));
        t.addTransition(new Transition(s2,new TransducerRelation(null,"b"),s2));
        t.addTransition(new Transition(s2,new TransducerRelation("y","c"),s3));
        t.addTransition(new Transition(s3,new TransducerRelation(null,null),s1));
        /* recognize word */
        List l = new ArrayList();
        l.add("x");l.add("y");
        l.add("x");l.add("y");
        Automaton img = t.image(l);
        System.err.println(img);
        System.err.println(new ToRExpression().toString(img));       
    }

    public void testTransitionCover() throws NoSuchStateException {
        Transducer t = new Transducer();
        State s1 = t.addState(true,true);
        State s2 = t.addState(false,false);
        State s3 = t.addState(false,false);
        t.addTransition(new Transition(s1,new TransducerRelation("x","a"),s2));
        t.addTransition(new Transition(s2,new TransducerRelation("y","b"),s2));
        t.addTransition(new Transition(s2,new TransducerRelation("y","c"),s3));
        t.addTransition(new Transition(s3,new TransducerRelation("x","b"),s1));
        Set s = t.makeTransitionCover();
        System.err.println(s);
    }
    
    
    
    /*
     * 
     * compute transduction x^n -> a^n if n even, b^n if n odd
     * from Berstel
     */
    public void testTransducer3() throws NoSuchStateException {
        Transducer t = new Transducer();
        State s1 = t.addState(true,true);
        State s2 = t.addState(false,false);
        State s3 = t.addState(true,false);
        State s4 = t.addState(false,true);
        State s5 = t.addState(false,false);
        State s6 = t.addState(false,true);
        t.addTransition(new Transition(s1,new TransducerRelation("x","a"),s2));
        t.addTransition(new Transition(s2,new TransducerRelation("x","a"),s1));
        t.addTransition(new Transition(s3,new TransducerRelation("x","b"),s4));
        t.addTransition(new Transition(s4,new TransducerRelation("x","b"),s5));
        t.addTransition(new Transition(s5,new TransducerRelation("x","b"),s4));
        /* recognize word */
        List l = new ArrayList();
        l.add("x");l.add("x");
        l.add("x");l.add("x");
        Automaton img = t.image(l);
        System.err.println(new ToRExpression().toString(img));       
        img = t.image(new String[]{"x","x","x"});
        System.err.println(new ToRExpression().toString(img));
    }
    
}
