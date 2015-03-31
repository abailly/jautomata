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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import rationals.Automaton;
import rationals.Builder;
import rationals.NoSuchStateException;
import rationals.State;
import rationals.Transition;

/**
 * This methods completes the transitions in an Automaton w.r.t. to an
 * arbitrary alphabet.
 * <p>
 * That is, for each state <code>q</code> and for each letter <code>l</code>
 * in <code>ioa</code>'s alphabet, if there is no transition labelled with
 * <code>l</code> starting from <code>q</code>, it adds a transition
 * <code>(q,l,q)</code> to this automaton.
 * <p>
 * The semantic of this completion scheme should be compared with
 * {@link rationals.transformations.CompleteSink}which completes an automaton
 * by adding a sink state.
 * <p>
 * 
 * @version $Id: CompleteNop.java 6 2006-08-30 08:56:44Z oqube $
 */
public class CompleteNop<L, Tr extends Transition<L>, T extends Builder<L, Tr, T>> implements UnaryTransformation<L, Tr, T> {

    private Set<L> alphabet;

    public CompleteNop(Set<L> alphabet) {
        this.alphabet = alphabet;
    }

    /*
     * (non-Javadoc)
     * 
     * @see rationals.transformations.UnaryTransformation#transform(rationals.Automaton)
     */
    public Automaton<L, Tr, T> transform(Automaton<L, Tr, T> a) {
        Automaton<L, Tr, T> b = new Automaton<>();
        Map<State, State> qm = new HashMap<>();
        for (Iterator<State> i = a.states().iterator(); i.hasNext();) {
            State q = i.next();
            State p = b.addState(q.isInitial(), q.isTerminal());
            qm.put(q, p);
        }
        Set<L> alph = new HashSet<>();
        for (Iterator<State> it = a.states().iterator(); it.hasNext();) {
            State q = it.next();
            alph.addAll(alphabet);
            for (Iterator<Transition<L>> i2 = a.delta(q).iterator(); i2.hasNext();) {
                Transition<L> tr = i2.next();
                try {
                    b.addTransition(new Transition<>(qm.get(tr.start()), tr.label(), qm.get(tr.end())));
                } catch (NoSuchStateException e) {
                }
                alph.remove(tr.label());
            }
            for (Iterator<L> i2 = alph.iterator(); i2.hasNext();) {
                try {
                    b.addTransition(new Transition<L>(q, i2.next(), q));
                } catch (NoSuchStateException e) {
                }
            }
            alph.clear();
        }
        return b;
    }

}
