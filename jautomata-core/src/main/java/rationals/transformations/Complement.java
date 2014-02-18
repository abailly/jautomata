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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rationals.Automaton;
import rationals.NoSuchStateException;
import rationals.State;
import rationals.Transition;

/**
 * A transformation that computes the complement of an automaton.
 * <p>
 * This transformation computes the complement of an automaton: Terminal states
 * are inverted and missing transitions are added.
 * 
 * @author nono
 * @version $Id: Complement.java 2 2006-08-24 14:41:48Z oqube $
 */
public class Complement implements UnaryTransformation {

    /*
     * (non-Javadoc)
     * 
     * @see rationals.transformations.UnaryTransformation#transform(rationals.Automaton)
     */
    public Automaton transform(Automaton a) {
        Automaton ret = new Automaton();
        List todo = new ArrayList();
        Map sm = new HashMap();
        Set done = new HashSet();
        Set s = a.initials();
        todo.addAll(s);
        while (!todo.isEmpty()) {
            State st = (State) todo.remove(0);
            State ns = (State) sm.get(st);
            if (ns == null) {
                ns = ret.addState(st.isInitial(), !st.isTerminal());
                sm.put(st, ns);
            }
            done.add(st);
            for (Iterator it = a.alphabet().iterator(); it.hasNext();) {
                Object l = it.next();
                Set ends = a.delta(st, l);
                if (ends.isEmpty())
                    try {
                        ret.addTransition(new Transition(ns, l, ns));
                    } catch (NoSuchStateException e) {
                    }
                else {
                    for (Iterator i = ends.iterator(); i.hasNext();) {
                        State end = ((Transition) i.next()).end();
                        State ne = (State) sm.get(end);
                        if (ne == null) {
                            ne = ret.addState(end.isInitial(), !end
                                    .isTerminal());
                            sm.put(end, ne);
                            todo.add(end);
                        }
                        try {
                            ret.addTransition(new Transition(ns, l, ne));
                        } catch (NoSuchStateException e) {
                        }
                    }
                }

            }
        }
        return ret;
    }

}