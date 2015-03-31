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
package rationals.ioautomata;

import org.hamcrest.Matcher;

import rationals.Automaton;
import rationals.Builder;
import rationals.NoSuchStateException;
import rationals.State;

/**
 * A special transition builder that builds IOTransition instances.
 * 
 * @author nono
 * 
 */
public class IOTransitionBuilder implements Builder<Object, IOTransition, IOTransitionBuilder> {

	private State from;

	private Automaton<Object, IOTransition, IOTransitionBuilder> automaton;

	private Object label;

	public IOTransitionBuilder(State state, IOAutomaton<IOTransition, IOTransitionBuilder> automaton) {
		this.from = state;
		this.automaton = automaton;
	}

	public IOTransitionBuilder() {
	}

	public IOTransitionBuilder receive(Object o) {
		this.label = new IOTransition.IOLetter(o, IOAlphabetType.INPUT);
		return this;
	}

	public IOTransitionBuilder receive(Matcher matcher) {
		this.label = new IOTransition.IOLetter(matcher, IOAlphabetType.INPUT);
		return this;
	}

	public IOTransitionBuilder send(Object o) {
		this.label = new IOTransition.IOLetter(o, IOAlphabetType.OUTPUT);
		return this;
	}

	public IOTransitionBuilder from(Object label) {
		this.from = automaton.state(label);
		this.label = null;
		return this;
	}

	public IOTransitionBuilder go(Object o) {
		State s = automaton.state(o);
		try {
			automaton.addTransition(new IOTransition(from, label, s));
		} catch (NoSuchStateException e) {
			assert false;
		}
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see rationals.TransitionBuilder#loop()
	 */
	public IOTransitionBuilder loop() {
		try {
			automaton.addTransition(new IOTransition(from, label, from));
		} catch (NoSuchStateException e) {
			assert false;
		}
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see rationals.TransitionBuilder#on(java.lang.Object)
	 */
	public IOTransitionBuilder on(Object fun) {
		this.label = new IOTransition.IOLetter(fun, IOAlphabetType.INTERNAL);
		return this;
	}

	public IOTransitionBuilder build(State state, Automaton<Object, IOTransition, IOTransitionBuilder> auto) {
		this.from = state;
		this.label = null;
		this.automaton = auto;
		return this;
	}

	/**
	 * Assume the lable is a letter.
	 */
	public IOTransition build(State from, Object label, State to) {
		return new IOTransition(from, label, to);
	}

	public void setAutomaton(Automaton<Object, IOTransition, IOTransitionBuilder> a) {
		automaton = a;
	}

}
