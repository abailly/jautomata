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
import rationals.properties.ContainsEpsilon;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A transformation that normalizes a given Automaton.
 * <p>
 * This transformation produces a new Automaton with the following features :
 * <ol>
 * <li>it has <em>one</em> start state and <em>one</em> end state,</li>
 * <li>there is no incoming (resp. outgoing) transitions to (resp. from) the
 * start (resp. end) state,</li>
 * <li>the resultant automaton is then pruned ({@link Pruner}) to remove
 * inaccessible states.</li>
 * </ol>
 * 
 * @author yroos
 * @version $Id: Normalizer.java 2 2006-08-24 14:41:48Z oqube $
 */
public class Normalizer<L, Tr extends Transition<L>, T extends Builder<L, Tr, T>> implements UnaryTransformation<L, Tr, T> {

    public Automaton<L, Tr, T> transform(Automaton<L, Tr, T> a) {
        Automaton<L, Tr, T> b = new Automaton<>();
        State ni = b.addState(true, false);
        State nt = b.addState(false, true);
        Map<State, State> map = new HashMap<State, State>();
        Iterator<State> i1 = a.states().iterator();
        while (i1.hasNext()) {
            State st = i1.next();
            map.put(st, b.addState(false, false));
        }
        /* add epsilon transition if contains epsilon */
        if (new ContainsEpsilon<L, Tr, T>().test(a))
            try {
                b.addTransition(new Transition<L>(ni, null, nt));
            } catch (NoSuchStateException e) {
            }
        Iterator<Transition<L>> i2 = a.delta().iterator();
        while (i2.hasNext()) {
            Transition<L> t =  i2.next();
            if (t.start().isInitial() && t.end().isTerminal()) {
                try {
                    b.addTransition(new Transition<L>(ni, t.label(), nt));
                } catch (NoSuchStateException x) {
                }
            }
            if (t.start().isInitial()) {
                try {
                    b.addTransition(new Transition<L>(ni, t.label(), map.get(t.end())));
                } catch (NoSuchStateException x) {
                }
            }

            if (t.end().isTerminal())
                try {
                    b.addTransition(new Transition<L>(map.get(t.start()), t.label(), nt));
                } catch (NoSuchStateException x) {
                }

            try {
                b.addTransition(new Transition<L>(map.get(t.start()), t.label(), map.get(t.end())));
            } catch (NoSuchStateException x) {
            }

        }
        b = new Pruner<L, Tr, T>().transform(b);
        return b;
    }
}

/*
 * $Log: Normalizer.java,v $ Revision 1.4 2005/02/20 21:14:19 bailly added API
 * for computing equivalence relations on automata
 * 
 * Revision 1.3 2004/09/21 11:50:28 bailly added interface BinaryTest added
 * class for testing automaton equivalence (isomorphism of normalized automata)
 * added computation of RE from Automaton
 *  
 */