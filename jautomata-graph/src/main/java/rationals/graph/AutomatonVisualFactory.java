package rationals.graph;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import rationals.Automaton;
import rationals.Transition;
import salvo.jesus.graph.Edge;
import salvo.jesus.graph.visual.Arrowhead;
import salvo.jesus.graph.visual.VisualEdge;
import salvo.jesus.graph.visual.VisualGraph;
import salvo.jesus.graph.visual.VisualGraphComponent;
import salvo.jesus.graph.visual.VisualGraphComponentFactory;
import salvo.jesus.graph.visual.VisualVertex;
import salvo.jesus.graph.visual.drawing.Painter;
import salvo.jesus.graph.visual.drawing.VisualDirectedEdgePainterImpl;
import salvo.jesus.graph.visual.drawing.VisualEdgePainter;
import salvo.jesus.graph.visual.drawing.VisualVertexPainterFactory;
import salvo.jesus.graph.visual.layout.DigraphLayeredLayout;
import salvo.jesus.graph.visual.layout.GraphLayoutManager;
import salvo.jesus.graph.visual.print.VisualGraphImageOutput;
import salvo.jesus.util.VisualGraphComponentPath;

/**
 * A class to produce VisualGraphComponent from Automaton states and
 * Transitions.
 * 
 * This class creates VisualVertex and VisualEdges according to the following
 * rules :
 * <ul>
 * <li>a state is rendered as a round Node ;</li>
 * <li>an initial State is rendered as an inverted round node ;</li>
 * <li>a terminal State has an outward arrow</li>
 * </ul>
 * The background color, foreground color and font used to render edges and
 * vertices can be specified using the appropriate methods
 * 
 * @author Arnaiud Bailly
 * @version 31082002
 * @see salvo.jesus.graph.visual.VisualGraphComponentFactory
 */
public class AutomatonVisualFactory extends VisualVertexPainterFactory
    implements VisualGraphComponentFactory {

  // random seed
  private java.util.Random rand = new java.util.Random(new java.util.Date()
      .getTime());

  // color to use for background
  private Color vertexBackground = Color.white;

  // color to use for foreground
  private Color vertexForeground = Color.black;

  // font to use
  private Font vertexFont = new Font("Helvetica", Font.BOLD, 9);

  // color for edges
  private Color edgeColor = Color.black;

  // font for edges
  private Font edgeFont = new Font("Helvetica", Font.PLAIN, 10);

  // fontcolor for edges
  private Color edgeFontColor = Color.black;

  private MouseListener mouseListener;

  private MouseMotionListener mouseMotionListener;

  private StatePainter1 statePainter = new StatePainter1();

  private VisualEdgePainter edgePainter = new VisualDirectedEdgePainterImpl();

  private Map /* < Transition, VisualEdge > */vemap = new HashMap();

  // ////////////////////////////////////////////////////////
  // PUBLIC METHODS
  // ////////////////////////////////////////////////////////

  /**
   * Constructor AutomatonVisualFactory with a MouseListener
   * 
   * @param popupDisplayer
   */
  public AutomatonVisualFactory(MouseListener ml, MouseMotionListener mml) {
    this.mouseListener = ml;
    this.mouseMotionListener = mml;
  }

  public AutomatonVisualFactory() {
  }

  public Color getVertexBackground() {
    return vertexBackground;
  }

  public void setVertexBackground(Color color) {
    // System.out.println("[AutomatonVisualFactory] color : "+color);
    this.vertexBackground = color;
  }

  public Color getVertexForeground() {
    return vertexForeground;
  }

  public void setVertexForeground(Color color) {
    this.vertexForeground = color;
  }

  public Font getVertexFont() {
    return vertexFont;
  }

  public void setVertexFont(Font font) {
    this.vertexFont = font;
  }

  public Color getEdgeColor() {
    return edgeColor;
  }

  public void setEdgeColor(Color color) {
    edgeColor = color;
  }

  public Color getEdgeFontColor() {
    return edgeFontColor;
  }

  public void setEdgeFontColor(Color color) {
    this.edgeFontColor = color;
  }

  public Font getEdgeFont() {
    return edgeFont;
  }

  public void setEdgeFont(Font font) {
    edgeFont = font;
  }

  /**
   * The given Vertex must have been created containing an instance of State
   * 
   * @see salvo.jesus.graph.visual.VisualGraphComponentFactory#createVisualVertex
   */
  public VisualVertex createVisualVertex(Object vertex, VisualGraph graph) {
    // create VisualVertex
    VisualVertex vv = new VisualVertex(vertex, graph);
    vv.setPainter(statePainter);
    vv.setShape(new VisualGraphComponentPath(new GeneralPath(new Arc2D.Double(
        0, 0, 30, 30, 0, 360, Arc2D.CHORD))));
    // vv.setGeneralPath(new GeneralPath(new Arc2D.Double(0, 0, 20, 20, 0,
    // 360,
    // Arc2D.CHORD)));
    vv.setFillcolor(vertexForeground);
    vv.setFontcolor(vertexBackground);
    vv.setOutlinecolor(vertexForeground);
    vv.setFont(vertexFont);
    vv.setLocation(rand.nextInt(500), rand.nextInt(400));
    if (mouseListener != null)
      vv.addMouseListener(mouseListener);
    if (mouseMotionListener != null)
      vv.addMouseMotionListener(mouseMotionListener);
    return vv;
  }

  /**
   * @see salvo.jesus.graph.visual.VisualGraphComponentFactory#createVisualEdge
   */
  public VisualEdge createVisualEdge(Edge edge, VisualGraph graph) {
    /* merge edges with same source and sink */
    Automaton a = ((AutomatonGraphAdapter) graph.getGraph()).getAutomaton();
    VisualEdge ve = (VisualEdge) vemap.get(edge);
    if (ve != null)
      return ve;
    else
      ve = new VisualEdge(edge, graph);
    vemap.put(edge, ve);
    Transition tr = (Transition)edge.getData();
    StringBuffer lbl = new StringBuffer(tr.label() != null ? tr.label()
        .toString() : "");
    Set s = a.delta(tr.start());
    for (Iterator i = s.iterator(); i.hasNext();) {
      Transition t = (Transition) i.next();
      if (t != tr && t.end().equals(tr.end())) {
        lbl.append('+').append(t.label());
        vemap.put(t, ve);
      }
    }

    ve.setFontcolor(edgeFontColor);
    ve.setOutlinecolor(edgeColor);
    ve.setFillcolor(edgeColor);
    ve.setFont(edgeFont);
    ve.setText(lbl.toString());
    ve.setPainter(edgePainter);
    return ve;
  }

  /**
   * @see salvo.jesus.graph.visual.VisualGraphComponentFactory#createArrowhead
   */
  public Arrowhead createArrowhead() {
    return new AutomatonArrowhead();
  }

  /*
   * (non-Javadoc)
   * 
   * @see salvo.jesus.graph.visual.drawing.PainterFactory#getPainter(salvo.jesus.graph.visual.VisualGraphComponent)
   */
  public Painter getPainter(VisualGraphComponent component) {
    if (component instanceof VisualVertex)
      return statePainter;
    else if (component instanceof VisualEdge)
      return edgePainter;
    return null;
  }

  /**
   * @throws FileNotFoundException
   * @throws IOException
   */
  public static void epsOutput(Automaton a, String fname, GraphLayoutManager lay)
      throws FileNotFoundException, IOException {
    FileOutputStream fos = new FileOutputStream(fname);
    epsOutput(a, fos, lay);
    fos.flush();
    fos.close();
  }

  /**
   * @param a
   * @param output
   * @param object
   * @throws IOException
   */
  public static void epsOutput(Automaton a, OutputStream output,
      GraphLayoutManager lay) throws IOException {
    imageOutput(a, output, lay, "eps");
  }

  public static void imageOutput(Automaton a, OutputStream output,
      GraphLayoutManager lay, String format) throws IOException {
    VisualGraph vg = new VisualGraph();
    AutomatonVisualFactory fact = new AutomatonVisualFactory();
    vg.setVisualGraphComponentFactory(fact);
    vg.setGraph(new AutomatonGraphAdapter(a));
    if (lay == null) {
      lay = new DigraphLayeredLayout(vg);
      ((DigraphLayeredLayout) lay).setDirected(true);
      ((DigraphLayeredLayout) lay).setRoots(a.initials());
    }
    vg.setGraphLayoutManager(lay);
    lay.setVisualGraph(vg);
    lay.setRepaint(false);
    lay.layout();
    vg.repaint();
    VisualGraphImageOutput out = new VisualGraphImageOutput();
    out.setFormat(format);
    out.output(vg, output);
  }

  /**
   * @return Returns the edgePainter.
   */
  public VisualEdgePainter getEdgePainter() {
    return edgePainter;
  }

  /**
   * @param edgePainter
   *          The edgePainter to set.
   */
  public void setEdgePainter(VisualEdgePainter edgePainter) {
    this.edgePainter = edgePainter;
  }

  /**
   * @return Returns the statePainter.
   */
  public StatePainter1 getStatePainter() {
    return statePainter;
  }

  /**
   * @param statePainter
   *          The statePainter to set.
   */
  public void setStatePainter(StatePainter1 statePainter) {
    this.statePainter = statePainter;
  }
}
