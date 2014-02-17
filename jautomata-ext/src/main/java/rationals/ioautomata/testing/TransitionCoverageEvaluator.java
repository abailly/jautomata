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
package rationals.ioautomata.testing;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import rationals.Automaton;
import rationals.Transition;
import rationals.ioautomata.IOAutomaton;
import rationals.ioautomata.IOTransition;

/**
 * An evaluator that tries to maximize covered transitions.
 * 
 * @author nono
 * @version $Id: TransitionCoverageEvaluator.java 2 2006-08-24 14:41:48Z oqube $
 */
public class TransitionCoverageEvaluator implements Evaluator {

    class Key {

        Automaton aut;

        Transition tr;

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#equals(java.lang.Object)
         */
        public boolean equals(Object obj) {
            Key k = (Key) obj;
            if (k == null)
                return false;
            return k.aut.equals(aut) && k.tr.equals(tr);
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#hashCode()
         */
        public int hashCode() {
            return (aut.hashCode() << 16) ^ tr.hashCode();
        }

        Key(Automaton a, Transition tr) {
            aut = a;
            this.tr = tr;
        }

        public String toString() {
            return "[" + aut.getId() + " : " + tr + "]";
        }

    }

    class Count {
        int count;

        Count(int i) {
            this.count = i;
        }

        void inc() {
            count++;
        }

        public String toString() {
            return Integer.toString(count);
        }

        public boolean equals(Object o) {
            Count c = (Count) o;
            if (c == null)
                return false;
            return c.count == count;
        }
    }

    private Map covers = new HashMap();

    private IOAutomaton automaton;

    private double minCoverage;

    private int totalFire;

    /*
     * (non-Javadoc)
     * 
     * @see rationals.ioautomata.testing.Evaluator#setTester(rationals.ioautomata.testing.AlphaBetaIOTester)
     */
    public void setTester(AlphaBetaIOTester tester) {
        this.automaton = tester.getSpec();
        /* try to cover input transitions */
    }

    /*
     * (non-Javadoc)
     * 
     * @see rationals.ioautomata.testing.Evaluator#start()
     */
    public void start() {
        covers = new HashMap();
    }

    /*
     * (non-Javadoc)
     * 
     * @see rationals.ioautomata.testing.Evaluator#eval(java.util.Set,
     *      java.util.List, java.util.Set)
     */
    public double eval(Set traces, IOTransition trans, Set st) {
        double val = 0;
        /* return the sum the transitions going out of st */
        for(Iterator i = automaton.delta(st).iterator();i.hasNext();) {
            Transition tr = (Transition)i.next();
            Count c = (Count) covers.get(tr);
            if (c != null)
                val += (double) 1 / c.count;
            else
                val += 1;
        }
//        Count c = (Count) covers.get(trans);
//        if (c != null) {
//            val = (double) 1 / c.count;
//        } else
//            val = 1;
        return val;
    }

    /*
     * (non-Javadoc)
     * 
     * @see rationals.ioautomata.testing.Evaluator#isEnough(java.util.Set)
     */
    public boolean isEnough(Set traces) {
        int sz = automaton.delta().size();
        double cov = covers.keySet().size() / (double) sz;
        if (cov > minCoverage) {
            System.err.println("Reached coverage of " + cov);
            return true;
        }
        /* check total firing count */
        if (totalFire > 10 * sz) {
            System.err.println("Giving up at " + cov);
            return true;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see rationals.ioautomata.testing.Evaluator#step(java.util.Set,
     *      java.util.Set)
     */
    public void step(Set state, Set next) {
        Iterator it = automaton.delta(state).iterator();
        while (it.hasNext()) {
            Transition tr = (Transition) it.next();
            if (!next.contains(tr.end()))
                continue;
            Count c = (Count) covers.get(tr);
            if (c == null)
                covers.put(tr, new Count(1));
            else
                c.inc();
            totalFire++;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see rationals.ioautomata.testing.Evaluator#reset()
     */
    public void reset() {
    }

    /**
     * @return Returns the minCoverage.
     */
    public double getMinCoverage() {
        return minCoverage;
    }

    /**
     * @param minCoverage
     *            The minCoverage to set.
     */
    public void setMinCoverage(double minCoverage) {
        this.minCoverage = minCoverage;
    }
}
