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
package rationals.transformations;
import rationals.Automaton;
import rationals.Builder;
import rationals.NoSuchStateException;
import rationals.State;
import rationals.Transition;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Compute the union of two automaton.
 * <ul>
 * <li>C = A + B</li>
 * <li>S(C) = S(A) U S(B)</li>
 * <li>S0(C) = S0(A) U SO(B)</li>
 * <li>T(C) = T(A) U T(B)</li>
 * <li>D(C) = D(A) U D(B)</li>
 * </ul>

 * @author nono
 * @version $Id: Union.java 2 2006-08-24 14:41:48Z oqube $
 */
public class Union<L, Tr extends Transition<L>, T extends Builder<L, Tr, T>> implements BinaryTransformation<L, Tr, T> {


  public Automaton<L, Tr, T> transform(Automaton<L, Tr, T> a , Automaton<L, Tr, T> b) {
    Automaton<L, Tr, T> ap = a.clone() ;
    Map<State, State> map = new HashMap<State, State>() ;
    Iterator<State> i1 = b.states().iterator() ;
    while(i1.hasNext()) {
      State e = i1.next() ;
      map.put(e , ap.addState(e.isInitial() , e.isTerminal())) ;
    }
    Iterator<Transition<L>> i2 = b.delta().iterator() ;
    while(i2.hasNext()) {
      Transition<L> t = i2.next();
      try {
        ap.addTransition(new Transition<>(map.get(t.start()), t.label(), map.get(t.end()))) ;
      } catch(NoSuchStateException x) {}
    }
    return ap ;
  }      
}
