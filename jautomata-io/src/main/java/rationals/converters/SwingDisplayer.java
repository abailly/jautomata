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
package rationals.converters;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import rationals.Automaton;
import rationals.graph.AutomatonVisualFactory;
import rationals.graph.AutomatonGraphAdapter;
import salvo.jesus.graph.Graph;
import salvo.jesus.graph.visual.GraphScrollPane;
import salvo.jesus.graph.visual.VisualGraph;
import salvo.jesus.graph.visual.VisualGraphComponent;
import salvo.jesus.graph.visual.drawing.VisualDirectedEdgePainterImpl;
import salvo.jesus.graph.visual.drawing.VisualEdgePainter;
import salvo.jesus.graph.visual.drawing.VisualVertexPainter;
import salvo.jesus.graph.visual.drawing.VisualVertexPainterImpl;
import salvo.jesus.graph.visual.layout.DigraphLayeredLayout;
import salvo.jesus.graph.visual.layout.GraphLayoutManager;
import salvo.jesus.graph.visual.print.VisualGraphImageOutput;
import salvo.jesus.graph.visual.print.VisualGraphPrinter;
import fr.lifl.utils.ExtensionFileFilter;
import fr.lifl.utils.FileDialog;

/**
 * Display an Automaton in a window using JM Salvo openjgraph
 * 
 * 
 * @author Arnaud Bailly
 * @version 30082002
 */

public class SwingDisplayer extends JFrame implements Displayer {

  /////////////////////////////////////////////////////////////
  // PRIVATE MEMBERS
  ////////////////////////////////////////////////////////////

  // the graph to displau
  private Graph graph;

  // the automaton
  private Automaton automaton;

  // the visual graph
  private VisualGraph vgraph;

private GraphLayoutManager layout;

private GraphScrollPane gsp;

private AutomatonContextMenu contextMenu;

  class AltDirectedEdgePainter extends VisualDirectedEdgePainterImpl {
    private transient BasicStroke origStroke = null;

    private transient BasicStroke fatStroke;

    public void paint(VisualGraphComponent component, Graphics2D g2d) {
      // Change the color to red and do not set the color back to its original
      // so that when the user sees the visual properties of the edge, it is
      // red.

      // Set the stroke for edges in thesubgraph to be double the stroke
      // of the default stroke.
      if (this.origStroke == null) {

        this.origStroke = new BasicStroke();
        this.fatStroke = new BasicStroke(origStroke.getLineWidth() * 2);
      }
      g2d.setStroke(this.fatStroke);
      super.paint(component, g2d);

      // Restore the stroke to the default
      g2d.setStroke(this.origStroke);
    }
  }

  class AltVertexPainter extends VisualVertexPainterImpl {

    private Color color;

    AltVertexPainter(Color color) {
      this.color = color;
    }

    public void paintFill(VisualGraphComponent component,
        java.awt.Graphics2D g2d) {
      component.setFillcolor(color);
      super.paintFill(component, g2d);
    }
  }

  class EnumeratingPainters {

    private int curColor = 0x00ff0000;

    private Color nextColor() {
      curColor = (curColor + 0x00003201) & 0x00ffffff;
      int r = (curColor >> 16) & 0xff;
      int g = (curColor >> 8) & 0xff;
      int b = curColor & 0xff;
      System.err.println("Using color " + r + "," + g + "," + b);
      return new Color(r, g, b);
    }

    VisualVertexPainter getVertexPainter() {
      return new AltVertexPainter(nextColor());
    }

    VisualEdgePainter getEdgePainter() {
      return new AltDirectedEdgePainter();
    }
  }

  /*
   * A contextual menu for automaton @author bailly
   * 
   * @version $Id: SwingDisplayer.java 2 2006-08-24 14:41:48Z oqube $
   */
  class AutomatonContextMenu extends JPopupMenu {

      public AutomatonContextMenu() {
          JMenuItem item = new JMenuItem("Print ...");
          item.addActionListener(new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                  new VisualGraphPrinter(vgraph).showPrint(getX(), getY());
                  /*
                   * if (autos == null) return; for (int i = 0; i <
                   * autos.length; i++) print(autos[i]);
                   */
              }
          });
          add(item);
          item = new JMenuItem("Save Image ...");
          item.addActionListener(new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                  File file;
                  FileDialog fd = new FileDialog();
                  ExtensionFileFilter filter = new ExtensionFileFilter();
                  filter.addExtension("eps");
                  filter.addExtension("jpeg");
                  filter.addExtension("png");
                  filter.addExtension("gif");
                  filter.setDescription("FIDL Automaton image");
                  file = fd.userSelect(filter, new File("."));
                  if (file == null)
                      return;
                  /* set format */
                  String format = "eps";
                  if (file.getName().lastIndexOf('.') != -1)
                      format = file.getName().substring(
                              file.getName().lastIndexOf('.') + 1);
                  /* write file in selected format */
                  VisualGraphImageOutput out = new VisualGraphImageOutput();
                  out.setFormat(format);
                  try {
                      out.output(vgraph, new FileOutputStream(file));
                      /*
                       * if (autos == null) return; for (int i = 0; i <
                       * autos.length; i++) print(autos[i]);
                       */
                  } catch (FileNotFoundException e1) {
                      JOptionPane.showMessageDialog(SwingDisplayer.this,
                              "Unable to write output file " + file + ": \n"
                                      + e1.getMessage());
                      e1.printStackTrace();
                  } catch (IOException e1) {
                      JOptionPane.showMessageDialog(SwingDisplayer.this,
                              "Unable to write output file " + file + ": \n"
                                      + e1.getMessage());
                      e1.printStackTrace();
                  }
              }
          });
          add(item);
          item = new JMenu("Zoom");
          JMenuItem item2 = new JMenuItem("in");
          item2.addActionListener(new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                //  zoomIn();
                  if (layout != null) {
                      layout.setRepaint(true);
                  }
                  vgraph.repaint();
              }
          });
          item.add(item2);
          item2 = new JMenuItem("out");
          item2.addActionListener(new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                  //zoomOut();
                  if (layout != null) {
                      layout.setRepaint(true);
                  }
                  vgraph.repaint();
              }
          });
          item.add(item2);
          add(item);

      }

//      public void zoomIn() {
//          if (gsp.() < maxZoom) {
//              zoomFactor = zoomFactor * 2;
//              gsp.setsetZoomFactor(((double) zoomFactor) / (double) 1024);
//          }
//      }
//
//      public void zoomOut() {
//          if (zoomFactor > minZoom) {
//              zoomFactor = zoomFactor / 2;
//              setZoomFactor(((double) zoomFactor) / (double) 1024);
//          }
//      }



  }
  /////////////////////////////////////////////////////////////
  // CONSTRUCTORS
  ////////////////////////////////////////////////////////////

  public SwingDisplayer() {

    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = new Dimension(screenSize.width / 2,
        screenSize.height / 2);

    this.setSize(frameSize);
    this.setLocation((int) (screenSize.getWidth() - frameSize.getWidth()) / 2,
        (int) (screenSize.getHeight() - frameSize.getHeight()) / 2);

    // Terminate the application when the window closes
    this.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        System.exit(0);
      }
    });
    setVisible(true);
  }

  /**
   * Defines the Automaton to display with this displayer
   * 
   * @param an
   *          Automaton object
   */
  public void setAutomaton(Automaton a) throws ConverterException {
    this.automaton = a;
    try {
      this.graph = new AutomatonGraphAdapter(a);
      display();
    } catch (Exception ex) {
      throw new ConverterException(ex.getMessage());
    }
  }

  /**
   * Defines the algorithm to use for layout This is a no-op in this class.
   * 
   * @param an
   *          Algorithm object
   */
  public void setAlgorithm(rationals.converters.algorithms.LayoutAlgorithm algo) {
  }

  /**
   * Asks the displayer to display the automaton
   */
  public void display() throws ConverterException {
    this.getContentPane().removeAll();
    // command pane
    JPanel cmdpanel = new JPanel();
    JButton sccbutton = new JButton("SCC");
    contextMenu = new AutomatonContextMenu();
    // Get a VisualGraph
    gsp = new GraphScrollPane();
    vgraph = new VisualGraph();
    vgraph.setVisualGraphComponentFactory(new AutomatonVisualFactory());
    vgraph.setGraph(graph);
    gsp.setVisualGraph(vgraph);
    gsp.setAntialias(true);
    layout = new DigraphLayeredLayout(vgraph);
    ((DigraphLayeredLayout)layout).setRoots(automaton.initials());
    layout.setRepaint(true);
    // Initialise a layout manager, though not really part of this eample
    gsp.setGraphLayoutManager(layout);
    gsp.addMouseListener(new MouseAdapter() {
        public void mouseReleased(MouseEvent e) {
            if(e.getButton() == MouseEvent.BUTTON3)
                contextMenu.show(gsp,e.getX(),e.getY());
        }
    });
    // Make it all visible
    this.getContentPane().setLayout(new GridLayout(1, 2));
    this.getContentPane().add(gsp);
    this.validate();
    layout.layout();
  }

}
