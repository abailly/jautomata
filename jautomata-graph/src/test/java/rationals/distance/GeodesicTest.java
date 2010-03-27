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
 * Created on 4 mai 2005
 *
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
