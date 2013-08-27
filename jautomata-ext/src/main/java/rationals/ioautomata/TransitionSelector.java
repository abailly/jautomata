/**
 *  Copyright (C) 2007 - OQube / Arnaud Bailly
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA

 Created 1 juin 07
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
    Set<Transition> trs = automaton.delta(state);
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
    Set<Transition> trs = automaton.delta(state);
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
