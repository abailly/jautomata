package rationals.converters;


/**
 * This is the base interface for displaying automatons in 
 * windows. 
 * 
 * @author bailly
 * @version 23 July 2002
 */
public interface GraphicsDisplayer extends Displayer {

	/**
	 * Asks the Displayer to redraw the Automaton on the given Graphics2D object
	 * @param a Graphics2D object
	 */
	public void draw(java.awt.Graphics2D gs);

}
