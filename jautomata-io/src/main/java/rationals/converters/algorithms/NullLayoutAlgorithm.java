package rationals.converters.algorithms;

import java.util.Iterator;

import rationals.Automaton;
import rationals.State;
import rationals.converters.ConverterException;

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
