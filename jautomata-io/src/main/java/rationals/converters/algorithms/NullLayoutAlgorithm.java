/*
 * (C) Copyright 2002 Arnaud Bailly (arnaud.oqube@gmail.com),
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
package rationals.converters.algorithms;

import rationals.Automaton;
import rationals.State;
import rationals.converters.ConverterException;

import java.util.Iterator;

/**
 * This layout algorithm basically performs nothing.
 * 
 * @author bailly
 * @version Jul 23, 2002
 * 
 */
public final class NullLayoutAlgorithm extends AbstractLayoutAlgorithm {

	private Automaton automata;
	
	/**
	 * The layout initializes a private Map from coordinates to state
	 * where all coordinates are set to (0,0)
	 * 
	 * @see rationals.converters.algorithms.LayoutAlgorithm#layout(Automaton)
	 */
	public void layout(Automaton aut) throws ConverterException {
		automata = aut;
		// create a coord object for each state with default values
		Iterator it = automata.states().iterator();
		while (it.hasNext()) {
			Coord c = new Coord(0, 0);
			statesCoord.put(c, (State) it.next());
		}
	}

	/**
	 * This methods does nothing
	 * 
	 * @see rationals.converters.algorithms.LayoutAlgorithm#work()
	 */
	public void work() {
	}

	/**
	 * This method always returns true
	 * 
	 * @see rationals.converters.algorithms.LayoutAlgorithm#done()
	 */
	public boolean done() {
		return true;
	}


}
