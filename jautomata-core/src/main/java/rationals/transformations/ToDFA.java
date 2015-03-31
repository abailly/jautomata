/*
 * (C) Copyright 2001 Arnaud Bailly (arnaud.oqube@gmail.com),
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

import rationals.Automaton;
import rationals.Builder;
import rationals.NoSuchStateException;
import rationals.State;
import rationals.Transition;

import java.util.*;

/**
 * Determinization of an automaton.
 * 
 * @author yroos
 * @version $Id: ToDFA.java 7 2006-08-31 23:01:30Z oqube $
 */
public class ToDFA<L, Tr extends Transition<L>, T extends Builder<L, Tr, T>> implements UnaryTransformation<L, Tr, T> {
  
	public Automaton<L, Tr, T> transform(Automaton<L, Tr, T> a) {
		Automaton<L, Tr, T> ret = new Automaton<>();
		Map<Set<State>, State> bmap = new HashMap<>();
		List<Set<State>>todo = new ArrayList<>();
		Set<Set<State>>done = new HashSet<>();
		Set<State> as = TransformationsToolBox.epsilonClosure(a.initials(), a);
		State from = ret.addState(true, TransformationsToolBox.containsATerminalState(as));
		bmap.put(as, from);
		todo.add(as);
		do {
			Set<State> sts = todo.remove(0);
			from = bmap.get(sts);
			if (done.contains(sts))
				continue;
			done.add(sts);
			/* get transition sets */
			Map<L, Set<State>> tam = TransformationsToolBox.mapAlphabet(a.delta(sts), a);
			/* unsynchronizable transitions in A */
			for (Iterator<Map.Entry<L, Set<State>>> i = tam.entrySet().iterator(); i.hasNext();) {
				Map.Entry<L, Set<State>> me = i.next();
				L l = me.getKey();
				as = (Set<State>) me.getValue();
				Set<State> asc = TransformationsToolBox.epsilonClosure(as, a);
				State to = (State) bmap.get(asc);
				if (to == null) {
					to = ret.addState(false, TransformationsToolBox
							.containsATerminalState(asc));
					bmap.put(asc, to);
				}
				todo.add(asc);
				try {
					ret.addTransition(new Transition<L>(from, l, to));
				} catch (NoSuchStateException e) {
					assert false;
				}
			}
		} while (!todo.isEmpty());
		return ret;
	}

  /*
    public Automaton transform(Automaton a) {
        a = new EpsilonTransitionRemover().transform(a);
        Automaton b = new Automaton();
        Map map = new HashMap();
        LinkedList l = new LinkedList();
        Set done = new HashSet();
        Set e = a.initials();
        boolean t = TransformationsToolBox.containsATerminalState(e);
        map.put(e, b.addState(true, t));
        l.add(e);
        while (!l.isEmpty()) {
            Set e1 = (Set) l.removeFirst();
            done.add(e1);
            State ep1 = (State) map.get(e1);
            Iterator j = a.alphabet().iterator();
            Object label = null;
            while (j.hasNext()) {
                label = j.next();
                Iterator i = e1.iterator();
                Set e2 = a.getStateFactory().stateSet();
                while (i.hasNext()) {
                    Iterator k = a.delta((State) i.next(), label).iterator();
                    while (k.hasNext()) {
                        e2.add(((Transition) k.next()).end());
                    }
                }
                State ep2;
                if (!e2.isEmpty()) {
                    if (!map.containsKey(e2)) {
                        t = TransformationsToolBox.containsATerminalState(e2);
                        map.put(e2, b.addState(false, t));
                    }
                    ep2 = (State) map.get(e2);
                    try {
                        b.addTransition(new Transition(ep1, label, ep2));
                    } catch (NoSuchStateException x) {
                    }
                    if (!done.contains(e2))
                        l.add(e2);
                }
            }
        }
        return b;
    }
  */
}
