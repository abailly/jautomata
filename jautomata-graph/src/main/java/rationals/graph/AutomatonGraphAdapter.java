package rationals.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rationals.Automaton;
import rationals.State;

import salvo.jesus.graph.DirectedEdge;
import salvo.jesus.graph.DirectedGraph;
import salvo.jesus.graph.DirectedGraphImplFactory;
import salvo.jesus.graph.Edge;
import salvo.jesus.graph.Graph;
import salvo.jesus.graph.GraphAddEdgeEvent;
import salvo.jesus.graph.GraphAddEdgeListener;
import salvo.jesus.graph.GraphAddVertexEvent;
import salvo.jesus.graph.GraphAddVertexListener;
import salvo.jesus.graph.GraphException;
import salvo.jesus.graph.GraphFactory;
import salvo.jesus.graph.GraphImpl;
import salvo.jesus.graph.GraphModificationException;
import salvo.jesus.graph.GraphRemoveEdgeListener;
import salvo.jesus.graph.GraphRemoveVertexListener;
import salvo.jesus.graph.StopAtVisitor;
import salvo.jesus.graph.algorithm.DepthFirstGraphTraversal;
import salvo.jesus.graph.algorithm.GraphTraversal;
import salvo.jesus.graph.DirectedEdgeImpl;
import rationals.Transition;

/**
 * An adapter class for encapsulating Automata objects as DirectedGraph
 * instances.
 * This adapter is useful for using display and output capabilities of 
 * <a href="http://openjgraph.sourceforge.net">openjgraph</a> package 
 * on automaton objects (without making jautomata core package dependant on
 * openjgraph). <br />
 * <strong>WARNING</strong>: this implementation does not work. It is waiting 
 * some refactoring of OpenJGraph API or some change in support package.
 * @author bailly@lifl.fr
 * @since 2.0
 * @see rationals.Automaton
 */
public class AutomatonGraphAdapter implements DirectedGraph {

  private DirectedGraphImplFactory factory;
  
  private List addvertexlistener;
  
  private List addedgelistener;
  
  private List removevertexlistener;
  
  private List removeedgelistener;
  
  private GraphTraversal traversal;
  
  private Automaton auto;

  public Automaton getAutomaton() {
    return auto;
  }
  
  /**
   * Default constructor.
   * Creates an adapter that wraps the given automaton.
   * 
   * @param auto the Automaton object to wrap. May not be null.
   */
  public AutomatonGraphAdapter(Automaton auto) {
    this.auto = auto;
    /* graph initializations */
    addvertexlistener = new ArrayList(10);
    addedgelistener = new ArrayList(10);
    removevertexlistener = new ArrayList(10);
    removeedgelistener = new ArrayList(10);
    this.factory = new DirectedGraphImplFactory();
    traversal = new DepthFirstGraphTraversal(this);
  }

  
  /////////////////////////////////////////////////////
  // GRAPH METHODS
  /////////////////////////////////////////////////////
  
  /*
   * (non-Javadoc)
   * Passed object must a be a state of the underlying atuomaton.
   * @see salvo.jesus.graph.DirectedGraph#getOutgoingEdges(salvo.jesus.graph.Vertex)
   */
  public List getOutgoingEdges(Object v) {
    List l = new ArrayList();
    for(Iterator i = auto.delta((State)v).iterator();i.hasNext();) {
      Transition tr = (Transition)i.next();
      DirectedEdgeImpl de = new DirectedEdgeImpl(tr.start(),tr.end(),tr);
      l.add(de);
    }
    return l;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see salvo.jesus.graph.DirectedGraph#getIncomingEdges(salvo.jesus.graph.Vertex)
   */
  public List getIncomingEdges(Object v) {
    List res = new ArrayList();
    for(Iterator i = auto.deltaMinusOne((State)v).iterator();i.hasNext();) {
      Transition tr = (Transition)i.next();
      DirectedEdgeImpl de = new DirectedEdgeImpl(tr.end(),tr.start(),tr);
      res.add(de);
    }
    return res;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see salvo.jesus.graph.DirectedGraph#getOutgoingAdjacentVertices(salvo.jesus.graph.Vertex)
   */
  public List getOutgoingAdjacentVertices(Object v) {
    List res = new ArrayList();
    for(Iterator i = auto.delta((State)v).iterator();i.hasNext();) {
      Transition tr = (Transition)i.next();
      res.add(tr.end());
    }
    return res;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see salvo.jesus.graph.DirectedGraph#getIncomingAdjacentVertices(salvo.jesus.graph.Vertex)
   */
  public List getIncomingAdjacentVertices(Object v) {
    List res = new ArrayList();
    for(Iterator i = auto.deltaMinusOne((State)v).iterator();i.hasNext();) {
      Transition tr = (Transition)i.next();
      res.add(tr.end());
    }
    return res;
  }
  
	/*
	 * (non-Javadoc)
	 * 
	 * @see salvo.jesus.graph.DirectedGraph#getEdge(salvo.jesus.graph.Vertex,
	 *      salvo.jesus.graph.Vertex)
	 */
  public DirectedEdge getEdge(Object fromvertex, Object tovertex) {
    List outIncidentEdges;
    Iterator iterator;
    DirectedEdge edge;
    
    // Get the adjacent edge set of the from vertex
    outIncidentEdges = this.getOutgoingEdges(fromvertex);
    
		// Find the edge where the direction is to the tovertex
    iterator = outIncidentEdges.iterator();
    while (iterator.hasNext()) {
      edge = (DirectedEdge) iterator.next();
      if (edge.getSink() == tovertex) {
	// Edge is found.
	iterator = null;
	return edge;
      }
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see salvo.jesus.graph.DirectedGraph#isPath(salvo.jesus.graph.Vertex,
   *      salvo.jesus.graph.Vertex)
   */
  public boolean isPath(Object fromVertex, Object toVertex) {
    List visited = new ArrayList(10);
    
    this.getTraversal().traverse(fromVertex, visited,
				 new StopAtVisitor(toVertex));
    if (toVertex == visited.get(visited.size() - 1))
      return true;
    else
      return false;
    
  }

	/*
	 * (non-Javadoc)
	 * 
	 * @see salvo.jesus.graph.DirectedGraph#isCycle(salvo.jesus.graph.Vertex)
	 */
  public boolean isCycle(Object fromVertex) {
    List outedges = this.getOutgoingEdges(fromVertex);
    Iterator iterator = outedges.iterator();
		DirectedEdge dedge;
		Object adjacentVertex;
		
		// For each outgoing edge of the vertex ...
		while (iterator.hasNext()) {
			dedge = (DirectedEdge) iterator.next();
			// ... get the opposite vertex
			adjacentVertex = dedge.getOppositeVertex(fromVertex);
			// .. and check if there is a path from the opposite vertex back to
			// the vertex
			if (this.isPath(adjacentVertex, fromVertex))
				// There is a cycle
				return true;
		}

		// No cycle
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see salvo.jesus.graph.Graph#getVerticesCount()
	 */
	public int getVerticesCount() {
		return auto.states().size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see salvo.jesus.graph.Graph#getGraphFactory()
	 */
	public GraphFactory getGraphFactory() {
		return this.factory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see salvo.jesus.graph.Graph#setGraphFactory(salvo.jesus.graph.GraphFactory)
	 */
	public void setGraphFactory(GraphFactory factory) {
		this.factory = (DirectedGraphImplFactory) factory;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see salvo.jesus.graph.Graph#add(salvo.jesus.graph.Vertex)
	 */
	public void add(Object v) throws GraphException {
		throw new GraphModificationException(
				"Cannot add vertices : add states to automaton instead");
	}

	/**
	 * @param v
	 */
	private void notifyStateAdd(Object v) {
		GraphAddVertexListener listener;
		Iterator iterator = addvertexlistener.iterator();
		while (iterator.hasNext()) {
			listener = (GraphAddVertexListener) iterator.next();
			listener.vertexAdded(new GraphAddVertexEvent(this, v));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see salvo.jesus.graph.Graph#remove(salvo.jesus.graph.Vertex)
	 */
	public void remove(Object v) throws GraphException {
		throw new GraphModificationException(
				"Cannot modify this graph. Use automaton interface instead");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see salvo.jesus.graph.Graph#getVerticesIterator()
	 */
	public Iterator getVerticesIterator() {
		return auto.states().iterator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see salvo.jesus.graph.Graph#cloneVertices()
	 */
	public List cloneVertices() {
		Iterator it = auto.states().iterator();
		List vertices = new ArrayList();
		while (it.hasNext()) {
			vertices.add((Object) it.next());
		}
		return vertices;
	}

	/*
	 * 
	 * (non-Javadoc)
	 * 
	 * @see salvo.jesus.graph.Graph#getAllVertices()
	 */
	public List getAllVertices() {
		return cloneVertices();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see salvo.jesus.graph.Graph#getAllEdges()
	 */
  public Collection getAllEdges() {
    List l = new ArrayList();
    for(Iterator i = auto.delta().iterator();i.hasNext();) {
      Transition tr = (Transition)i.next();
      DirectedEdgeImpl de = new DirectedEdgeImpl(tr.start(),tr.end(),tr);
      l.add(de);
    }
    return l;
  }

	/*
	 * (non-Javadoc)
	 * 
	 * @see salvo.jesus.graph.Graph#getEdgesCount()
	 */
	public int getEdgesCount() {
		return auto.delta().size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see salvo.jesus.graph.Graph#createEdge(salvo.jesus.graph.Vertex,
	 *      salvo.jesus.graph.Vertex)
	 */
	public Edge createEdge(Object v1, Object v2) {
	  return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see salvo.jesus.graph.Graph#addEdge(salvo.jesus.graph.Vertex,
	 *      salvo.jesus.graph.Vertex)
	 */
	public Edge addEdge(Object v1, Object v2) throws GraphException {
		throw new GraphModificationException(
				"Cannot modify this graph. Use automaton interface instead");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see salvo.jesus.graph.Graph#addEdge(salvo.jesus.graph.Edge)
	 */
	public void addEdge(Edge e) throws GraphException {
		throw new GraphModificationException(
				"Cannot modify this graph. Use automaton interface instead");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see salvo.jesus.graph.Graph#removeEdge(salvo.jesus.graph.Edge)
	 */
	public void removeEdge(Edge e) throws GraphException {
		throw new GraphModificationException(
				"Cannot modify this graph. Use automaton interface instead");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see salvo.jesus.graph.Graph#removeEdges(salvo.jesus.graph.Vertex)
	 */
	public void removeEdges(Object v) throws GraphException {
		throw new GraphModificationException(
				"Cannot modify this graph. Use automaton interface instead");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see salvo.jesus.graph.Graph#getDegree()
	 */
	public int getDegree() {
		State v;
		HashSet set;

		set = new HashSet(auto.states());
		if (set.size() > 0) {
			v = (State) Collections.max(set, new Comparator() {
				public int compare(Object obj1, Object obj2) {
					Object v1 = (Object) obj1, v2 = (Object) obj2;
					int countv1 = getDegree(v1);
					int countv2 = getDegree(v2);

					if (countv1 < countv2)
						return -1;
					if (countv1 > countv2)
						return 1;
					else
						return 0;
				}

				public boolean equals(Object objcomparator) {
					return objcomparator.equals(this);
				}
			});
			return this.getEdges((Object) v).size();
		} else
			return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see salvo.jesus.graph.Graph#getDegree(salvo.jesus.graph.Vertex)
	 */
	public int getDegree(Object v) {
		return this.getEdges(v).size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see salvo.jesus.graph.Graph#getVertices(int)
	 */
	public Set getVertices(int degree) {
		Set verticesofsamedegree = new HashSet();
		Iterator iterator;
		Object vertex;

		iterator = auto.states().iterator();
		while (iterator.hasNext()) {
			vertex = (Object) iterator.next();
			if (this.getAdjacentVertices(vertex).size() == degree)
				verticesofsamedegree.add(vertex);
		}

		return verticesofsamedegree;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see salvo.jesus.graph.Graph#getEdges(salvo.jesus.graph.Vertex)
	 */
	public List getEdges(Object v) {
		List l = getOutgoingEdges(v);
		//	  l.addAll(getIncomingEdges(v));
		return l;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see salvo.jesus.graph.Graph#getAdjacentVertices(salvo.jesus.graph.Vertex)
	 */
	public List getAdjacentVertices(Object v) {
		List res = new ArrayList();
		List out = getOutgoingAdjacentVertices(v);
		List in = getIncomingAdjacentVertices(v);
		res.addAll(out);
		res.addAll(in);
		return res;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see salvo.jesus.graph.Graph#getAdjacentVertices(java.util.List)
	 */
	public HashSet getAdjacentVertices(List vertices) {
		HashSet res = new HashSet();
		Iterator it = vertices.iterator();
		Object current;
		List adjv;
		Iterator adjvIt;
		while (it.hasNext()) {
			current = (Object) it.next();
			adjv = this.getAdjacentVertices(current);
			adjvIt = adjv.iterator();
			while (adjvIt.hasNext()) {
				res.add(adjvIt.next());
			}
		}
		return res;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see salvo.jesus.graph.Graph#getConnectedSet()
	 */
	public List getConnectedSet() {
		ArrayList al = new ArrayList();
		al.add(new ArrayList(auto.states()));
		return al;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see salvo.jesus.graph.Graph#getConnectedSet(salvo.jesus.graph.Vertex)
	 */
	public List getConnectedSet(Object v) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see salvo.jesus.graph.Graph#mergeconnectedSet(salvo.jesus.graph.Vertex,
	 *      salvo.jesus.graph.Vertex)
	 */
	public void mergeconnectedSet(Object v1, Object v2) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see salvo.jesus.graph.Graph#traverse(salvo.jesus.graph.Vertex)
	 */
	public List traverse(Object startat) {
		return traversal.traverse(startat);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see salvo.jesus.graph.Graph#getTraversal()
	 */
	public GraphTraversal getTraversal() {
		return this.traversal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see salvo.jesus.graph.Graph#setTraversal(salvo.jesus.graph.algorithm.GraphTraversal)
	 */
	public void setTraversal(GraphTraversal traversal) {
		this.traversal = traversal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see salvo.jesus.graph.Graph#isConnected(salvo.jesus.graph.Vertex,
	 *      salvo.jesus.graph.Vertex)
	 */
	public boolean isConnected(Object v1, Object v2) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see salvo.jesus.graph.Graph#addGraphAddVertexListener(salvo.jesus.graph.GraphAddVertexListener)
	 */
	public void addGraphAddVertexListener(GraphAddVertexListener listener) {
		addvertexlistener.add(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see salvo.jesus.graph.Graph#addGraphAddEdgeListener(salvo.jesus.graph.GraphAddEdgeListener)
	 */
	public void addGraphAddEdgeListener(GraphAddEdgeListener listener) {
		addedgelistener.add(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see salvo.jesus.graph.Graph#addGraphRemoveEdgeListener(salvo.jesus.graph.GraphRemoveEdgeListener)
	 */
	public void addGraphRemoveEdgeListener(GraphRemoveEdgeListener listener) {
		removeedgelistener.add(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see salvo.jesus.graph.Graph#addGraphRemoveVertexListener(salvo.jesus.graph.GraphRemoveVertexListener)
	 */
	public void addGraphRemoveVertexListener(GraphRemoveVertexListener listener) {
		removevertexlistener.add(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see salvo.jesus.graph.Graph#removeGraphAddVertexListener(salvo.jesus.graph.GraphAddVertexListener)
	 */
	public void removeGraphAddVertexListener(GraphAddVertexListener listener) {
		addvertexlistener.remove(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see salvo.jesus.graph.Graph#removeGraphAddEdgeListener(salvo.jesus.graph.GraphAddEdgeListener)
	 */
	public void removeGraphAddEdgeListener(GraphAddEdgeListener listener) {
		addedgelistener.remove(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see salvo.jesus.graph.Graph#removeGraphRemoveEdgeListener(salvo.jesus.graph.GraphRemoveEdgeListener)
	 */
	public void removeGraphRemoveEdgeListener(GraphRemoveEdgeListener listener) {
		removeedgelistener.remove(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see salvo.jesus.graph.Graph#removeGraphRemoveVertexListener(salvo.jesus.graph.GraphRemoveVertexListener)
	 */
	public void removeGraphRemoveVertexListener(
			GraphRemoveVertexListener listener) {
		removevertexlistener.remove(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see salvo.jesus.graph.Graph#same()
	 */
	public Graph same() {
		Graph g = new GraphImpl();
		g.setGraphFactory(this.getGraphFactory());
		return g;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see salvo.jesus.graph.Graph#findVertex(java.lang.Object)
	 */
	public Object findVertex(Object o) {
		return o;
	}

}
