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
import rationals.State;
import rationals.transformations.EpsilonTransitionRemover;

import java.util.List;
import java.util.Set;

/**
 * This method computes the weak bisimulation relation between two states.
 * The weak bisimulation is computed as (strong) bisimulation between the two 
 * given automata where all epsilon transitions have been removed.
 * 
 * @author nono
 * @version $Id: WeakBisimulation.java 2 2006-08-24 14:41:48Z oqube $
 */
public class WeakBisimulation implements Relation {

    private Automaton a1;

    private Automaton a2;

    private Set exp;

    private Bisimulation bisim;

    /*
     * (non-Javadoc)
     * 
     * @see rationals.tests.Relation#setAutomata(rationals.Automaton,
     *      rationals.Automaton)
     */
    public void setAutomata(Automaton a1, Automaton a2) {
        EpsilonTransitionRemover er = new EpsilonTransitionRemover();
        this.bisim = new Bisimulation( er.transform(a1), er.transform(a2));
    }

    /*
     * (non-Javadoc)
     * 
     * @see rationals.tests.Relation#equivalence(rationals.State,
     *      rationals.State)
     */
    public boolean equivalence(State q0a, State q0b) {
        return bisim.equivalence(q0a,q0b);
    }

    public boolean equivalence(Set nsa, Set nsb) {
        return bisim.equivalence(nsa,nsb);
    }
    /* (non-Javadoc)
     * @see rationals.properties.Relation#getErrorTrace()
     */
    public List getErrorTrace() {
        throw new UnsupportedOperationException();
    }
}