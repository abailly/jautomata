/*
 * (C) Copyright 2005 Arnaud Bailly (arnaud.oqube@gmail.com),
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
