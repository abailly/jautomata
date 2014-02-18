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
import rationals.Couple;
import rationals.State;
import rationals.Transition;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * This class implements (strong) simulation equivalence between 
 * two automata.
 * <p />
 * Given two automata <code>A=(Qa,q0a,Ta,Sa,deltaA)</code> and <code>B=(Qb,q0b,Tb,Sb,deltaB)</code>,
 * a simulation S of A by B is a relation in <code>Qa x Qb</code> 
 * s.t., whenever <code>(qa,qb) \in S</code>,
 * <ul>
 * <li>for each <code>(qa,a,qa') \in deltaA</code>, exists <code>(qb,a,qb')\in deltaB</code>
 * and <code>(qa',qb') \in S</code>,</li>
 * </ul>
 * B is a simulation of A iff <code>q0b ~ q0a</code>.
 * <p />
 * Note that in general, a simulation is not symetric. A symmetric 
 * simulation is of course a bisimulation.
 * 
 * @author nono
 * @version $Id: Simulation.java 2 2006-08-24 14:41:48Z oqube $
 * @see rationals.properties.Bisimulation
 */
public class Simulation implements Relation {
    
    private Automaton a1;

    private Automaton a2;

    private Set exp;

    /**
     * Constructor with two automataon.
     * This constructor effectively calls {@link #setAutomata(Automaton,Automaton)}.
     * 
     * @param automaton
     * @param automaton2
     */
    public Simulation(Automaton automaton, Automaton automaton2) {
        setAutomata(automaton,automaton2);
    }
    

    /*
     * (non-Javadoc)
     * 
     * @see rationals.tests.Relation#setAutomata(rationals.Automaton,
     *      rationals.Automaton)
     */
    public void setAutomata(Automaton a1, Automaton a2) {
        this.a1 = a1;
        this.a2 = a2;
        this.exp = new HashSet();
    }

    public Simulation() {}
    
    /**
     * Checks that all combination of states from nsa and nsb
     * are bisimilar.
     * 
     */
    public boolean equivalence(Set nsa, Set nsb) {
       for(Iterator i = nsa.iterator();i.hasNext();) {
           State sa = (State)i.next();
           for(Iterator j = nsb.iterator();j.hasNext();) {
               State sb = (State)j.next();
               if(!equivalence(sa,sb))
                   return false;
           }
       }
       return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see rationals.tests.Relation#equivalence(rationals.State,
     *      rationals.State)
     */
    public boolean equivalence(State q0a, State q0b) {
        Couple cpl = new Couple(q0a, q0b);
        /* check states are unknown */
        if (exp.contains(cpl))
            return true;
        exp.add(cpl);
        /* iterate over all transitions */
        Set tas = a1.delta(q0a);
        Set tbs = a2.delta(q0b);
        Iterator it = tas.iterator();
        while (it.hasNext()) {
            Transition tr = (Transition) it.next();
            State ea = tr.end();
            /* check transition exists in b */
            Set tbsl = a2.delta(q0b, tr.label());
            if (tbsl.isEmpty())
                return false;
            Iterator trb = tbsl.iterator();
            while (trb.hasNext()) {
                Transition tb = (Transition) trb.next();
                /* mark transition as visited */
                tbs.remove(tb);
                State eb = tb.end();
                if (!equivalence(ea, eb) && !trb.hasNext())
                    return false;
            }
        }
        /* OK */
        return true;
    }

    /* (non-Javadoc)
     * @see rationals.properties.Relation#getErrorTrace()
     */
    public List getErrorTrace() {
        // TODO Auto-generated method stub
        return null;
    }

}
