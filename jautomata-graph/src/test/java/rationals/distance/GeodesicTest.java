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

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import junit.framework.TestCase;

import org.jibble.epsgraphics.EpsGraphics2D;

/**
 * @author nono
 * @version $Id: GeodesicTest.java 2 2006-08-24 14:41:48Z oqube $
 */
public class GeodesicTest extends TestCase {

    /**
     * Constructor for GeodesicTest.
     * 
     * @param arg0
     */
    public GeodesicTest(String arg0) {
        super(arg0);
    }

    public void testGetPolyedron1() throws IOException {
        Geodesic geo = new Geodesic(0.05, 2);
        Collection l = geo.getPolyedron();
        EpsGraphics2D g = new EpsGraphics2D("Test map", new File(
                "geodesic1.eps"), 0, 0, 540, 540);
        RadialGraph rad = new RadialGraph(2, new String[] { "x", "y"});
        for(Iterator i = l.iterator();i.hasNext();) {
            Geodesic.Point pt = (Geodesic.Point)i.next();
            g.drawRect((int)(pt.coords[0]*500),(int)(pt.coords[1]*500),2,2);
        }
        g.flush();
        g.close();
        System.err.println(l);
    }

/*    public void testGetPolyedron2() throws IOException {
        Geodesic geo = new Geodesic(Math.sqrt(2)/2+0.001, 3);
        Collection l = geo.getPolyedron();
        EpsGraphics2D g = new EpsGraphics2D("Test map", new File(
                "geodesic1.eps"), 0, 0, 540, 540);
        RadialGraph rad = new RadialGraph(3, new String[] { "x", "y", "z"});
        for(Iterator i = l.iterator();i.hasNext();) 
            rad.addVector(((Geodesic.Point)i.next()).coords);
        rad.setSize(540, 540);
        rad.paintDirect(g);
        g.flush();
        g.close();
        System.err.println(l);
    }
*/

}
