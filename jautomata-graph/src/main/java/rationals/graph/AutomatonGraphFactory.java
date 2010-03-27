package rationals.graph;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import rationals.Automaton;
import rationals.State;
import rationals.Transition;
import salvo.jesus.graph.DirectedEdgeImpl;
import salvo.jesus.graph.DirectedGraph;
import salvo.jesus.graph.DirectedGraphImpl;
import salvo.jesus.graph.Graph;

/**
 * A factory class to link automaton objects to Graph objects
 *
 * This class is used to construct a Graph from an Automaton. It
 * only contains static helper methods.
 *
 * @author Arnaud Bailly
 * @version 31082002
 */
public abstract class AutomatonGraphFactory {

	// static cache of automatons
	private static Map automataCache = new java.util.HashMap();

	private static List automatas = new java.util.ArrayList();

	/////////////////////////////////////////////////////
	// STATIC METHODS
	/////////////////////////////////////////////////////

	/**
	 * Constructs a Graph given an Automaton.
	 * 
	 * This method constructs a Graph from an Automaton. The vertices
	 * of the graph encapsulates states of the Automaton
	 *
	 * @param automaton the Automaton object to convert
	 * @return an instance of DirectedGraphImpl 
	 * @see salvo.jesus.graph.Graph
	 * @see salvo.jesus.graph.DirectedGraphImpl
	 * @see rationals.Automaton
	 */
	public static Graph makeGraph(Automaton automaton) throws Exception {
		// look in cache first
		DirectedGraphImpl dgraph =
			(DirectedGraphImpl) automataCache.get(automaton);
		if (dgraph == null) {
			dgraph = new DirectedGraphImpl();
			makeGraph(dgraph, automaton);
		}
		return dgraph;
	}

	private static void makeGraph(DirectedGraph dgraph, Automaton automaton)
		throws Exception {
		// map from states to vertices
		Map s2v = new HashMap();
		// add states
		Iterator stit = automaton.states().iterator();
		while (stit.hasNext()) {
			State s = (State) stit.next();
			Object v = s;
			dgraph.add(v);
			s2v.put(s, v);
		}
		// add edges
		Iterator trit = automaton.delta().iterator();
		while (trit.hasNext()) {
			Transition tr = (Transition) trit.next();
			dgraph.addEdge(
				new DirectedEdgeImpl(
					 s2v.get(tr.start()),
					 s2v.get(tr.end()),
					tr.label()));
		}
		automataCache.put(automaton, dgraph);
	}

	/**
	 * Constructs a Graph given an array of Automata.
	 * 
	 * This method constructs a Graph from an array of  Automata. The vertices
	 * of the graph encapsulates states of the Automata
	 *
	 * @param automata the Automata  to convert
	 * @return an instance of DirectedGraphImpl 
	 * @see salvo.jesus.graph.Graph
	 * @see salvo.jesus.graph.DirectedGraphImpl
	 * @see rationals.Automaton
	 */
	public static Graph makeGraph(Automaton[] automata) throws Exception {
		DirectedGraphImpl dgraph = null;
		// try in cache with first automata as key
		if (automata.length > 0)
			dgraph = (DirectedGraphImpl) automataCache.get(automata[0]);
		if (dgraph == null) {
			dgraph = new DirectedGraphImpl();
			for (int i = 0; i < automata.length; i++)
				makeGraph(dgraph, automata[i]);
		}
		return dgraph;
	}

	public static Automaton setGraph(Automaton automaton) {
		setgraph(automaton);
		return automaton;
	}

	public static Automaton setGraph(Automaton[] automata) {
		Automaton res = null;
		if (automata.length > 0)
			res = automata[0];
		if (res == null) {
			res = new Automaton();
			for (int i = 0; i < automata.length; i++) {
				setGraph(automata[i]);
			}
		}
		return res;

	}

	public static void setgraph(Automaton automaton) {
		automatas.add(automaton);
	}

}
