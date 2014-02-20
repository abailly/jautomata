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

/**
 * An interface for easier creation of automata.
 * 
 * @author nono
 * 
 */
public interface Builder<Tr extends Transition, T extends Builder<Tr, T>> {

	/**
	 * Factory method.
	 * 
	 * @param label
	 * @param auto
	 * @return
	 */
	T build(State label, Automaton<Tr, T> auto);

	/**
	 * Sets the label of the transition.
	 * 
	 * @param label
	 * @return this transition builder.
	 */
	T on(Object label);

	/**
	 * Sets the end state and terminates transition construction. This method
	 * effectively adds the transition to the automaton.
	 * 
	 * @param o
	 *            the label of the end state.
	 */
	T go(Object o);

	/**
	 * Adds a new transition in the automaton that loops on current label and
	 * from state.
	 * 
	 * @return
	 */
	T loop();

	/**
	 * Resets this builder to another starting state. Note that the state is
	 * created if needed.
	 * 
	 * @param label
	 *            the state to start from.
	 * @return this builder.
	 */
	T from(Object label);

	/**
	 * Build a transition according to the specific logic of this builder.
	 * 
	 * @param from
	 * @param lable
	 * @param to
	 * @return
	 */
	Tr build(State from, Object label, State to);

	void setAutomaton(Automaton<Tr, T> a);
}
