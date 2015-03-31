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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Compute the kleene-star closure of an automaton.
 * 
 * @version $Id: Star.java 2 2006-08-24 14:41:48Z oqube $
 */
public class Star<L, Tr extends Transition<L>, T extends Builder<L, Tr, T>> implements UnaryTransformation<L, Tr, T> {
    public Automaton<L, Tr, T> transform(Automaton<L, Tr, T> a) {
        if (a.delta().size() == 0)
            return Automaton.epsilonAutomaton();
        Automaton<L, Tr, T> b = new Automaton<>();
        State ni = b.addState(true, true);
        State nt = b.addState(true, true);
        Map<State, State> map = new HashMap<>();
        Iterator<State> i1 = a.states().iterator();
        while (i1.hasNext()) {
            map.put(i1.next(), b.addState(false, false));
        }
        Iterator<Transition<L>> i2 = a.delta().iterator();
        while (i2.hasNext()) {
            Transition<L> t = i2.next();
            try {
                b.addTransition(new Transition<>(map.get(t.start()), t.label(), map.get(t.end())));
            } catch (NoSuchStateException x) {
            }
            if (t.start().isInitial() && t.end().isTerminal()) {
                try {
                    b.addTransition(new Transition<>(ni, t.label(), nt));
                    b.addTransition(new Transition<>(nt, t.label(), ni));
                } catch (NoSuchStateException x) {
                }
            } else if (t.start().isInitial()) {
                try {
                    b.addTransition(new Transition<>(ni, t.label(), map.get(t.end())));
                    b.addTransition(new Transition<>(nt, t.label(), map.get(t.end())));
                } catch (NoSuchStateException x) {
                }
            } else if (t.end().isTerminal()) {
                try {
                    b.addTransition(new Transition<>(map.get(t.start()), t.label(), nt));
                    b.addTransition(new Transition<>(map.get(t.start()), t.label(), ni));
                } catch (NoSuchStateException x) {
                }
            }
        }
        return b;
    }
}