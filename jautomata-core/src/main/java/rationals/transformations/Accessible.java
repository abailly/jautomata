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
package rationals.transformations;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import rationals.Automaton;
import rationals.NoSuchStateException;
import rationals.State;
import rationals.Transition;

/**
 * Compute the automaton accessible from a given state
 * @author bailly
 * @version $Id: Accessible.java 2 2006-08-24 14:41:48Z oqube $
 */
public class Accessible implements UnaryTransformation {

	private State state;

	/**
	 * The state we must start exploration from
	 * 
	 */
	public Accessible(State s) {
		this.state = s;
	}

	/* (non-Javadoc)
	 * @see rationals.transformations.UnaryTransformation#transform(rationals.Automaton)
	 */
	public Automaton transform(Automaton a) {
		Set trs = a.delta();
		Automaton b = new Automaton();
		Map stmap = new HashMap();
		/* initial state = state */
		State ns = b.addState(true,state.isTerminal());
		stmap.put(state,ns);
		explore(state,stmap,a,b);
		/* eplore a and remove transitions from trs */
		Iterator it = trs.iterator();
		while(it.hasNext()) {
			Transition tr = (Transition)it.next();
			State nstart = (State)stmap.get(tr.start());
			State nend = (State)stmap.get(tr.end());
			if((nstart != null) && (nend != null))
				try {
					b.addTransition(new Transition(nstart,tr.label(),nend));
				} catch (NoSuchStateException e) {
					System.err.println(e.getMessage());
					return null;
				}
		}
		return b;
	}

	/**
	 * Recursive function to explore transitions from a state
	 * @param state
	 * @param stmap
	 * @param b
	 */
	private void explore(State curstate, Map stmap, Automaton a,Automaton b) {
		Iterator it = a.delta(curstate).iterator();
		while(it.hasNext()) {
			Transition tr = (Transition)it.next();
			State e= tr.end();
			State ne = (State)stmap.get(e);
			if(ne != null)
				continue;
			else {
				ne = b.addState(e.isInitial(),e.isTerminal());
				stmap.put(e,ne);
				explore(e,stmap,a,b);
			}
		}
	}

}
