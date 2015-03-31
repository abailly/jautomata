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

/**
 * Tests if an automaton is normalized.
 * 
 * @see rationals.transformations.Normalizer
 * @author nono
 * @version $Id: isNormalized.java 2 2006-08-24 14:41:48Z oqube $
 */
public class isNormalized<L, Tr extends Transition<L>, T extends Builder<L, Tr, T>> implements UnaryTest<L, Tr, T> {
    public boolean test(Automaton<L, Tr, T> a) {
        if (a.initials().size() != 1)
            return false;
        if (a.terminals().size() != 1)
            return false;
        State e = a.initials().iterator().next();
        if (a.deltaMinusOne(e).size() > 0)
            return false;
        e = a.terminals().iterator().next();
        if (a.delta(e).size() > 0)
            return false;
        return true;
    }
}