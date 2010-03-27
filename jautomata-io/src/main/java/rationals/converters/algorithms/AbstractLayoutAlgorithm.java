package rationals.converters.algorithms;

import java.util.HashMap;
import java.util.Map;

import rationals.Automaton;
import rationals.converters.ConverterException;

/**
 * Base class for algorithms. Implements tweak method for 
 * handling parameter setting by user
 *
 * @author Arnaud Bailly
 * @version 22032002
 */
public abstract class AbstractLayoutAlgorithm implements LayoutAlgorithm {

    protected Map statesCoord =  new HashMap();

	/**
	 * Sets implementation defined parameters for algorithm
	 *
	 * @param prop property name to set
	 * @param val float value 
	 */
	public void tweak(String prop, double val) {
		tweak(prop, new Double(val));
	}

	/**
	 * Sets implementation defined parameters for algorithm
	 *
	 * @param prop property name to set
	 * @param val int value 
	 */
	public void tweak(String prop, int val) {
		tweak(prop, new Integer(val));
	}

	public void tweak(String prop, Object val) {
		// recupere les informations sur les proprietes
		try {
			java.beans.BeanInfo info =
				java.beans.Introspector.getBeanInfo(
					this.getClass(),
					AbstractLayoutAlgorithm.class);
			java.beans.PropertyDescriptor[] props =
				info.getPropertyDescriptors();
			// parcours les proprietes et essaye de les mettre a jour en fonction des parametres
			for (int i = 0; i < props.length; i++) {
				if (props[i].getName().equals(prop)) {
					System.err.println(
						"DEBUG >>>> tweaking " + prop + " -> " + val);
					try {
						java.lang.reflect.Method wmeth =
							props[i].getWriteMethod();
						wmeth.invoke(this, new Object[] { val });
					} catch (Exception ex) {
						System.err.println("DEBUG >>>> Caught exception " + ex);
					}
				}
			}
		} catch (java.beans.IntrospectionException ex) {
			System.err.println("Error in introspecting : " + ex);
		}
	}

	/**
	 * list tweakable properties 
	 *
	 * @return a map of String/class usable properties
	 */
	public java.util.Map allParameters() {
		// recupere les informations sur les proprietes
		try {
			java.util.Map propmap = new java.util.HashMap();
			java.beans.BeanInfo info =
				java.beans.Introspector.getBeanInfo(
					this.getClass(),
					AbstractLayoutAlgorithm.class);
			java.beans.PropertyDescriptor[] props =
				info.getPropertyDescriptors();
			// parcours les proprietes et essaye de les mettre a jour en fonction des parametres
			for (int i = 0; i < props.length; i++)
				propmap.put(props[i].getName(), props[i].getPropertyType());
			return propmap;
		} catch (java.beans.IntrospectionException ex) {
			System.err.println("Error in introspecting : " + ex);
			return new java.util.HashMap();
		}
	}

	/**
	 * @see rationals.converters.algorithms.LayoutAlgorithm#done()
	 */
	public boolean done() {
		return false;
	}

	/**
	 * @see rationals.converters.algorithms.LayoutAlgorithm#layout(Automaton)
	 */
	public void layout(Automaton aut) throws ConverterException {
	}

	/**
	 * @see rationals.converters.algorithms.LayoutAlgorithm#state()
	 */
	public Map getState() {
		return statesCoord;
	}

	/**
	 * @see rationals.converters.algorithms.LayoutAlgorithm#work()
	 */
	public void work() {
	}

	/**
	 * @see rationals.converters.algorithms.LayoutAlgorithm#setState(Map)
	 */
	public void setState(Map m) {
		statesCoord = m;
	}

}
