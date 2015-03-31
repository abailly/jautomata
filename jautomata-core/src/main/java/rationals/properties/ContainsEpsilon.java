/*
 * (C) Copyright 2001 Arnaud Bailly (arnaud.oqube@gmail.com),
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
import rationals.Builder;
import rationals.State;
import rationals.Transition;
import rationals.transformations.TransformationsToolBox;

import java.util.Iterator;
import java.util.Set;

/**
 * Checks whether an automaton recognizes the empty word. This test assumes that
 * the tested automaton does not contain epsilon (ie. <code>null</code>)
 * transitions.
 * 
 * @version $Id: ContainsEpsilon.java 2 2006-08-24 14:41:48Z oqube $
 */
public class ContainsEpsilon<L, Tr extends Transition<L>, T extends Builder<L, Tr, T>> implements UnaryTest<L, Tr, T> {

    public boolean test(Automaton<L, Tr, T> a) {
        Iterator<State> i = a.initials().iterator();
        Set<State> s = a.getStateFactory().stateSet();
        while (i.hasNext()) {
            State st = i.next();
            if (st.isTerminal())
                return true;
            s.add(st);
            /* compute epsilon closure */
            Set<State> cl = TransformationsToolBox.epsilonClosure(s,a);
            if(TransformationsToolBox.containsATerminalState(cl))
                return true;
        }
        return false;
    }
}
