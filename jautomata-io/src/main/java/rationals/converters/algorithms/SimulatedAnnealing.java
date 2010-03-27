package rationals.converters.algorithms;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import rationals.State;
import rationals.Transition;

/**
 * Implementation of simulated annealing for graph drawing<p>
 * Transitions are spring that attracts nodes.
 *
 * @author Arnaud Bailly
 * @version 22032002
 */
public class SimulatedAnnealing extends AbstractLayoutAlgorithm {

	/** random move factor */
	private double randomMove = 0.1;

	/** width of display */
	private double width = 200;

	/** height of display */
	private double height = 200;

	/** maximum distance */
	private double expectedDist = 70;

	/** attractive force */
	private double attractiveForce = 1;

	/** cooling factor */
	private double coolFactor = 0.005;

	/** cooling threshold */
	private double coolThreshold = 1;

	/** temperature */
	private double temperature = 70;

	/** current temperature */
	private double currentTemperature = temperature;

	/** map of states to movement vector */
	private Map movesCoord = new HashMap();

	/** set of transitions */
	private Set transitions;

	/** automaton to display */
	private rationals.Automaton automata;

	/////////////////////////////////////////////////////:
	// CONSTRUCTORS
	/////////////////////////////////////////////////////

	/////////////////////////////////////////////////////:
	// ACCESSORS AND MUTATORS
	////////////////////////////////////////////////////

	public double getCoolFactor() {
		return coolFactor;
	}

	public void setCoolFactor(double cf) {
		coolFactor = cf;
	}

	public double getCoolThreshold() {
		return coolThreshold;
	}

	public void setCoolThreshold(double ct) {
		coolThreshold = ct;
	}

	public double getTemperature() {
		return temperature;
	}

	public void setTemperature(double temp) {
		temperature = temp;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double temp) {
		width = temp;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double temp) {
		height = temp;
	}

	public double getExpectedDist() {
		return expectedDist;
	}

	public void setExpectedDist(double temp) {
		expectedDist = temp;
	}

	public double getAttractiveForce() {
		return attractiveForce;
	}

	public void setAttractiveForce(double temp) {
		attractiveForce = temp;
	}

	///////////////////////////////////////////////////
	// PUBLIC METHODS
	////////////////////////////////////////////////////

	/**
	 * calculate display of automaton
	 *
	 * @param aut an Automaton object
	 */
	public void layout(rationals.Automaton aut)
		throws rationals.converters.ConverterException {
		automata = aut;
		transitions = aut.delta();
		// initialise 
		Iterator it = aut.states().iterator();
		while (it.hasNext()) {
			State s = (State) it.next();
			statesCoord.put(
				s,
				new Coord(Math.random() * width, Math.random() * height));
			movesCoord.put(s, new Coord(0, 0));
		}
	}

	public void work() {
		if (currentTemperature > coolThreshold) {
			move();
			currentTemperature = currentTemperature * (1 - coolFactor);
		}
	}

	public boolean done() {
		return currentTemperature < coolThreshold;
	}

	////////////////////////////////////////////////////
	// PRIVATE METHODS
	///////////////////////////////////////////////////

	// returns true if the two states are connected by at least one transition
	private boolean areConnected(State s1, State s2) {
		Iterator it = transitions.iterator();
		while (it.hasNext()) {
			Transition trans = (Transition) it.next();
			if ((trans.start().equals(s1) && trans.end().equals(s2))
				|| (trans.start().equals(s2) && trans.end().equals(s1)))
				return true;
		}
		return false;
	}

	/**
	 * a basic move of automaton
	 */
	private void move() {
		Iterator it = statesCoord.keySet().iterator();
		// calcul d'attraction
		while (it.hasNext()) {
			State s1 = (State) it.next();
			Iterator it2 = statesCoord.keySet().iterator();
			while (it2.hasNext()) {
				State s2 = (State) it2.next();
				if (s1.equals(s2))
					continue;
				Coord s1coord = (Coord) statesCoord.get(s1);
				Coord s2coord = (Coord) statesCoord.get(s2);
				Coord s1mcoord = (Coord) movesCoord.get(s1);
				double dist = s1coord.distance(s2coord);
				Coord vec = s1coord.vector(s2coord);
				// calculate move for s1
				vec.div(dist);
				if (areConnected(s1, s2)) {
					double attract =
						attractiveForce * Math.log(dist / expectedDist);
					// adjust vector
					vec.mul(attract);
					// System.err.println("coeff :" +coeff+",vector :"+vec);
					// adjust states moves
					vec.mul(currentTemperature);
					s1mcoord.add(vec);
				}
				vec = s1coord.vector(s2coord);
				vec.div(dist);
				double repulse = (expectedDist * expectedDist) / (dist * dist);
				// adjust vector
				vec.mul(repulse);
				//      System.err.println("coeff :" +coeff+",vector :"+vec);
				// adjust states moves
				vec.mul(currentTemperature);
				s1mcoord.sub(vec);
			}
		}
		// apply moves to coordinates
		it = statesCoord.keySet().iterator();
		while (it.hasNext()) {
			State s = (State) it.next();
			Coord c = (Coord) statesCoord.get(s);
			Coord m = (Coord) movesCoord.get(s);
			// move towards barycenter

			c.add(m);
			// System.err.println(s+" : "+c+" ->"+m);
			m.x = m.y = 0.0; // reset move
		}
	}

	/**
	 * @return
	 */
	public double getRandomMove() {
		return randomMove;
	}

	/**
	 * @param d
	 */
	public void setRandomMove(double d) {
		randomMove = d;
	}

}
