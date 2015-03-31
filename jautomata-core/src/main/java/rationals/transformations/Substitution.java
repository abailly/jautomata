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
package rationals.transformations;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import rationals.Automaton;
import rationals.Builder;
import rationals.NoSuchStateException;
import rationals.State;
import rationals.Transition;

/**
 * A (rational) substitution is a  morphism that maps
 * letters to languages.
 * A rational substitution is constructed like a {@see rationals.transformations.Morphism}
 * with an instance of {@see java.util.Map} that has Object as 
 * keys and either Object or Automaton instances as values.
 * If the value of a key is an object, then it is  considered a letter 
 * and this class acts like a morphism. If the value of the key is an
 * Automaton, then the complete transition is replaced by the language
 * denoted by the automaton.
 * 
 * @author nono
 * @see J.Berstel, "Transductions and context-free languages"
 */
public class Substitution<L, Tr extends Transition<L>, T extends Builder<L, Tr, T>> implements UnaryTransformation<L, Tr, T> {

	private Map<?, ?> morph;

	public Substitution(Map<?, ?> m) {
		this.morph = m;
	}

	/* (non-Javadoc)
	 * @see rationals.transformations.UnaryTransformation#transform(rationals.Automaton)
	 */
	public Automaton<L, Tr, T> transform(Automaton<L, Tr, T> a) {
		Automaton<L, Tr, T> b = new Automaton<>();
		/* state map */
		Map<State, State> stm = new HashMap<>();
		for (Iterator<Transition<L>> i = a.delta().iterator(); i.hasNext();) {
			Transition<L> tr = i.next();
			State ns = tr.start();
			State nss = stm.get(ns);
			if (nss == null) {
				nss = b.addState(ns.isInitial(), ns.isTerminal());
				stm.put(ns, nss);
			}
			State ne = tr.end();
			State nse = stm.get(ne);
			if (nse == null) {
				nse = b.addState(ne.isInitial(), ne.isTerminal());
				stm.put(ne, nse);
			}
			L lbl = tr.label();
			if (!morph.containsKey(lbl))
				try {
					b.addTransition(new Transition<L>(nss, lbl, nse));
				} catch (NoSuchStateException e) {
				}
			else
				try {
					/* is value an automaton ? */
					Object o = morph.get(lbl);
					if (o instanceof Automaton)
						insert(nss, nse, b, (Automaton<L, Tr, T>) o);
					else
						b.addTransition(new Transition<L>(nss, (L) morph.get(lbl), nse));
				} catch (NoSuchStateException e1) {
				}
		}
		return b;
	}

	/**
	 * Insert <code>automaton</code> between states <code>nss</code> and
	 * <code>nse</code> in automaton <code>b</code>.
	 * This method add epsilon transitions from <code>nss</code> to each starting 
	 * state of automaton and from each ending state to <code>nse</code>.
	 * 
	 * @param nss
	 * @param nse
	 * @param b
	 * @param automaton
	 */

	private void insert(State nss, State nse, Automaton<L, Tr, T> b, Automaton<L, Tr, T> automaton) {
		/* map states */
		Map<State, State> map = new HashMap<State, State>();
		for (Iterator<State> i = automaton.states().iterator(); i.hasNext();) {
			State e = i.next();
			State n = b.addState(false, false);
			map.put(e, n);
			if (e.isInitial())
				try {
					b.addTransition(new Transition<L>(nss, null, n));
				} catch (NoSuchStateException e1) {
				}
			if (e.isTerminal())
				try {
					b.addTransition(new Transition<L>(n, null, nse));
				} catch (NoSuchStateException e1) {
				}

		}
		for (Iterator<Transition<L>> i = automaton.delta().iterator(); i.hasNext();) {
			Transition<L> t = i.next();
			try {
				b.addTransition(new Transition<L>(map.get(t.start()), t.label(), map.get(t.end())));
			} catch (NoSuchStateException x) {
			}
		}

	}

}
