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
package rationals.ioautomata;

import rationals.State;
import rationals.Transition;
import rationals.ioautomata.IOTransition.IOLetter;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

/**
 * Selects an arbitrary transition of a given type for an automaton.
 * 
 * @author nono
 * 
 */
public class TransitionSelector {

  private IOAlphabetType type;

  private Random rand = new Random();

  public TransitionSelector(IOAlphabetType output) {
    this.type = output;
  }

  public IOLetter selectFrom(IOAutomaton<IOTransition,IOTransitionBuilder> automaton,
      Set<State> state) {
    Set<Transition<Object>> trs = automaton.delta(state);
    for (Iterator i = trs.iterator(); i.hasNext();)
      if (((IOTransition) i.next()).getType() != type)
        i.remove();
    if (trs.size() == 0)
      return null;
    int r = rand.nextInt(trs.size());
    IOTransition tr = null;
    for (Iterator it = trs.iterator(); r >= 0; r--)
      tr = (IOTransition) it.next();
    return (IOLetter) tr.label();
  }

  /**
   * Return ALL transitions of this selector's type available in current state.
   * 
   * @param automaton
   * @param state
   * @return
   */
  public Set<IOLetter> selectAllFrom(IOAutomaton<IOTransition,IOTransitionBuilder> automaton,
      Set<State> state) {
    Set<Transition<Object>> trs = automaton.delta(state);
    Set<IOTransition.IOLetter> letters = new HashSet<IOLetter>();
    for (Iterator i = trs.iterator(); i.hasNext();) {
      IOTransition iot = (IOTransition) i.next();
      if ((iot).getType() != type)
        i.remove();
      else
        letters.add((IOLetter) iot.label());
    }
    return letters;
  }

}
