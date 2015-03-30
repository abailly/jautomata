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
package rationals.properties;

import rationals.Automaton;
import rationals.Builder;
import rationals.State;
import rationals.Transition;
import rationals.transformations.StatesCouple;
import rationals.transformations.TransformationsToolBox;

import java.util.*;

/**
 * A class that compute trace equivalence relation between two states. This
 * class checks whether two states from two automata are trace equivalent, which
 * simply means they recognize the same prefix of languages.
 * <p>
 * This class effectively computes the deterministic form of the two given
 * automata.
 * 
 * @version $Id: TraceEquivalence.java 2 2006-08-24 14:41:48Z oqube $
 */
public class TraceEquivalence<L, Tr extends Transition<L>, T extends Builder<L, Tr, T>> implements Relation<L, Tr, T> {

    private Automaton<L, Tr, T> a1;

    private Automaton<L, Tr, T> a2;

    private List<L> errorTrace;

    /*
     * (non-Javadoc)
     * 
     * @see rationals.tests.Relation#setAutomata(rationals.Automaton,
     *      rationals.Automaton)
     */
    public void setAutomata(Automaton<L, Tr, T> a1, Automaton<L, Tr, T> a2) {
        this.a1 = a1;
        this.a2 = a2;
    }

    /*
     * (non-Javadoc)
     * 
     * @see rationals.tests.Relation#equivalence(rationals.State,
     *      rationals.State)
     */
    public boolean equivalence(State q0a, State q0b) {
        /* compute epsilon closures on states */
        Set<State> nsa = a1.getStateFactory().stateSet();
        Set<State> nsb = a2.getStateFactory().stateSet();
        nsa.add(q0a);
        nsb.add(q0b);
        /* check equivalence on sets */
        return equivalence(nsa, nsb);
    }

    /*
     * (non-Javadoc)
     * 
     * @see rationals.properties.Relation#equivalence(java.util.Set,
     *      java.util.Set)
     */
    public boolean equivalence(Set<State> nsa, Set<State> nsb) {
        /* sets of explored states */
        Stack<StatesCouple> todo = new Stack<>();
        /* current traces for failure */
        Stack<L> labels = new Stack<>();
        List<L> trace = new ArrayList<>();
        Set<StatesCouple> done = new HashSet<>();
        todo.push(new StatesCouple(nsa, nsb));
        labels.push(null); // Needed to avoid empty stack
        do {
            StatesCouple cpl = todo.pop();
            L lbl = labels.pop();
            Set<State> sa = TransformationsToolBox.epsilonClosure(cpl.sa, a1);
            Set<State> sb = TransformationsToolBox.epsilonClosure(cpl.sb, a2);
            if (done.contains(cpl)) {
                trace.remove(trace.size() - 1);
                continue;
            } else {
            	trace.add(lbl);
            }
            done.add(cpl);
            /* compute set of transitions */
            List<Transition<L>> tas = new ArrayList<>(a1.delta(sa));
            List<Transition<L>> tbs = new ArrayList<>(a2.delta(sb));
            /* map from letters to set of states */
            Map<L, Set<State>> am = new HashMap<>();
            Map<L, Set<State>> bm = new HashMap<>();
            /* compute set of states reached for each letter */
            mapAlphabet(tas, am, a1);
            mapAlphabet(tbs, bm, a2);
            Iterator<Map.Entry<L, Set<State>>> it2 = am.entrySet().iterator();
            while (it2.hasNext()) {
            	Map.Entry<L, Set<State>> me = it2.next();
                L l = me.getKey();
                Set<State> as = me.getValue();
                Set<State> bs = bm.remove(l);
                if (bs == null) {
                    this.errorTrace = trace;
                    this.errorTrace.add(l);
                    return false;
                }
                StatesCouple sc = new StatesCouple(as, bs);
                todo.push(sc);
                labels.push(l);
            }
            if (!bm.isEmpty()) {
                this.errorTrace = trace;
                this.errorTrace.addAll(bm.keySet());
                return false;
            }
        } while (!todo.isEmpty());
        return true;
    }

    /*
     * @param tas @param am
     */
    public void mapAlphabet(List<Transition<L>> tas, Map<L, Set<State>> am, Automaton<L, Tr, T> a) {
        /* compute set of states for each letter */
        while (!tas.isEmpty()) {
            Transition<L> tr = tas.remove(0);
            L l = tr.label();
            if (l == null)
                continue;
            Set<State> as = am.get(l);
            if (as == null) {
                as = a.getStateFactory().stateSet();
                am.put(l, as);
            }
            as.add(tr.end());
        }
    }

    /**
     * @return Returns the errorTrace.
     */
    public List<L> getErrorTrace() {
        return errorTrace;
    }
}