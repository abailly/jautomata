package rationals.converters.algorithms;

import java.util.Map;

/**
 * The base interface for all DisplayAlgorithms. A LayoutAlgorithm is
 * used to compute layout for an Automaton object. The normal way to use it
 * is : 
 * <ul>
 * <li>define the automaton to layout with layout(Automaton a) ;</li>
 * <li>repeatedly call work() while done() is false ;</li>
 * <li>retrieve computed coordinates for states with state().</li>
 * </ul>
 * <p>
 * Methods work() and done() can be used to compute layout incrementally for
 * algorithm which supports this concept.
 * <p>
 * Methods tweak(...) are used to handle customization of algortithms parameters
 * generically with tools or command line parameters.
 * 
 * @author Arnaud Bailly 
 * @version 22032002
 */
public interface LayoutAlgorithm {

	/**
	 * starts display of automaton
	 *
	 * @param aut rationals.Automaton to display
	 * @return a Map of states to coords 
	 */
	public void layout(rationals.Automaton aut)
		throws rationals.converters.ConverterException;

	/**
	 * Method called by clients to ask the algorithm to perform
	 * an incremental work. The definition of an increment of work
	 * is algorithm dependent
	 */
	public void work();

	/**
	 * This methods should be used by clients of algorithms to check
	 * termination of algorithm
	 * 
	 * @return true if algorithm has terminated, false otherwise
	 * */
	public boolean done();

	/**
	 * Retrieve the current state of the display 
	 * 
	 * @return a map from State objects to Coord objects
	 */
	public java.util.Map getState();

	/**
	 * Sets the current state of the layout
	 * 
	 * @param a Map from Coord objects to State
	 */
	public void setState(Map m);
	
	/**
	 * Sets implementation defined parameters for algorithm
	 *
	 * @param prop property name to set
	 * @param val an Object used to define property
	 */
	public void tweak(String prop, Object val);

	/**
	 * Sets implementation defined parameters for algorithm
	 *
	 * @param prop property name to set
	 * @param val float value 
	 */
	public void tweak(String prop, double val);

	/**
	 * Sets implementation defined parameters for algorithm
	 *
	 * @param prop property name to set
	 * @param val int value 
	 */
	public void tweak(String prop, int val);

	/**
	 * list tweakable properties 
	 *
	 * @return a map of String/class usable properties
	 */
	public java.util.Map allParameters();

}
