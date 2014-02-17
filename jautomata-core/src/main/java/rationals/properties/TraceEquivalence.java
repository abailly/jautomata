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
package rationals.properties;

import rationals.Automaton;
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
 * @author nono
 * @version $Id: TraceEquivalence.java 2 2006-08-24 14:41:48Z oqube $
 */
public class TraceEquivalence implements Relation {

    private Automaton a1;

    private Automaton a2;

    private List errorTrace;

    /*
     * (non-Javadoc)
     * 
     * @see rationals.tests.Relation#setAutomata(rationals.Automaton,
     *      rationals.Automaton)
     */
    public void setAutomata(Automaton a1, Automaton a2) {
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
        Set nsa = a1.getStateFactory().stateSet();
        Set nsb = a2.getStateFactory().stateSet();
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
    public boolean equivalence(Set nsa, Set nsb) {
        /* sets of explored states */
        Stack todo /* < StatesCouple > */= new Stack();
        /* current traces for failure */
        Stack labels = new Stack();
        List trace = new ArrayList();
        Set /* < StatesCouple > */done = new HashSet();
        todo.push(new StatesCouple(nsa, nsb));
        labels.push("");
        do {
            StatesCouple cpl = (StatesCouple) todo.pop();
            Object lbl = labels.pop();
            Set sa = TransformationsToolBox.epsilonClosure(cpl.sa, a1);
            Set sb = TransformationsToolBox.epsilonClosure(cpl.sb, a2);
            if (done.contains(cpl)) {
                trace.remove(trace.size() - 1);
                continue;
            }else
                trace.add(lbl);
            done.add(cpl);
            /* compute set of transitions */
            List /* < Transition > */tas = new ArrayList(a1.delta(sa));
            List /* < Transition > */tbs = new ArrayList(a2.delta(sb));
            /* map from letters to set of states */
            Map /* < Object, State > */am = new HashMap();
            Map /* < Object, State > */bm = new HashMap();
            /* compute set of states reached for each letter */
            mapAlphabet(tas, am, a1);
            mapAlphabet(tbs, bm, a2);
            Iterator it2 = am.entrySet().iterator();
            while (it2.hasNext()) {
                Map.Entry me = (Map.Entry) it2.next();
                Object l = me.getKey();
                Set as = (Set) me.getValue();
                Set bs = (Set) bm.remove(l);
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
                this.errorTrace.add(bm.keySet());
                return false;
            }
        } while (!todo.isEmpty());
        return true;
    }

    /*
     * @param tas @param am
     */
    public void mapAlphabet(List tas, Map am, Automaton a) {
        /* compute set of states for each letter */
        while (!tas.isEmpty()) {
            Transition tr = (Transition) tas.remove(0);
            Object l = tr.label();
            if (l == null)
                continue;
            Set as = (Set) am.get(l);
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
    public List getErrorTrace() {
        return errorTrace;
    }
}