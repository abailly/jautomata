package rationals.converters;

import rationals.Automaton;
import rationals.converters.algorithms.LayoutAlgorithm;

/**
 * This is the base inteface for all devices used to display automata.
 * This interface is used to display a graph representation of an 
 * Automaton object, not an encoding of its structure. It should 
 * not be confused with the interface {@see rationals.converters.StreamEncoder}
 * which used to serialize the semantics of the Automaton.
 * 
 * @author bailly
 * @version 23072002
 */ 
public interface Displayer {

	/**
	 * Defines the Automaton to display with this displayer
	 * 
	 * @param an Automaton object
	 */
	public void setAutomaton(Automaton a) throws ConverterException;

	/**
	 * Defines the algorithm to use for layout
	 * 
	 * @param an LyaoutAlgorithm object. May not be null.
	 */
	public void setAlgorithm(
		LayoutAlgorithm algo);

	/**
	 * Asks the displayer to display the automaton 
	 * 
	 */
	public void display() throws ConverterException;

}
