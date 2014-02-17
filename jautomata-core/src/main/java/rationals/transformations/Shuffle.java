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
package rationals.transformations;

import rationals.Automaton;
import rationals.Synchronization;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * This class implements the shuffle operator between two automatas.
 * <ul>
 * <li>C = A shuffle B</li>
 * <li>S(C) = { (a,b) | a in S(A) and b in S(B) }</li>
 * <li>S0(C) = (S0(A),SO(B))</li>
 * <li>T(C) = { (a,b) | a in T(A) and b in T(B) }</li>
 * <li>D(C) = { ((s1a,s1b),a,(s2a,s2b)) | exists (s1a,a,s2a) in D(A) or exists
 * (s1b,a,s2b) in D(b) }</li>
 * </ul>
 * This class uses the Mix operator with an empty alphabet to compute the
 * Shuffle.
 * 
 * @author Arnaud Bailly
 * @version $Id: Shuffle.java 2 2006-08-24 14:41:48Z oqube $
 * @see Mix
 */
public class Shuffle implements BinaryTransformation {

    public Automaton transform(Automaton a, Automaton b) {

        Mix mix = new Mix(new Synchronization() {
            public Object synchronize(Object t1, Object t2) {
                return null;
            }

            public Set synchronizable(Set a, Set b) {
                return Collections.unmodifiableSet(new HashSet());
            }

            public Set synchronizing(Collection alphabets) {
                // TODO Auto-generated method stub
                return null;
            }

            public boolean synchronizeWith(Object object, Set alph) {
                // TODO Auto-generated method stub
                return false;
            }
        });
        return mix.transform(a, b);
    }
}
