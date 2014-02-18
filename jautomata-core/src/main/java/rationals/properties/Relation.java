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

import java.util.List;
import java.util.Set;

import rationals.Automaton;
import rationals.State;

/**
 * An interface for computing equivalences between automata.
 * 
 * This interface allows definition of various relation between 
 * states.
 * 
 * @author nono
 * @version $Id: Relation.java 2 2006-08-24 14:41:48Z oqube $
 */
public interface Relation {

    /**
     * Sets the context for computing the relation.
     * This method must be called before {@link equivalent(State,State)}.
     * 
     * @param a1
     * @param a2
     */
    void setAutomata(Automaton a1,Automaton a2);
    
    /**
     * Assert the equivalence between two states.
     * This method returns true if and only if the two states
     * are in relation.
     * 
     * @param s1
     * @param s2
     * @return true is s1 ~ s2, false otherwise 
     */
    boolean equivalence(State s1,State s2);


    /**
     * Asset the equivalence between two set of states.
     * This method returns true if and only if the two states set 
     * are equivalent.
     * 
     * @param nsa a Set of State objects from a
     * @param nsb a Set of State objects from b
     * @return true if nsa is equivalent to nsb
     */
    public boolean equivalence(Set nsa, Set nsb);
    
    /**
     * Returns the trace of labels that lead to an error.
     * 
     * @return a List of objects or null.
     */
    public List getErrorTrace();

}
