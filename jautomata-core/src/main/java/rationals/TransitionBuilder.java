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

/**
 * A class for step-by-step creation of transitions. A TransitionBuilder can be
 * used to add expesiveness to transition creation withing automaton.
 * 
 * @author nono
 * 
 */
public class TransitionBuilder implements
		Builder<Transition, TransitionBuilder> {

	private State start;

	private Automaton<Transition, TransitionBuilder> automaton;

	protected Object label;

	/**
	 * Creates a transition builder for given automaton.
	 * 
	 * @param state
	 *            the starting state of transition.
	 * @param automaton
	 *            the automaton where transition will be added.
	 */
	public TransitionBuilder(State state,
			Automaton<Transition, TransitionBuilder> automaton) {
		this.start = state;
		this.automaton = automaton;
	}

	public TransitionBuilder() {
	}

	/**
	 * Sets the label of the transition.
	 * 
	 * @param label
	 * @return this transition builder.
	 */
	public TransitionBuilder on(Object label) {
		this.label = label;
		return this;
	}

	/**
	 * Sets the end state and terminates transition construction. This method
	 * effectively adds the transition to the automaton.
	 * 
	 * @param o
	 *            the label of the end state.
	 */
	public TransitionBuilder go(Object o) {
		State s = automaton.state(o);
		try {
			automaton.addTransition(new Transition(start, label, s));
		} catch (NoSuchStateException e) {
			assert false;
		}
		return this;
	}

	/**
	 * Adds a new transition in the automaton that loops on current label and
	 * from state.
	 * 
	 * @return
	 */
	public TransitionBuilder loop() {
		try {
			automaton.addTransition(new Transition(start, label, start));
		} catch (NoSuchStateException e) {
			assert false;
		}
		return this;
	}

	/**
	 * Resets this builder to another starting state. Note that the state is
	 * created if needed.
	 * 
	 * @param label
	 *            the state to start from.
	 * @return this builder.
	 */
	public TransitionBuilder from(Object label) {
		this.start = automaton.state(label);
		this.label = null;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see rationals.Builder#build(java.lang.Object, rationals.Automaton)
	 */
	public TransitionBuilder build(State state,
			Automaton<Transition, TransitionBuilder> auto) {
		this.start = state;
		this.label = null;
		this.automaton = auto;
		return this;
	}

	public Transition build(State from, Object label, State to) {
		return new Transition(from, label, to);
	}

	public void setAutomaton(Automaton<Transition, TransitionBuilder> a) {
		this.automaton = a;
	}

}
