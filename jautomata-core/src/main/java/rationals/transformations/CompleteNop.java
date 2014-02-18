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
 * @author nono
 * @version $Id: CompleteNop.java 6 2006-08-30 08:56:44Z oqube $
 */
public class CompleteNop implements UnaryTransformation {

    private Set alphabet;

    public CompleteNop(Set alphabet) {
        this.alphabet = alphabet;
    }

    /*
     * (non-Javadoc)
     * 
     * @see rationals.transformations.UnaryTransformation#transform(rationals.Automaton)
     */
    public Automaton transform(Automaton a) {
        Automaton b = new Automaton();
        Map qm = new HashMap();
        for (Iterator i = a.states().iterator(); i.hasNext();) {
            State q = (State) i.next();
            State p = b.addState(q.isInitial(), q.isTerminal());
            qm.put(q, p);
        }
        Set alph = new HashSet();
        for (Iterator it = a.states().iterator(); it.hasNext();) {
            State q = (State) it.next();
            alph.addAll(alphabet);
            for (Iterator i2 = a.delta(q).iterator(); i2.hasNext();) {
                Transition tr = (Transition) i2.next();
                try {
                    b.addTransition(new Transition((State) qm.get(tr.start()),
                            tr.label(), (State) qm.get(tr.end())));
                } catch (NoSuchStateException e) {
                }
                alph.remove(tr.label());
            }
            for (Iterator i2 = alph.iterator(); i2.hasNext();) {
                try {
                    b.addTransition(new Transition(q, i2.next(), q));
                } catch (NoSuchStateException e) {
                }
            }
            alph.clear();
        }
        return b;
    }

}
