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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import rationals.Automaton;
import rationals.State;
import rationals.Transition;

/**
 * A property that checks a given automaton is deterministic. 
 * <p>
 * An automaton <code>(X,Q,I,T,D)</code> is deterministic iff :
 * <ul>
 * <li><code>|I| = 1</code></li>
 * <li><code>D is a function from (Q x X) -> Q</code>
 * <li><code>X</code> does not contains the symbol <code>epsilon</code></li>
 * </ul>
 * @author nono
 * @version $Id: IsDeterministic.java 2 2006-08-24 14:41:48Z oqube $
 */
public class IsDeterministic implements UnaryTest {

    /* (non-Javadoc)
     * @see rationals.properties.UnaryTest#test(rationals.Automaton)
     */
    public boolean test(Automaton a) {
        if(a.alphabet().contains(null))
            return false;
        if(a.initials().size() > 1)
            return false;
        for(Iterator it = a.states().iterator();it.hasNext();) {
            State s = (State)it.next();
            Set tra = new HashSet();
            for(Iterator it2 = a.delta(s).iterator();it2.hasNext();) {
                Transition tr =(Transition)it2.next();
                if(tra.contains(tr.label()))
                    return false;
                else
                    tra.add(tr.label());
            }
        }
        return true;
    }

}
