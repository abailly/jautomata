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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import rationals.Automaton;
import rationals.NoSuchStateException;
import rationals.State;
import rationals.Transition;

/**
 * Removes states that neither accessible nor coaccessible.
 * 
 * @author nono
 * @version $Id: Pruner.java 2 2006-08-24 14:41:48Z oqube $
 */
public class Pruner implements UnaryTransformation {

  public Automaton transform(Automaton a) {
    Map conversion = new HashMap() ;
    Iterator i = a.accessibleAndCoAccessibleStates().iterator();
    Automaton b = new Automaton() ;
    while(i.hasNext()) {
      State e = (State) i.next() ;
      conversion.put(e , b.addState(e.isInitial() , e.isTerminal())) ;
    }
    i = a.delta().iterator();
    while(i.hasNext()) {
      Transition t = (Transition) i.next() ;
      State bs = (State) conversion.get(t.start()) ;
      State be = (State) conversion.get(t.end()) ;
      if(bs == null || be == null)
          continue;
      try {
        b.addTransition(new Transition(
          bs,
          t.label() ,
          be)) ;
      } catch (NoSuchStateException x) {}
    }
    return b ;
  }
}
  
