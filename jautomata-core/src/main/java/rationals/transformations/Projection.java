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

import rationals.Automaton;
import rationals.NoSuchStateException;
import rationals.State;
import rationals.Transition;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * This class computes the projection of an Automaton on given alphabet. The
 * projection alphabet is set by the class's constructor.
 * <p />
 * The algorithm is verys simple: All transitions which are not labelled
 * with letters from the projection alphabet are transformed into 
 * <code>null</code> transitions. The resulting automaton is obviously no 
 * more deterministic if the automaton <code>a</code> was.
 * 
 * @author nono
 * @version $Id: Projection.java 2 2006-08-24 14:41:48Z oqube $
 */
public class Projection implements UnaryTransformation {

    private Set alphabet;

    public Projection(Set alphabet) {
        this.alphabet = alphabet;
    }

    /*
     * (non-Javadoc)
     * 
     * @see rationals.transformations.UnaryTransformation#transform(rationals.Automaton)
     */
    public Automaton transform(Automaton a) {
        Automaton b = new Automaton();
        Map smap = new HashMap();
        Iterator it = a.delta().iterator();
        while (it.hasNext()) {
            Transition tr = (Transition) it.next();
            State os = tr.start();
            State oe = tr.end();
            Object l = tr.label();
            /* check states exist */
            State ns = (State) smap.get(os);
            State ne = (State) smap.get(oe);
            if (ns == null)
                smap.put(os, ns = b.addState(os.isInitial(), os.isTerminal()));
            if (ne == null)
                smap.put(oe, ne = b.addState(oe.isInitial(), oe.isTerminal()));
            /* check label is in alphabet */
            if (alphabet.contains(l))
                try {
                    b.addTransition(new Transition(ns, l, ne));
                } catch (NoSuchStateException e) {
                }
            else
                try {
                    b.addTransition(new Transition(ns, null, ne));
                } catch (NoSuchStateException e1) {
                }
        }
        return b;
    }
}