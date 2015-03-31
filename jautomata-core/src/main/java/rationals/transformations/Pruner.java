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
 * Removes states that neither accessible nor coaccessible.
 * 
 * @version $Id: Pruner.java 2 2006-08-24 14:41:48Z oqube $
 */
public class Pruner<L, Tr extends Transition<L>, T extends Builder<L, Tr, T>> implements UnaryTransformation<L, Tr, T> {

  public Automaton<L, Tr, T> transform(Automaton<L, Tr, T> a) {
    Map<State, State> conversion = new HashMap<>() ;
    Iterator<State> i1 = a.accessibleAndCoAccessibleStates().iterator();
    Automaton<L, Tr, T> b = new Automaton<>() ;
    while(i1.hasNext()) {
      State e = i1.next() ;
      conversion.put(e , b.addState(e.isInitial() , e.isTerminal())) ;
    }
    Iterator<Transition<L>> i2 = a.delta().iterator();
    while(i2.hasNext()) {
      Transition<L> t = i2.next() ;
      State bs = conversion.get(t.start()) ;
      State be = conversion.get(t.end()) ;
      if(bs == null || be == null) continue;
      try {
        b.addTransition(new Transition<>(bs, t.label(), be));
      } catch (NoSuchStateException x) {}
    }
    return b ;
  }
}
  
