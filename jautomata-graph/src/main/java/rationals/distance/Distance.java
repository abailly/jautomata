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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rationals.Automaton;
import rationals.graph.AutomatonGraphAdapter;
import rationals.State;
import rationals.Transition;
import rationals.properties.IsDeterministic;
import salvo.jesus.graph.algorithm.DirectedGraphAdjacencyMatrix;
import salvo.jesus.graph.algorithm.DirectedGraphDualMatrix;

/**
 * This class computes various metrics related to the vectorial space associated
 * with a given automaton. The automaton is first minimalized.
 * 
 * @author nono
 * @version $Id: Distance.java 2 2006-08-24 14:41:48Z oqube $
 */
public abstract class Distance {

    private Automaton dfa;

    private Map /* < Transition, Integer > */idx;

    private Map /* < State, Integer > */sidx = new HashMap();

    private int dim;

    private double[] bounds;

    private transient double[] diff;

    /**
     * Cosntruct a distance from an automaton.
     * 
     * @param a must be deterministic.
     */
    public Distance(Automaton a) {
        if(!new IsDeterministic().test(a))
            throw new IllegalArgumentException("Cannot create distance on non deterministic autoamaton");
        this.dfa =a;
        //this.dfa = new Reducer().transform(a);
        /* compute maximum coordinates matrices */
        DirectedGraphDualMatrix m = new DirectedGraphDualMatrix(new AutomatonGraphAdapter(a));
        double[][] tm = new salvo.jesus.graph.algorithm.Distance(m).distances();
        DirectedGraphAdjacencyMatrix am = new DirectedGraphAdjacencyMatrix(new AutomatonGraphAdapter(a));
        double[][] sm = new salvo.jesus.graph.algorithm.Distance(am)
                .distances();
        /* create index map */
        this.idx = new HashMap();
        this.bounds = new double[tm.length];
        this.diff = new double[tm.length];
        int k = 0;
        int si = 0;
        /* get index of start state */
        for (Iterator it = this.dfa.states().iterator(); it.hasNext(); k++) {
            State st = (State) it.next();
            sidx.put(st, new Integer(k));
            if (st.isInitial()) {
                si = k;
            }
        }
        int i=0;
        for (Iterator it = dfa.delta().iterator(); it.hasNext(); i++) {
            Transition tr = (Transition) it.next();
            idx.put(tr, new Integer(i));
            /* bounds for cycle members = length of loop */
            if (tm[i][i] != Double.POSITIVE_INFINITY) {
                bounds[i] = 1 / exponent(tm[i][i]);
            } else {
                /* bound = distance from start state */
                Integer in = (Integer) sidx.get(tr.end());
                bounds[i] = 1 / exponent(sm[si][in.intValue()] + 1);
            }
        }
        this.dim = i;
    }

    /**
     * Compute the vector for a given word. The <code>word</code> is expected
     * to be word from the language accepted by the associated automaton or a
     * prefix of such a word.
     * 
     * @param word
     *            a List of Object representing a word in this automaton.
     * @return an array of int representing the vector for this word. Null if
     *         word is not a prefix of the associated language.
     *  
     */
    public int[] vector(List word) {
        int[] vec = new int[dim];
        State s = (State) dfa.initials().iterator().next(); /*
                                                             * dfa is
                                                             * deterministic and
                                                             * minimal
                                                             */
        for (Iterator i = word.iterator(); i.hasNext();) {
            Object l = i.next();
            /* compute next transition */
            Set d = dfa.delta(s, l);
            if (d.size() == 0)
                return null;
            Transition tr = (Transition) d.iterator().next();
            int ix = ((Integer) idx.get(tr)).intValue();
            vec[ix]++;
            s = tr.end();
        }
        return vec;
    }

    /**
     * Compute the normalized vector for a given word. The raw vector's
     * coefficients are divided by the L-2 norm of the vector.
     * 
     * @param word
     *            a List of Object representing a word in this automaton.
     * @return a array of double objects representing normalized vector. null if
     *         <code>word</code> is not in this language.
     */
    public double[] normalize(List word) {
        int[] vec = vector(word);
        if (vec == null)
            return null;
        return normalize(vec);
    }

    /**
     * Compute the distance between two words.
     * 
     * @param word1
     * @param word2
     * @return a distance. Returns infinity if either word1 or word2 are not
     *         recognized prefixes of this dfa.
     */
    public double distance(List word1, List word2) {
        double[] n1 = normalize(word1);
        if (n1 == null)
            return Double.POSITIVE_INFINITY;
        double[] n2 = normalize(word2);
        if (n2 == null)
            return Double.POSITIVE_INFINITY;
        return distance(n1, n2);
    }

    /**
     * @param n1
     * @param n2
     * @return
     */
    private synchronized double distance(double[] n1, double[] n2) {
        double d = 0;
        for (int i = 0; i < dim; i++) {
            diff[i] = n1[i] - n2[i];
        }
        return norm(diff);
    }

    private double border(double[] vec, int i) {
        for (int j = 0; j < dim; j++) {
            diff[j] = i == j ? bounds[i] : 0;
        }
        return distance(vec, diff);
    }

    /**
     * Compute the eta-coverage of a set of words with respect to this distance.
     * 
     * @param w
     *            a Set of List of words
     * @return an upper bound on the eta-coverage of this set of words.
     */
    public double etaCoverage(Set w) {
        int n = w.size();
        /***********************************************************************
         * map from words to normalized vectors
         *  
         */
        LinkedHashMap vecs = new LinkedHashMap();
        for (Iterator it = w.iterator(); it.hasNext();) {
            List l = (List) it.next();
            vecs.put(normalize(l), l);
        }
        double[] brd = new double[dim];
        Arrays.fill(brd,Double.MAX_VALUE);
        double ret = Double.MIN_VALUE;
        /* compute the maximum distance between words of w */
        double[][] vs = (double[][]) vecs.keySet().toArray(new double[n][]);
        System.err.println("Max border = " + brd);
        for (int i = 0; i < n - 1; i++) {
            /* compute the maximum of minimum distance to borders */
            for (int k = 1; k < dim; k++) {
                double ec = border(vs[i], k);
                if (ec < brd[k])
                    brd[k] = ec;
            }
            double min = Double.MAX_VALUE;
            double[] minv = null;
            /* compute the closest words on each dimension */
            for (int j = i + 1; j < n; j++) {
                double d = distance(vs[i], vs[j]);
                if (d < min) {
                    min = d;
                    minv = vs[j];
                }
            }
            System.err.println("min distance from " + vecs.get(vs[i]) + "=" + min + " to "+ vecs.get(minv));
            if (min > ret)
                ret = min;
        }
        System.err.println("Max kernel = " + ret);
        return ret;
    }

    class VectorAndLength {
        /**
         * @param uv
         * @param len2
         */
        public VectorAndLength(double[] uv, double len2, double[] norm) {
            this.vec = uv;
            this.norm = norm;
            this.len = len2;
        }

        double[] vec;

        double[] norm;

        double len;
    }

    /**
     * Computes a set of words from <code>dfa</code> which is an eta-coverage
     * of the full language recognized by the dfa.
     * 
     * @param eta
     *            the required coverage. Must be greater than 0 and lower than
     *            sqrt(2).
     * @return a Set of List denoting words which is an eta coverage of this
     *         automaton's language
     */
    @SuppressWarnings("unchecked")
    public Set etaCoverage(double eta) {
        if (eta < 0 || eta > exponent(2))
            throw new IllegalArgumentException("Bad value of eta");
        Set ret = new HashSet();
        /* compute geodesic */
        Geodesic geodesic = new Geodesic(eta, dim);
        Collection pts = geodesic.getPolyedron();
        /* denormalize vectors and order by length */
        List vecs = new ArrayList();
        for (Iterator i = pts.iterator(); i.hasNext();) {
            double[] vec = (double[]) i.next();
            double len = 1 / vec[0];
            double[] uv = unnormalize(vec);
            int k = 0;
            /* insert in vecs */
            for (Iterator j = vecs.iterator(); j.hasNext();) {
                VectorAndLength vl = (VectorAndLength) j.next();
                if (len < vl.len)
                    break;
            }
            vecs.add(k, new VectorAndLength(uv, len, vec));
        }
        /*
         * explore automaton in breadh-first direction stop when all points have
         * been covered or no more points can be covered
         */
        return explore(vecs, eta / 2);
    }

    class ExploreState {
        /**
         * @param curvec
         * @param ln2
         * @param state2
         * @param curword
         */
        public ExploreState(int[] curvec, int ln2, State state2, List curword) {
            vec = new int[dim];
            System.arraycopy(curvec, 0, vec, 0, dim);
            ln = ln2;
            state = state2;
            word = new ArrayList(curword);
        }

        int[] vec;

        int ln;

        State state;

        List word;
    }

    Set explore(List vecs, double eta) {
        Set ret = new HashSet();
        LinkedList /* < ExploreState > */trq = new LinkedList();
        int[] cv = new int[dim];
        cv[0] = 1;
        ExploreState st = new ExploreState(cv, 0, (State) dfa.initials()
                .iterator().next(), new ArrayList());
        trq.add(st);
        do {
            st = (ExploreState) trq.removeFirst();
            Set trs = dfa.delta(st.state);
            int[] curvec = st.vec;
            int ln = st.ln;
            List word = st.word;
            for (Iterator k = trs.iterator(); k.hasNext();) {
                Transition tr = (Transition) k.next();
                int j = ((Integer) idx.get(tr)).intValue();
                curvec[j]++;
                ln++;
                double[] v = normalize(curvec);
                word.add(tr.label());
                System.err.println("word =" + word);
                /* compare to vecs */
                for (Iterator i = vecs.iterator(); i.hasNext();) {
                    VectorAndLength vl = (VectorAndLength) i.next();
                    double d = distance(vl.norm, v);
                    System.err.println(" distance =" + d);
                    if (d < eta) {
                        i.remove();
                        System.err.println("removing " + vl);
                        ret.add(word);
                    }
                }
                trq.add(new ExploreState(curvec, ln, tr.end(), word));
                curvec[j]--;
                ln--;
                word.remove(word.size() - 1);
            }
        } while (!vecs.isEmpty());
        return ret;
    }

    /**
     * @param curvec
     * @return
     */
    private double[] normalize(int[] vec) {
        double[] ret = new double[dim];
        for (int i = 0; i < dim; i++)
            ret[i] = vec[i];
        double norm = norm(ret);
        for (int i = 0; i < dim; i++)
            ret[i] /= norm;
        return ret;
    }

    /**
     * @param ds
     * @return
     */
    private double length(double[] ds) {
        double acc = 0;
        for (int i = 0; i < dim; i++)
            acc += ds[i];
        return acc;
    }

    /**
     * Compute a word from a vector. This method computes a word from the
     * language of this automaton that is closest to the coordinates of vec and
     * at a distance lower than <code>eta</code>.
     * 
     * @param vec
     *            a normalized vector.
     * @param eta
     *            maximum distance of returned word.
     * @return a word as List of objects from this automaton's alphabet. If
     *         null, then no word can be constructed at a distance lower than
     *         eta from this vector.
     */
    public List makeWord(double[] vec, double eta) {
        /* denormalize vector */
        double[] real = unnormalize(vec);
        /* map of transitions */
        Map trm = new HashMap();
        for (Iterator i = idx.entrySet().iterator(); i.hasNext();) {
            Map.Entry e = (Map.Entry) i.next();
            Transition tr = (Transition) e.getKey();
            int k = ((Integer) e.getValue()).intValue();
            trm.put(tr, new Double(real[k]));
        }
        /* find a word */
        List l = new ArrayList();
        while (true) {

        }
    }

    /**
     * @param vec
     * @return
     */
    private double[] unnormalize(double[] vec) {
        double[] ret = new double[vec.length];
        /* by convention, vec[0] contains 1/norm */
        double len = 1 / vec[0];
        for (int i = 0; i < dim; i++) {
            ret[i] = vec[i] * len;
        }
        return ret;
    }

    /**
     * Returns the index map associating each transition with an index.
     * 
     * @return a Map from Transition to Integer.
     */
    public Map indices() {
        return idx;
    }

    /**
     * @return
     */
    public Automaton getDfa() {
        return dfa;
    }

    /**
     * @return
     */
    public int getDimension() {
        return dim;
    }

    /**
     * Returns an array of String denoting the ordered dimensions of this
     * distance.
     * 
     * @return an array of String whose length equals <code>dimension</code>.
     */
    public String[] getAxis() {
        String[] ret = new String[dim];
        for (Iterator i = idx.entrySet().iterator(); i.hasNext();) {
            Map.Entry e = (Map.Entry) i.next();
            int j = ((Integer) e.getValue()).intValue();
            String lbl = e.getKey().toString();
            ret[j] = lbl;
        }
        return ret;
    }

    /**
     * @return Returns the bounds.
     */
    public double[] getBounds() {
        return bounds;
    }

    /**
     * @param bounds
     *            The bounds to set.
     */
    public void setBounds(double[] bounds) {
        this.bounds = bounds;
    }

    /**
     * Compute the norm of a vector in this space.
     * 
     * @param vec
     * @return
     */
    public abstract double norm(double[] vec);

    /**
     * The exponent used in the computation of the norm. For a L-k norm, this is
     * usually 1/k.
     * 
     * @param d
     * @return
     */
    public abstract double exponent(double d);

}
