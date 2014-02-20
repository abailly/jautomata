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
package rationals.converters;

import rationals.Automaton;
import rationals.State;
import rationals.Transition;

import java.util.Iterator;

public class Xamd implements ToString {
  public String toString(Automaton a) {
    StringBuffer sb = new StringBuffer("") ;
    int nbe = a.states().size() ;
    int nbl = 0 ;
    Iterator i = a.alphabet().iterator() ;
    while (i.hasNext()) {
      int k = i.next().toString().charAt(0) - 'a' + 1 ;
      if (k > nbl) nbl = k ;
    }
    State[] stateNums = new State[nbe] ;
    i = a.states().iterator() ;
    while(i.hasNext()) {
      State e = (State) i.next() ;
      int num = Integer.parseInt(e.toString()) ;
      stateNums[num] = e ;
    }
    sb.append(nbe + 1).append('\n') ;
    sb.append(nbl).append('\n') ;
    sb.append("1\n\n1\n\n").append(nbe +1).append("\n") ;
    for (int k = 0 ; k < nbe ; k++) {
      if (stateNums[k].isTerminal()) {
        sb.append("1\n") ;
      } else {
        sb.append("0\n") ;
      }
    }
    sb.append("0\n") ;
    sb.append(a.delta().size() + a.initials().size()).append('\n') ;
    i = a.delta().iterator() ;
    while (i.hasNext()) {
      Transition t = (Transition) i.next() ;
      int i1 = Integer.parseInt(t.start().toString()) + 1 ;
      int i2 = Integer.parseInt(t.end().toString()) + 1 ;
      int i3 = t.label().toString().charAt(0) - 'a' + 1 ;
      sb.append(i1).append('\n') ;
      sb.append(i2).append('\n') ;
      sb.append(i3).append('\n') ;
    }
    for (int j = 0 ; j < nbe ; j++) {
      if (stateNums[j].isInitial()) {
      int i1 = nbe + 1 ;
      int i2 = Integer.parseInt(stateNums[j].toString()) + 1 ;
      int i3 = nbl + 1 ;
      sb.append(i1).append('\n') ;
      sb.append(i2).append('\n') ;
      sb.append(i3).append('\n') ;
      }
    }
    return sb.toString() ;
  }
    
}
