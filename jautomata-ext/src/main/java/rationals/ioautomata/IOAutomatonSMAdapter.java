/**
 *  Copyright (C) 2007 - OQube / Arnaud Bailly
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA

 Created 1 juin 07
 */
package rationals.ioautomata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hamcrest.Matcher;

import rationals.State;
import rationals.Transition;
import rationals.ioautomata.IOTransition.IOLetter;

/**
 * A basic implementation of an IOStateMachine that wraps an IOAutomaton. This
 * implementation of IOStateMachine has the following semantics:
 * <ul>
 * <li>A call to {@link #input(Object)} is synchronously executed in the
 * underlying automaton: It advances the state if at least one input matches and
 * and otherwise throws an {@link IllegalStateException},</li>
 * <li>All internal transitions available in current state are always fired. If
 * they are labelled by {@see Function} instances, the function is called with a
 * parameter that is the latest input available</li>
 * <li>A call to output returns the available output data if any,</li>
 * <li>If several output are available from the same state, one is fired at
 * random from the set of fireable output transitions.</li>
 * <li>A call to {@link #output()} is succesful if an output transition is
 * currently enabled. If not, it returns null.</li>
 * </ul>
 * 
 * @author nono
 * 
 */
public class IOAutomatonSMAdapter implements IOStateMachine {

	protected IOAutomaton<IOTransitionBuilder> automaton;

	protected Set<State> state;

	private boolean inputEnabled;

	private TransitionSelector selectOutput;

	private TransitionSelector selectInternal;

	protected Object savedInput;

	public final Function<IOAutomaton<IOTransitionBuilder>, Object> lastInput = new Function<IOAutomaton<IOTransitionBuilder>, Object>() {

		public Object apply(IOAutomaton<IOTransitionBuilder> message)
				throws Exception {
			assert message == automaton;
			return savedInput;
		}

		public String toString() {
			return "<last input>";
		}
	};

	public IOAutomatonSMAdapter(IOAutomaton<IOTransitionBuilder> a) {
		this.automaton = a;
		this.state = a.initials();
		this.selectOutput = new TransitionSelector(IOAlphabetType.OUTPUT);
		this.selectInternal = new TransitionSelector(IOAlphabetType.INTERNAL);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see rationals.ioautomata.IOStateMachine#availableOutput()
	 */
	public int availableOutput() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see rationals.ioautomata.IOStateMachine#input(java.lang.Object)
	 */
	public void input(Object o) {
		boolean ok = doInput(o);
		/* check input is OK, else error */
		if (!ok) {
			if (!inputEnabled)
				throw new IllegalStateException("No input possible in state :"
						+ state);
			else
				return;
		}
		savedInput = o;
		doInternal(o);
	}

	private void doInternal(Object o) {
		Set<IOTransition.IOLetter> lts = null;
		List<Object> collectedResults = new ArrayList<Object>();
		do {
			lts = selectInternal.selectAllFrom(automaton, state);
			Set<State> ns = automaton.getStateFactory().stateSet();
			for (IOLetter lt : lts)
				ns.addAll(fire(lt, o, collectedResults));
			// update state
			if (!lts.isEmpty())
				state = ns;
		} while (!lts.isEmpty());
		// inject inputs
		for (Object res : collectedResults)
			input(res);
	}

	private Collection<? extends State> fire(IOLetter lt, Object o,
			List<Object> res) {
		// first invoke functions
		Object ret = call(lt, o);
		if (ret != null)
			res.add(ret);
		// compute states
		return automaton.step(state, lt);
	}

	/**
	 * @param lt
	 * @param o
	 * @return
	 */
	private Object call(IOLetter lt, Object o) {
		Object ret = null;
		if (lt.label instanceof Function) {
			try {
				ret = ((Function) lt.label).apply(o);
			} catch (Exception e) {
				ret = e;
			}
		}
		return ret;
	}

	/**
	 * @param o
	 * @return
	 */
	private boolean doInput(Object o) {
		boolean ok = false;
		for (Transition tr : automaton.delta(state)) {
			IOTransition.IOLetter lt = (IOLetter) ((IOTransition) tr).label();
			if (lt.type == IOAlphabetType.INPUT
					&& IOSynchronization.inputMatchesLetter(o, lt)) {
				doTransition(tr.label());
				ok = true;
			}
		}
		return ok;
	}

	/**
	 * @param object
	 */
	private void doTransition(Object object) {
		state = automaton.step(state, object);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see rationals.ioautomata.IOStateMachine#isInputEnabled()
	 */
	public boolean isInputEnabled() {
		return inputEnabled;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see rationals.ioautomata.IOStateMachine#output()
	 */
	public Object output() {
		IOTransition.IOLetter lt = selectOutput.selectFrom(automaton, state);
		if (lt == null)
			if (!inputEnabled)
				throw new IllegalStateException("No available output in state:"
						+ state);
			else
				return null;
		Object ret = call(lt, automaton);
		doTransition(lt);
		doInternal(lt.label);
		return ret == null ? lt.label : ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see rationals.ioautomata.IOStateMachine#output(java.lang.Object[], int,
	 * int)
	 */
	public int output(Object[] o, int start, int len) {
		if (len <= 0)
			return 0;
		Object out = output();
		o[start] = out;
		return out == null ? 0 : 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see rationals.ioautomata.IOStateMachine#reset()
	 */
	public void reset() {
		state = automaton.initials();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * rationals.ioautomata.IOStateMachine#setInternalHandler(rationals.ioautomata
	 * .Function)
	 */
	public void setInternalHandler(Function hdl) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see rationals.ioautomata.IOStateMachine#stop()
	 */
	public void stop() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
	}

	public Set<State> getState() {
		return state;
	}

	public void setInputEnabled(boolean b) {
		this.inputEnabled = b;
	}

}
