/*
 * (C) Copyright $YEAR Arnaud Bailly (arnaud.oqube@gmail.com),
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
package rationals.distance;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * A subclass of canvas that displays a radial graph. This class may be used as
 * a component in a UI.
 * 
 * @author nono
 * @version $Id: RadialGraph.java 2 2006-08-24 14:41:48Z oqube $
 */
public class RadialGraph extends Canvas {

    private int dim;

    private double eta = 0.0;

    private BufferedImage image;

    private double size = 400;

    private double margin = 60;

    /* coordinate of axis */
    private Point2D coords[];

    /* label of axis */
    private String[] axis;

    /* list of vectors to draw */
    private List vectors = new ArrayList();

    /* flag for filling shapes */
    private boolean fill;

    /* flag for direction connection of shapes */
    private boolean connect;

    /**
     * Create a radial graph with given number of dimensions.
     * 
     * @param dim
     *            the number of dimensions (axis) in this graph.
     * @param axis
     *            the labels of each axis. Size must be equal to dim.
     */
    public RadialGraph(int dim, String[] axis) {
        if (axis.length != dim)
            throw new IllegalArgumentException(
                    "Length of axis must be equals to " + dim);
        this.dim = dim;
        this.axis = axis;
        this.coords = new Point2D[dim];
    }

    /**
     * Draw the radii of the map.
     * 
     * @param g
     */
    public void draw(Graphics2D g) {
        double cx = size / 2 + margin;
        Graphics2D gd = (Graphics2D) g.create();
        /* translate coordinates */
        gd.translate(cx, cx);
        coords[0] = new Point2D.Double(0, -size / 2);
        Line2D line = new Line2D.Double(0, 0, 0, -size / 2);
        /* draw each axis */
        gd.setStroke(new BasicStroke(0.5f));
        gd.setFont(new Font("Helvetica", Font.ITALIC, 10));
        gd.draw(line);
        for (int j = 0; j < dim; j++) {
            double theta = Math.PI / 2 + Math.PI * 2 * j / dim;
            double dx = Math.cos(theta);
            double dy = Math.sin(theta);
            coords[j] = new Point2D.Double(dx * (size / 2), -dy * (size / 2));
            line = new Line2D.Double(0, 0, dx * (size / 2), -dy * (size / 2));
            gd.draw(line);
            String s = axis[j];
            int w = gd.getFontMetrics().stringWidth(s);
            gd.drawString(s,
                    (float) (dx * (size / 2) + (dx > 0 ? 10 : -w - 10)),
                    (float) (-dy * ((size) / 2)));
        }
        // draw a circle
        gd.drawOval((int)-size/2,(int)-size/2,(int)size,(int)size);
    }

    public void drawEta(double eta, Graphics2D g) {
        double cx = size / 2 + margin;
        Graphics2D gd = (Graphics2D) g.create();
        /* translate coordinates */
        gd.translate(cx, cx);
        /* calcul de l'angle */
        double theta = Math.asin(1 - eta * eta / 2);
        Ellipse2D dot = new Ellipse2D.Double(-2, -2, 4, 4);
        for (int i = 0; i < dim; i++) {
            double rho = Math.PI / 2 + Math.PI * 2 * i / dim;
            double dx = Math.cos(rho);
            double dy = Math.sin(rho);
            double cura = theta;
            do {
                double ay = Math.sin(cura);
                double x = ay * dx * (size / 2);
                double y = -ay * dy * (size / 2);
                /* mark */
                dot.setFrame(x - 2, y - 2, 4, 4);
                gd.draw(dot);
                cura = cura - (Math.PI / 2 - theta);
            } while (cura > 0);
        }
    }

    /**
     * Draw the word on given context with given color
     * 
     * @param word
     * @param g
     * @param col
     */
    public void draw(double[] vec, Graphics2D g, Color col, Stroke str) {
        if(vec == null)
            return;
        double cx = size / 2 + margin;
        GeneralPath path = new GeneralPath();
        Rectangle2D pt = new Rectangle2D.Double(-2, -2, 4, 4);
        Graphics2D gd = (Graphics2D) g.create();
        /* translate coordinates */
        gd.translate(cx, cx);
        /* draw axis */
        // draw(gd);
        gd.setStroke(str);
        gd.setColor(col);
        int n = dim;
        for (int i = 0; i < n; i++) {
            double theta = Math.PI / 2 + Math.PI * 2 * i / n;
            double dx = Math.cos(theta);
            double dy = Math.sin(theta);
            double x = vec[i] * dx * (size / 2);
            double y = -vec[i] * dy * (size / 2);
            if (i == 0) {
                path.moveTo((float) x, (float) y);
                pt.setFrame(x - 2, y - 2, 4, 4);
                gd.draw(pt);
            } else if (!connect || (x != 0 || y != 0)) {
                path.lineTo((float) x, (float) y);
                pt.setFrame(x - 2, y - 2, 4, 4);
                gd.draw(pt);
            }
        }
        path.closePath();
        gd.draw(path);

    }

    /**
     * Draw the word on given context with given color
     * 
     * @param word
     * @param g
     * @param col
     */
    public void fill(double[] vec, Graphics2D g, Color col, Stroke str,boolean coord) {
        double cx = size / 2 + margin;
        GeneralPath path = new GeneralPath();
        Rectangle2D pt = new Rectangle2D.Double(-2, -2, 4, 4);
        Graphics2D gd = (Graphics2D) g.create();
        /* translate coordinates */
        gd.translate(cx, cx);
        /* draw axis */
        // draw(gd);
        gd.setStroke(str);
        gd.setColor(col);
        int n = dim;
        double[][] pts = new double[n][2];
        for (int i = 0; i < n; i++) {
            double theta = Math.PI / 2 + Math.PI * 2 * i / n;
            double dx = Math.cos(theta);
            double dy = Math.sin(theta);
            double x = vec[i] * dx * (size / 2);
            double y = -vec[i] * dy * (size / 2);
            pts[i][0]= x;
            pts[i][1] = y;
            if (i == 0) {
                path.moveTo((float) x, (float) y);
                pt.setFrame(x - 2, y - 2, 4, 4);
                gd.draw(pt);
            } else if(!connect || (x != 0 || y != 0)) {
                path.lineTo((float) x, (float) y);
                pt.setFrame(x - 2, y - 2, 4, 4);
                gd.draw(pt);
            }
        }
        path.closePath();
        gd.fill(path);
        /* show coordinates */
        if(coord) {
            gd.setColor(Color.black);
            for(int i=0;i<n;i++)
                gd.drawString(""+vec[i],(float)(pts[i][0]+5),(float)(pts[i][1]+5));
        }
    }

    public void paintDirect(Graphics g) {
        RenderingHints hints = new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        hints.put(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        Graphics2D gd = (Graphics2D) g;
        gd.setRenderingHints(hints);
        gd.setColor(Color.white);
        Shape sh = gd.getClip();
        if (sh == null)
            sh = new Rectangle2D.Double(0, 0, getWidth(), getHeight());
        gd.fill(sh);
        gd.setColor(Color.black);
        EnumeratingPainters p = new EnumeratingPainters();
        BasicStroke s = new BasicStroke(0.5f);
        draw(gd);
        if (eta != 0)
            drawEta(eta, gd);
        for (Iterator it = vectors.iterator(); it.hasNext();) {
            double[] vec = (double[]) it.next();
            if (fill)
                fill(vec, gd, p.nextColor(), s,false);
            else
                draw(vec, gd, p.nextColor(), s);
        }

    }

    public void paint(Graphics g) {
        if (image == null)
            image = new BufferedImage(getWidth(), getHeight(),
                    BufferedImage.TYPE_4BYTE_ABGR);
        RenderingHints hints = new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        hints.put(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        Graphics2D gd = (Graphics2D) image.getGraphics();
        gd.setRenderingHints(hints);
        gd.setColor(Color.white);
        Shape sh = gd.getClip();
        if (sh == null)
            sh = new Rectangle2D.Double(0, 0, getWidth(), getHeight());
        gd.fill(sh);
        gd.setColor(Color.black);
        EnumeratingPainters p = new EnumeratingPainters();
        BasicStroke s = new BasicStroke(0.5f);
        draw(gd);
        if (eta != 0)
            drawEta(eta, gd);
        for (Iterator it = vectors.iterator(); it.hasNext();) {
            double[] vec = (double[]) it.next();
            if (fill)
                fill(vec, gd, p.nextColor(), s,false);
            else
                draw(vec, gd, p.nextColor(), s);
        }
        /* copy to g */
        ((Graphics2D) g).drawImage(image, null, 0, 0);
    }

    class EnumeratingPainters {

        private int curColor = 0x00fF0000;

        public Color nextColor() {
            curColor = (curColor + 0x00003203) & 0x00ffffff;
            int r = (curColor >> 16) & 0xff;
            int g = (curColor >> 8) & 0xff;
            int b = curColor & 0xff;
            return new Color(r, g, b, 0xf0);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.Component#getPreferredSize()
     */
    public Dimension getPreferredSize() {
        return new Dimension((int) (size + margin), (int) (size + margin));
    }

    public void addVector(double[] vec) {
        this.vectors.add(vec);
    }

    public void removeVector(double[] vec) {
        this.vectors.remove(vec);
    }

    /**
     * @return Returns the eta.
     */
    public double getEta() {
        return eta;
    }

    /**
     * @param eta
     *            The eta to set.
     */
    public void setEta(double eta) {
        this.eta = eta;
    }

    /**
     * @return Returns the fill.
     */
    public boolean isFill() {
        return fill;
    }

    /**
     * @param fill
     *            The fill to set.
     */
    public void setFill(boolean fill) {
        this.fill = fill;
    }

    /**
     * @return Returns the margin.
     */
    public double getMargin() {
        return margin;
    }

    /**
     * @param margin
     *            The margin to set.
     */
    public void setMargin(double margin) {
        this.margin = margin;
    }

    /**
     * @param size
     *            The size to set.
     */
    public void setSize(double size) {
        this.size = size;
    }

    /**
     * @return Returns the vectors.
     */
    public List getVectors() {
        return vectors;
    }

    /**
     * @param l
     */
    public void addVectors(Collection l) {
        vectors.addAll(l);
    }

    /**
     * @return Returns the connect.
     */
    public boolean isConnect() {
        return connect;
    }

    /**
     * @param connect
     *            The connect to set.
     */
    public void setConnect(boolean connect) {
        this.connect = connect;
    }
}
