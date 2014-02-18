/*
 * (C) Copyright 2003 Arnaud Bailly (arnaud.oqube@gmail.com),
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
package rationals.graph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;

import rationals.State;
import salvo.jesus.graph.visual.VisualGraphComponent;
import salvo.jesus.graph.visual.VisualVertex;
import salvo.jesus.graph.visual.drawing.VisualVertexPainterImpl;

/**
 * Paints a State as a small colored dot in a white circle. If state is initial,
 * the circle has a colored border. If state is terminal the colored circle as
 * as small white circle inside it.
 * 
 * @author nono
 * @version $Id: StatePainter1.java 2 2006-08-24 14:41:48Z oqube $
 */
public class StatePainter1 extends VisualVertexPainterImpl {

    /* standard shape */
    private GeneralPath basic;

    private GeneralPath init;

    private GeneralPath fini;

    public StatePainter1() {
        basic = new GeneralPath(new Arc2D.Double(0, 0, 15, 15, 0, 360,
                Arc2D.CHORD));
        init = new GeneralPath(basic);
        init.transform(AffineTransform.getScaleInstance(2.0, 2.0));
        fini = new GeneralPath(basic);
        fini.transform(AffineTransform.getScaleInstance(0.90, 0.90));
    }

    /*
     * (non-Javadoc)
     * 
     * @see salvo.jesus.graph.visual.drawing.Painter#paint(salvo.jesus.graph.visual.VisualGraphComponent,
     *      java.awt.Graphics2D)
     */
    public void paint(VisualGraphComponent vg, Graphics2D gd) {
        Graphics2D g2d = (Graphics2D) gd.create();
        VisualVertex vv = (VisualVertex) vg;
        Color col = vv.getFillcolor();
        double x, y;
        x = vg.getBounds2D().getCenterX();
        y = vg.getBounds2D().getCenterY();
        AffineTransform tr1 = AffineTransform.getTranslateInstance(x
                - basic.getBounds2D().getCenterX(), y
                - basic.getBounds2D().getCenterY());
        AffineTransform tr2 = AffineTransform.getTranslateInstance(x
                - init.getBounds2D().getCenterX(), y
                - init.getBounds2D().getCenterY());
        Shape gp = basic.createTransformedShape(tr1);
        Shape gp1 = init.createTransformedShape(tr2);
        /* paint in white */
        if ((vv.getVertex() instanceof State)) {
            State s = (State) vv.getVertex();
            g2d.setColor(Color.white);
            g2d.fill(gp1);
            g2d.setColor(col);
            g2d.fill(gp);
            if (s.isInitial())
                g2d.draw(gp1);
            if (s.isTerminal()) {
                AffineTransform tr3 = AffineTransform.getTranslateInstance(x
                        - fini.getBounds2D().getCenterX(), y
                        - fini.getBounds2D().getCenterY());
                Shape gf = fini.createTransformedShape(tr3);
                /* draw a white cross on state */
                g2d.setColor(Color.white);
                g2d.setStroke(new BasicStroke(1));
                g2d.draw(gf);
            }
            /* draw name of state */
        }
        paintText(vv, g2d);
        g2d.dispose();
    }

    /**
     * Draw the name of state centered.
     * 
     * @see salvo.jesus.graph.visual.drawing.VisualVertexPainter#paintText(salvo.jesus.graph.visual.VisualGraphComponent,
     *      java.awt.Graphics2D)
     */
    public void paintText(VisualGraphComponent component, Graphics2D g2d) {
        FontMetrics fontMetrics = component.getFontMetrics();
        VisualVertex vv = (VisualVertex) component;
        int lineWidth;
        int lineHeight;
        Rectangle bounds;

        lineHeight = fontMetrics.getAscent() - fontMetrics.getDescent();

        bounds = vv.getShape().getBounds();

        g2d.setFont(vv.getFont());
        g2d.setColor(vv.getFontcolor());
        g2d.clip(bounds);
        String n = vv.getText();
        lineWidth = fontMetrics.stringWidth(n);
        g2d.drawString(n,
                (float) (bounds.x + bounds.width / 2 - lineWidth / 2),
                (float) (bounds.y + bounds.height / 2 + lineHeight / 2));
        g2d.setClip(null);
    }

}

/*
 * $Log: StatePainter.java,v $ Revision 1.2 2004/08/18 06:48:08 bailly
 * correcting automata-graph integration Revision 1.1 2004/07/19 06:37:25 bailly
 * modified painting of automaton
 *  
 */