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
 * Computes the minimal automaton from a deterministic automaton.
 * <p />
 * This class first determinizes the transformed automaton, then compute
 * states equivalence classes to create new states and transitions.
 * 
 * @author nono
 * @version $Id: Reducer.java 2 2006-08-24 14:41:48Z oqube $
 */
public class Reducer<L, Tr extends Transition<L>, T extends Builder<L, Tr, T>> implements UnaryTransformation<L, Tr, T> {

    /*
     * equivalence on DFA
     */
    private boolean same(State e1, State e2, Automaton<L, Tr, T> a, Map<State, Set<State>> m) {
        if (!m.get(e1).equals(m.get(e2)))
            return false;
        /* iterate over all transitions */
        Set<Transition<L>> tas = a.delta(e1);
        Set<Transition<L>> tbs = a.delta(e2);
        Iterator<Transition<L>> it = tas.iterator();
        while (it.hasNext()) {
            Transition<L> tr = it.next();
            State ep1 = tr.end();
            /* check transition exists in b */
            Set<Transition<L>> tbsl = a.delta(e2, tr.label());
            if (tbsl.isEmpty())
                return false;
            Iterator<Transition<L>> trb = tbsl.iterator();
            while (trb.hasNext()) {
                Transition<?> tb = trb.next();
                /* mark transition as visited */
                tbs.remove(tb);
                State ep2 = tb.end();
                if (!m.get(ep1).equals(m.get(ep2)))
                    return false;
            }
        }
        if (!tbs.isEmpty()) {
            return false;
        }
        return true;
    }

    public Automaton<L, Tr, T> transform(Automaton<L, Tr, T> a) {
        Automaton<L, Tr, T> b = new ToDFA<L, Tr, T>().transform(a);
        Map<State, Set<State>> current = new HashMap<State, Set<State>>();
        Set<State> s1 = b.getStateFactory().stateSet();
        Set<State> s2 = b.getStateFactory().stateSet();
        Iterator<State> i = b.states().iterator();
        while (i.hasNext()) {
            State e = i.next();
            if (e.isTerminal()) {
                s1.add(e);
                current.put(e, s1);
            } else {
                s2.add(e);
                current.put(e, s2);
            }
        }
        Map<State, Set<State>> old;
        do {
            old = current;
            current = new HashMap<State, Set<State>>();
            i = old.keySet().iterator();
            while (i.hasNext()) {
                State e1 = i.next();
                Set<State> s = b.getStateFactory().stateSet();
                Iterator<State> j = current.keySet().iterator();
                while (j.hasNext()) {
                    State e2 = j.next();
                    if (same(e1, e2, b, old)) {
                        s = current.get(e2);
                        break;
                    }
                }
                s.add(e1);
                current.put(e1, s);
            }
        } while (!new HashSet<Set<State>>(current.values())
                .equals(new HashSet<Set<State>>(old.values())));
        Automaton<L, Tr, T> c = new Automaton<L, Tr, T>();
        Set<Set<State>> setSet = new HashSet<Set<State>>(current.values());
        Iterator<Set<State>> sets = setSet.iterator();
        Map<Set<State>, State> newStates = new HashMap<>();
        while (sets.hasNext()) {
            Set<State> set = sets.next();
            boolean term = TransformationsToolBox.containsATerminalState(set);
            boolean init = TransformationsToolBox.containsAnInitialState(set);
            newStates.put(set, c.addState(init, term));
        }
        sets = setSet.iterator();
        while (sets.hasNext()) {
            Set<State> set = sets.next();
            State r = set.iterator().next();
            State rp = newStates.get(set);
            Iterator<L> k = b.alphabet().iterator();
            while (k.hasNext()) {
                L l = k.next();
                Set<Transition<L>> ds = b.delta(r, l);
                if(ds.isEmpty())
                    continue;
                State f = ds.iterator().next().end();
                State fp = newStates.get(current.get(f));
                try {
                    c.addTransition(new Transition<>(rp, l, fp));
                } catch (NoSuchStateException x) {
                }
            }
        }
        return c;
    }

}