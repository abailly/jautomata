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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import rationals.Automaton;
import rationals.graph.AutomatonGraphAdapter;
import rationals.converters.Expression;
import rationals.graph.AutomatonVisualFactory;
import salvo.jesus.graph.algorithm.DirectedGraphDualMatrix;
import fr.lifl.utils.FIDLTestCase;

/**
 * @author nono
 * @version $Id: DistanceTest.java 5 2006-08-25 13:57:34Z oqube $
 */
public class DistanceTest extends FIDLTestCase {

    private Distance distance;

    private Distance distance2;

    private Automaton a;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        this.a = new Expression().fromString("a(b+c)(ab)*");
        this.distance = new DistanceL1(a);
        Automaton b = new Expression().fromString("(ab*)(c(ab)*c*)");
        this.distance2 = new DistanceL1(b);
       }

    /**
     * Constructor for DistanceTest.
     * 
     * @param arg0
     */
    public DistanceTest(String arg0) {
        super(arg0);
    }

    public void testVector() throws FileNotFoundException, IOException {
        List w = Arrays.asList(new Object[] { "a", "b", "a", "b" });
        int[] vec = distance.vector(w);
        System.err.println(distance.getDfa());
        System.err.println(distance.indices());
        AutomatonVisualFactory.epsOutput(distance.getDfa(),
                "fig-sample-dfa-distance.eps", null);
        assertEquals(new int[] { 1, 1, 1, 0, 1 }, vec);
    }

    public void testVectorNull() {
        List w = Arrays.asList(new Object[] { "a", "b", "c" });
        int[] vec = distance.vector(w);
        assertNull(vec);
    }

    public void testNormalize() {
        List w = Arrays.asList(new Object[] { "a", "b", "a", "b" });
        double[] norm = distance.normalize(w);
	//        assertEquals(new double[] { 0.5, 0.5, 0.5, 0.5 }, norm);
    }

    public void testDistance0() {
        List w = Arrays.asList(new Object[] { "a", "b", "a", "b" });
        List w2 = Arrays.asList(new Object[] { "a", "b", "a", "b" });
        double d = distance.distance(w, w2);
        System.err.println(d);
        assertEquals((double) 0, (double) d, 0.00000001);
    }

    public void testDistanceTriangle() {
        List w = Arrays.asList(new Object[] { "a", "b", "a", "b" });
        List w2 = Arrays.asList(new Object[] { "a", "c", "a", "b", "a" });
        List w3 = Arrays.asList(new Object[] { "a", "b", "a", "b", "a" });
        double d1 = distance.distance(w, w2);
        double d2 = distance.distance(w2, w3);
        double d3 = distance.distance(w, w3);
        assertTrue(d1 <= d2 + d3);
        assertTrue(d2 <= d1 + d3);
        assertTrue(d3 <= d2 + d1);
    }

    public void testDistanceLimit1() {
        List w = new ArrayList(Arrays
                .asList(new Object[] { "a", "b", "a", "b" }));
        List w2 = new ArrayList(Arrays
                .asList(new Object[] { "a", "c", "a", "b" }));
        System.out.println("s(" + w + "," + w2 + ") = "
                + distance.distance(w, w2));
        for (int i = 0; i < 10; i++) {
            w.add("a");
            w.add("b");
            w2.add("a");
            w2.add("b");
            System.out.println("s(" + w + "," + w2 + ") = "
                    + distance.distance(w, w2));
        }
    }

    public void testDistanceLimit2() {
        List w = new ArrayList(Arrays
                .asList(new Object[] { "a", "b", "a", "b" }));
        List w2 = new ArrayList(Arrays
                .asList(new Object[] { "a", "b", "a", "b" }));
        System.out.println("s(" + w + "," + w2 + ") = "
                + distance.distance(w, w2));
        for (int i = 0; i < 10; i++) {
            w2.add("a");
            w2.add("b");
        }
        for (int i = 0; i < 10; i++) {
            w.add("a");
            w.add("b");
            System.out.println("s(" + w + "," + w2 + ") = "
                    + distance.distance(w, w2));
        }
    }

    public void testDistanceLimit3() {
        List w = new ArrayList(Arrays
                .asList(new Object[] { "a", "b", "a", "b" }));
        List w2 = new ArrayList(Arrays
                .asList(new Object[] { "a", "b", "a", "b" }));
        System.out.println("s(" + w + "," + w2 + ") = "
                + distance.distance(w, w2));
        for (int i = 0; i < 100; i++) {
            w.add("a");
            w.add("b");
            System.out.println("s(" + w2 + "^"+i+"," + w2 + ") = "
                    + distance.distance(w, w2));
        }
    }
    
    public void testEta() {
        List w2 = new ArrayList(Arrays
                .asList(new Object[] { "a", "c", "a", "b", "a", "b"}));
        List w = new ArrayList(Arrays
                .asList(new Object[] { "a", "b", "a", "b" }));
        Set t = new HashSet();
        t.add(w2);
        t.add(w);
        double dmax = distance.etaCoverage(t);
        System.err.println("Dmax = "+dmax);
    }
    
  /*  public void testEtaSet() {
        Set ret = distance.etaCoverage(1);
        
    }
*/
    public void testDistance2() throws FileNotFoundException, IOException {
        System.err.println(distance2.indices());
        List w2 = new ArrayList(Arrays
                .asList(new Object[] { "a", "c", "c","c"}));
        List w = new ArrayList(Arrays
                .asList(new Object[] { "a", "b", "b", "b","c","c","c","c" }));
        double d= distance2.distance(w2,w);
        System.err.println("Distance = "+d);
        System.err.println("w2 = "+printArray(distance2.normalize(w2))+", " +"w = "+printArray(distance2.normalize(w)));
                AutomatonVisualFactory.epsOutput(distance2.getDfa(),
                "fig-sample-dfa-distance2.eps", null);
    }
    
    public void testMatrix() {
        System.err.println(a);
       DirectedGraphDualMatrix am = new DirectedGraphDualMatrix(new AutomatonGraphAdapter(a));
        System.err.println(am);
        System.err.println(new salvo.jesus.graph.algorithm.Distance(am));
        System.err.println(am.polynomial());
    }
}
