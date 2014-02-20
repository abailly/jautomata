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

import junit.framework.TestCase;
import rationals.Automaton;
import rationals.NoSuchStateException;
import rationals.State;
import rationals.Transition;
import rationals.converters.ConverterException;
import rationals.converters.Expression;
import rationals.converters.ToRExpression;
import rationals.transformations.Reducer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TransducerNivatTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void test01Language() throws NoSuchStateException, ConverterException {
		TransducerNivat t = new TransducerNivat();
		State s1 = t.addState(true, true);
		State s2 = t.addState(false, false);
		State s3 = t.addState(false, false);
		State s4 = t.addState(false, false);
		t.addTransition(new Transition(s1,new TransducerRelation( "x", "a"), s2));
		t.addTransition(new Transition(s2,new TransducerRelation( null, "b"), s3));
		t.addTransition(new Transition(s3,new TransducerRelation( "y", "c"), s4));
		t.addTransition(new Transition(s4,new TransducerRelation( null, null), s1));
		/* recognize word */
		Automaton a = new Expression().fromString("(xy)*");
		/* check */
		Automaton b = t.image(a);
		System.err.println(new ToRExpression().toString(new Reducer().transform(b)));
		List exp = Arrays.asList(new String[] {  "a", "b","c", "a", "b", "c" });
		assertTrue("Image does not recognizes 'abcabc'",b.accept(exp));
	}

	public void test02Word() throws NoSuchStateException {
		TransducerNivat t = new TransducerNivat();
		State s1 = t.addState(true, true);
		State s2 = t.addState(false, false);
		State s3 = t.addState(false, false);
		State s4 = t.addState(false, false);
		t.addTransition(new Transition(s1,new TransducerRelation( "x", "a"), s2));
		t.addTransition(new Transition(s2,new TransducerRelation( null, "b"), s3));
		t.addTransition(new Transition(s3,new TransducerRelation( "y", "c"), s4));
		t.addTransition(new Transition(s4,new TransducerRelation( null, null), s1));
		/* recognize language */
		List l = new ArrayList();
		l.add("x");
		l.add("y");
		l.add("x");
		l.add("y");
		/* check */
		Automaton b = t.image(l);
		System.err.println(new ToRExpression().toString(new Reducer().transform(b)));
		List exp = Arrays.asList(new String[] {  "a", "b","c", "a", "b", "c" });
		assertTrue("Image does not recognizes 'abcabc'",b.accept(exp));
	}
	
	public void test03Mixin() throws NoSuchStateException, FileNotFoundException, IOException {
		TransducerNivat t = new TransducerNivat();
		State s0 = t.addState(true, false);
		State s1 = t.addState(false,false);
		State s2 = t.addState(false, false);
		State s3 = t.addState(false, true);
		State s4 = t.addState(false, false);
		State s5 = t.addState(false, false);
		State s6 = t.addState(false, true);
		t.addTransition(new Transition(s0,new TransducerRelation( "?a", "?a"), s1));
		t.addTransition(new Transition(s1,new TransducerRelation( "?b", "?b"), s2));
		t.addTransition(new Transition(s2,new TransducerRelation( "!b", "!b"), s3));
		t.addTransition(new Transition(s5,new TransducerRelation( null, "!d"), s3));
		t.addTransition(new Transition(s3,new TransducerRelation( "!a", "!a"), s3));
		t.addTransition(new Transition(s2,new TransducerRelation( "<b>", "<b>"), s4));
		t.addTransition(new Transition(s4,new TransducerRelation( null, "?d"), s5));
		t.addTransition(new Transition(s5,new TransducerRelation( null,"<d>"), s1));
		t.addTransition(new Transition(s1,new TransducerRelation( "?a","?a"), s1));
		t.addTransition(new Transition(s1,new TransducerRelation( "<a>","<a>"), s1));
		t.addTransition(new Transition(s1,new TransducerRelation( "<b>","<b>"), s1));
		/* recognize word */
		Automaton l = new Automaton();
		s0 = l.addState(true,false);
		s1 = l.addState(false,false);
		s2 = l.addState(false,false);
		s3 = l.addState(false,false);
		s4 = l.addState(false,false);
		s5 = l.addState(false,false);
		s6 = l.addState(false,true);
		l.addTransition(new Transition(s0, "?a", s1));
		l.addTransition(new Transition(s1, "?b", s2));
		l.addTransition(new Transition(s2, "<b>", s3));
		l.addTransition(new Transition(s3, "<a>", s4));
		l.addTransition(new Transition(s4, "?a", s1));
		l.addTransition(new Transition(s2, "!b", s5));
		l.addTransition(new Transition(s5, "!a", s6));
		Automaton b = t.image(l);
		b= new Reducer().transform(b);
		System.err.println(new ToRExpression().toString(b));		
	}
}
