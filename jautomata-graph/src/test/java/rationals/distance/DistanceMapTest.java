/*______________________________________________________________________________
 * 
 * Copyright 2005 Arnaud Bailly - NORSYS/LIFL
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * (1) Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 * (2) Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in
 *     the documentation and/or other materials provided with the
 *     distribution.
 *
 * (3) The name of the author may not be used to endorse or promote
 *     products derived from this software without specific prior
 *     written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Created on 29 avr. 2005
 *
 */
package rationals.distance;

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.jibble.epsgraphics.EpsGraphics2D;

import rationals.Automaton;
import rationals.converters.ConverterException;
import rationals.converters.Expression;
import rationals.graph.AutomatonVisualFactory;

/**
 * @author nono
 * @version $Id: DistanceMapTest.java 2 2006-08-24 14:41:48Z oqube $
 */
public class DistanceMapTest extends TestCase {

    private Distance distance;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        Automaton b = new Expression().fromString("(ab*)(c(ab)*c*)");
        AutomatonVisualFactory.epsOutput(b,"fig-sample-distancemap.eps",null);
        this.distance = new DistanceL1(b);
    }

    /**
     * Constructor for DistanceMapTest.
     * 
     * @param arg0
     */
    public DistanceMapTest(String arg0) {
        super(arg0);
    }

    public void test1() throws ConverterException, FileNotFoundException, IOException {
        Automaton a = new Expression().fromString("a(b+c)(ab)*");
        AutomatonVisualFactory.epsOutput(a,"fig-sample-dfa-distance.eps",null);
       Distance dist = new DistanceL1(a);
       EpsGraphics2D g = new EpsGraphics2D("Test map", new File("fig-radial.eps"), 0,
               0, 600, 600);
       List w = new ArrayList(Arrays.asList(new Object[] { "a", "b", "a", "b"}));
       List w2 = new ArrayList(Arrays.asList(new Object[] { "a","c", "a", "b",
               "a", "b"}));
       String[] lbls = dist.getAxis();
       RadialGraph rad = new RadialGraph(dist.getDimension(), lbls);
       rad.setSize(500);
       rad.paintDirect(g);
       rad.setConnect(true);
       rad.draw(dist.normalize(w), g, Color.red, new BasicStroke(1f,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL,1.0f,new float[]{2,5},0));
       rad.draw(dist.normalize(w2), g, Color.blue, new BasicStroke(1f,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL,1.0f,new float[]{10,3},0));
       g.flush();
       g.close();
        
    }
    
    public void testEpsout() throws IOException {
        EpsGraphics2D g = new EpsGraphics2D("Test map", new File("map.eps"), 0,
                0, 1060, 1060);
        List w = new ArrayList(Arrays.asList(new Object[] { "a", "b", "b", "b",
                "b", "b", "b", "b" }));
        List w2 = new ArrayList(Arrays.asList(new Object[] { "c", "a", "b",
                "a", "b", "a", "b", "a", "b", "c", "c", "c", "c", "c", "c" }));
        String[] lbls = distance.getAxis();
        Geodesic geodesic = new Geodesic(0.7, lbls.length);
        geodesic.setBase(distance.getBounds());
        Collection l = geodesic.getPolyedron();
        RadialGraph rad = new RadialGraph(distance.getDimension(), lbls);
        for(Iterator i = l.iterator();i.hasNext();) 
            rad.addVector(((Geodesic.Point)i.next()).coords);
        rad.setSize(1000);
        rad.paintDirect(g);
        rad.draw(distance.normalize(w), g, Color.red, new BasicStroke(1.5f));
        rad.draw(distance.normalize(w2), g, Color.blue, new BasicStroke(1.5f));
        g.flush();
        g.close();
    }

    public void testEnum() throws IOException {
        EpsGraphics2D g = new EpsGraphics2D("Test map",
                new File("language.eps"), 0, 0, 600, 600);
        String[] lbls = distance.getAxis();
/*        Geodesic geodesic = new Geodesic(0.7, lbls.length);
        geodesic.setBase(distance.getBounds());
        Collection l = geodesic.getPolyedron();
*/
        RadialGraph rad = new RadialGraph(distance.getDimension(), lbls);
        rad.setConnect(false);
        Set s = distance.getDfa().enumerate(15);
        System.err.println("coverage for "+s.size()+" words = " +distance.etaCoverage(s));
        int n = distance.getDimension();
        double[] max = new double[n];
        Arrays.fill(max,0);
        /* compute set of vectors from words */
        for (Iterator i = s.iterator(); i.hasNext();) {            
            List w = (List) i.next();
            double[] v = distance.normalize(w);
            for(int j=0;j<n;j++) {
                if(v[j]> max[j])
                    max[j] = v[j];
            }
            rad.addVector(v);
        }
        rad.setSize(500);
/*        for(Iterator i = l.iterator();i.hasNext();) 
            rad.addVector(((Geodesic.Point)i.next()).coords);
*/
        rad.paintDirect(g);
        rad.draw(distance.getBounds(),g,Color.red,new BasicStroke(1.5f));
        rad.draw(max,g,Color.black,new BasicStroke(1.5f));
        g.flush();
        g.close();
    }
}
