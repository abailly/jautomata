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
 * Created on 2 mai 2005
 *
 */
package rationals.distance;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Computes the geodesic polyhedron with edges of length lower than a given
 * parameter with all its points on the part of a n-dimensional sphere of radius
 * 1, center point 0 and positive coordinates.
 * 
 * @author nono
 * @version $Id: Geodesic.java 2 2006-08-24 14:41:48Z oqube $
 */
public class Geodesic {

    /* required edge length */
    private double eta;

    /* dimension of the sphere */
    private int dim;

    /* base polyedron */
    double[] base;

    DecimalFormat frm = new DecimalFormat("0.0000");
    
    /**
     * Create a geodesic object for given parameter.
     * 
     * @param eta
     *            maximum length of edges. Must be greater than 0 and lower than
     *            sqrt(2)
     */
    public Geodesic(double eta, int dim) {
        this.eta = eta;
        this.dim = dim;
    }

    class Point {
        double[] coords = new double[dim];

        /**
         *  
         */
        public void normalize() {
            /* compute norm */
            double norm = norm();
            if (norm == 1.0 || norm == 0.0)
                return;
            /* update coordinates */
            for(int i=0;i<dim;i++)
                coords[i] /= norm;
        }

        /**
         * Returns distance from this points to p.
         * 
         * @param p
         * @return
         */
        public double distance(Point p) {
            double d = 0;
            for (int i = 0; i < dim; i++) {
                double dif = coords[i] - p.coords[i];
                d += dif * dif;
            }
            return Math.sqrt(d);
        }

        /**
         * 
         * @return
         */
        private double norm() {
            double n = 0.0;
            for (int i = 0; i < dim; i++) {
                n += coords[i] * coords[i];
            }
            return Math.sqrt(n);
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#toString()
         */
        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append('(');
            for (int i = 0; i < dim; i++) {
                sb.append(frm.format(coords[i]));
                if (i < dim - 1)
                    sb.append(',');
            }
            sb.append(')');
            return sb.toString();
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#equals(java.lang.Object)
         */
        public boolean equals(Object obj) {
            Point pt = (Point) obj;
            for (int i = 0; i < dim; i++)
                if (coords[i] != pt.coords[i])
                    return false;
            return true;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#hashCode()
         */
        public int hashCode() {
            int l = 0;
            for (int i = 0; i < dim; i++)
                l ^= (long) coords[i] >> 16;
            return l;
        }
    }

    /**
     * A facet is made of n points of n coordinates.
     * 
     * @author nono
     * @version $Id: Geodesic.java 2 2006-08-24 14:41:48Z oqube $
     */
    class Facet {
        List vertices = new ArrayList();

        List adj = new ArrayList();

        /*
         * Default constructor
         */
        Facet() {
        }

        /**
         * split a facet of dimension n into n facets from barycenter.
         * 
         * @return a List of Facet objects
         */
        List split() {
            List nf = new ArrayList();
            Point p1 = null, p2 = null;
            int n = vertices.size();
            Point barycenter = new Point();
            /* comput median points on each "border" of facet 
             * each edge has dim-1 points so we must compute 
             * all the barycenters of each group of n-1 edges  
             * */
            for (Iterator i = vertices.iterator(); i.hasNext();) {
                p2 = (Point) i.next();
                /* contribution to barycenter */
                for (int j = 0; j < dim; j++) {
                    barycenter.coords[j] += p2.coords[j] / (double)n;
                }
            }
            /* compute global barycenter */
            barycenter.normalize();
            System.err.println("barycenter = "+barycenter);
            /* compute distance between barycenter and all points */
            double d = 0;
            double dmax = Double.MIN_VALUE;
            for (Iterator i = vertices.iterator(); i.hasNext();) {
                p1 = (Point) i.next();
                d = p1.distance(barycenter);
                if (d > dmax)
                    dmax = d;
            }
            /*
             * split facet only if there are points at a distance greater than
             * eta to the barycenter
             */
            System.err.println("dmax = "+dmax);
            if (dmax < eta)
                return nf;
            /* create facets */
            p1 = null;
            List ret = new ArrayList();
            for (int i = 0; i < n; i++) {
                Facet f = new Facet();
                f.vertices.add(barycenter);
                int j = 0;
                for (Iterator it = vertices.iterator(); it.hasNext(); j++) {
                    p2 = (Point) it.next();
                    if (j == i)
                        continue;
                    f.vertices.add(p2);
                }
                ret.add(f);
            }
            return ret;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#toString()
         */
        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append('[');
            for (Iterator i = vertices.iterator(); i.hasNext();) {
                sb.append(i.next());
                if (i.hasNext())
                    sb.append(',');
            }
            sb.append(']');
            return sb.toString();
        }
    }

    /**
     * Computes and return the polyedron for this geodesic sphere.
     * 
     * @return a List of arrays of double as points on the sphere s.t.
     */
    public Collection getPolyedron() {
        Set s = new HashSet();
        List l = new ArrayList();
        List ret = new ArrayList();
        l.add(makeSimplex());
        do {
            ret.clear();
            for (Iterator i = l.iterator(); i.hasNext();) {
                Facet f = (Facet) i.next();
                for (Iterator j = f.vertices.iterator(); j.hasNext();) {
                    Geodesic.Point pt = (Geodesic.Point) j.next();
                    s.add(pt);
                }
                List nl = f.split();
                if (!nl.isEmpty()) {
                    i.remove();
                    ret.addAll(nl);
                }
            }
            if (!ret.isEmpty()) {
                l.clear();
                l.addAll(ret);
            }
        } while (!ret.isEmpty());
        return s;
    }

    /**
     * Returns a collection of facets representing the simplex of 
     * the hypersphere surface.
     * 
     * @return
     */
    private Collection makeSimplex() {
        /*
         * initialize things - we start with minimal simplex in 
         * n dimensions
         */
        List pts = new ArrayList();
        List ret = new ArrayList();
        int n = dim;
        /* limit points */
        for (int i = 0; i < n; i++) {
            Point pt = new Point();
            for (int j = 0; j < n; j++)
                pt.coords[j] = (i == j) ? (base != null) ? base[i] : 1 : 0;
            pts.add(pt);
        }
        Point barycenter = new Point();
        /* barycenter */
        for (Iterator i = pts.iterator(); i.hasNext();) {
            Point p2 = (Point) i.next();
            /* contribution to barycenter */
            for (int j = 0; j < n; j++) {
                barycenter.coords[j] += p2.coords[j] / (double)n;
            }
        }
        barycenter.normalize();        
        /* construct facets */
        for(int i=0;i<n;i++) {
            Facet f = new Facet();
            for(int j=0;j<n;j++) {
                
            }
        }
        return ret;
    }

    /**
     * @return Returns the base.
     */
    public double[] getBase() {
        return base;
    }

    /**
     * @param base
     *            The base to set.
     */
    public void setBase(double[] base) {
        this.base = base;
    }
}
