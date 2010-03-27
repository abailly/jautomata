package rationals.converters;

import java.util.Iterator;

import rationals.Automaton;
import rationals.State;
import rationals.Transition;

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
