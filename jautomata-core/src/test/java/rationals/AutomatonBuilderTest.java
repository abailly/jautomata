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
package rationals;

import junit.framework.TestCase;

import java.util.Arrays;
import java.util.Collections;

public class AutomatonBuilderTest extends TestCase {

	private Automaton<Transition, TransitionBuilder> a;

	public void testStateLabellingYieldUniqueStates() {
		Automaton a = new Automaton();
		State s = a.state("init");
		State s2 = a.state("init");
		assertSame("should be same object", s, s2);
		assertEquals("objects should be equals", s, s2);
	}

	public void testUpdateStateAfterCreationAndAddTransition() {
		a.state("init").setInitial(true);
		a.from("init").on("a").go("next");
		a.state("next").setTerminal(true);
		System.err.println(a);
		assertTrue("automaton should accept word 'a'", a.accept(Collections
				.<Object> singletonList("a")));
	}

	public void testAddSeveralTransitions() {
		a.from("init").on("a").go("next");
		a.from("next").on("b").go("other");
		a.from("other").on("c").go("next");
		a.state("next").setTerminal(true);
		a.state("init").setInitial(true);
		assertTrue("automaton should accept word 'abcbc'", a.accept(Arrays
				.<Object> asList(new Object[] { "a", "b", "c", "b", "c" })));
		assertTrue("automaton should not accept word 'ab'", !a.accept(Arrays
				.<Object> asList(new Object[] { "a", "b" })));
	}

	public void testAddSeveralTransitionsFromSameStateAndLoops() {
		a.from("init").on("a").go("next").on("b").go("other");
		a.from("next").on("c").loop().on("b").go("other");
		a.state("init").setInitial(true);
		a.state("other").setTerminal(true);
		assertTrue("automaton should accept word 'accb'", a.accept(Arrays
				.<Object> asList(new Object[] { "a", "c", "c", "b" })));
		assertTrue("automaton should not accept word 'a'", !a.accept(Arrays
				.<Object> asList(new Object[] { "a" })));
	}

	public void testRestartBuilderFromOtherState() {
		a.from("init").on("a").go("next").on("b").go("other").from("next").on(
				"c").loop().on("b").go("other");
		a.state("init").setInitial(true);
		a.state("other").setTerminal(true);
		assertTrue("automaton should accept word 'accb'", a.accept(Arrays
				.<Object> asList(new Object[] { "a", "c", "c", "b" })));
		assertTrue("automaton should not accept word 'a'", !a.accept(Arrays
				.<Object> asList(new Object[] { "a" })));
	}

	public void setUp() {
		a = new Automaton<Transition, TransitionBuilder>();
		a.setBuilder(new TransitionBuilder());
	}

}
