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

import java.util.HashMap;
import java.util.Map;

/**
 * Test if two automaton are isomorphic.
 * Two automata are isomorphic iff there exists a bijection
 * between states of each automata that preserve the initial states, the 
 * terminal states and the transition relation.
 *  
 * @author nono
 * @version $Id: AreIsomorph.java 2 2006-08-24 14:41:48Z oqube $
 */
public class AreIsomorph implements BinaryTest {

    /* (non-Javadoc)
     * @see rationals.properties.BinaryTest#test(rationals.Automaton, rationals.Automaton)
     */
    public boolean test(Automaton a, Automaton b) {
        /* basic test */
        if(a.states().size() != b.states().size() || a.initials().size() != b.initials().size() || a.terminals().size() != b.terminals().size())
            return false;
        Map /* < State , State > */ atob = new HashMap();
        // TODO        
        return false;
    }

}
