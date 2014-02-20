/*
 * (C) Copyright 2002 Arnaud Bailly (arnaud.oqube@gmail.com),
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

import java.util.*;

/**
 * This class allows to remove epsilon transitions in an automaton. Epsilon
 * transition are transitions (q , l , q') where l is null.
 * 
 * @author Yves Roos
 * @version 22032002
 */
public class EpsilonTransitionRemover implements UnaryTransformation {

    /*
     * (non-Javadoc)
     * 
     * @see rationals.transformations.UnaryTransformation#transform(rationals.Automaton)
     */
    public Automaton transform(Automaton a) {
        Automaton ret = new Automaton(); /* resulting automaton */
        Map /* < HashValue, State > */ sm = new HashMap();
        Set done = new HashSet();
        List todo = new ArrayList(); /* set of states to explore */
        Set cur = TransformationsToolBox.epsilonClosure(a.initials(), a);
        /* add cur as initial state of ret */
        State is = ret.addState(true,TransformationsToolBox.containsATerminalState(cur));
        HashValue hv = new HashValue(cur);
        sm.put(hv,is);
        todo.add(hv);
        do {
            HashValue s = (HashValue) todo.remove(0);
            State ns =  (State)sm.get(s);
            if(ns == null) {
                ns = ret.addState(false,TransformationsToolBox.containsATerminalState(s.s));
                sm.put(s,ns);
            }
            /* set s as explored */
            done.add(s);
            /* look for all transitions in s */
            Map /* < Object, Set > */trm = instructions(a.delta(s.s),a);
            Iterator it = trm.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry e = (Map.Entry) it.next();
                Object o = e.getKey();
                Set ar = (Set) e.getValue();
                /* compute closure of arrival set */
                ar = TransformationsToolBox.epsilonClosure(ar, a);
                hv = new HashValue(ar);
                /* retrieve state in new automaton from hashvalue */
                State ne = (State)sm.get(hv);
                if(ne == null) {
                    ne = ret.addState(false,TransformationsToolBox.containsATerminalState(ar));
                    sm.put(hv,ne);
                }
                try {
                    /* create transition */
                    ret.addTransition(new Transition(ns,o,ne));
                } catch (NoSuchStateException e1) {
                }
                /* explore new state */
                if(!done.contains(hv))
                    todo.add(hv);
            }
        } while (!todo.isEmpty());
        return ret;
    }

    private Map /* < Object, Set > */instructions(Set /* < Transition > */s,Automaton a) {
        Map /* < Object, Set > */m = new HashMap();
        Iterator it = s.iterator();
        while (it.hasNext()) {
            Transition tr = (Transition) it.next();
            Object l = tr.label();
            if (l != null) {
                Set st = (Set) m.get(l);
                if (st == null) {
                    st = a.getStateFactory().stateSet();
                    m.put(l,st);
                }
                /* add arrival state */
                st.add(tr.end());
            }
        }
        return m;
    }

}

