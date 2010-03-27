/*
 * Copyright (c) 2007 - OQube / Arnaud Bailly This library is free software; you
 * can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 * 
 * Created 30 mai 07
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
public class IOTransitionBuilder implements
		Builder<IOTransition, IOTransitionBuilder> {

	private State from;

	private Automaton<IOTransition, IOTransitionBuilder> automaton;

	private Object label;

	public IOTransitionBuilder(State state,
			IOAutomaton<IOTransitionBuilder> automaton) {
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

	public IOTransitionBuilder build(State state,
			Automaton<IOTransition, IOTransitionBuilder> auto) {
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

	public void setAutomaton(Automaton<IOTransition, IOTransitionBuilder> a) {
		automaton = a;
	}

}
