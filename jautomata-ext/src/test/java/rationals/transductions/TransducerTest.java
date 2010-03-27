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
 * Created on 19 mars 2005
 *
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
