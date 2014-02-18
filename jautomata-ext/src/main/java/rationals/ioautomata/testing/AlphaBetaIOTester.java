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
package rationals.ioautomata.testing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rationals.ioautomata.IOAlphabetType;
import rationals.ioautomata.IOAutomaton;
import rationals.ioautomata.IOStateMachine;
import rationals.ioautomata.IOTransition;
import rationals.transformations.TransformationsToolBox;

/**
 * An IOAutomata tester based on the alphab-beta evaluation algorithm for two
 * players games.
 * <p>
 * The specification is expected to be in normal form, which means that set of
 * states may be partitionned in two : player states from which only inputs are
 * possible and opponent states from which only outputs are possible.
 * 
 * @author nono
 * @version $Id: AlphaBetaIOTester.java 2 2006-08-24 14:41:48Z oqube $
 */
public class AlphaBetaIOTester extends AbstractIOTester {

    private IOAutomaton spec;

    private IOStateMachine impl;

    /* depth of search */
    private int depth = 2;

    /* precomputed initial states */
    private Set q0;

    /* the evaluator */
    private Evaluator evaluator;

    /*
     * (non-Javadoc)
     * 
     * @see rationals.ioautomata.testing.IOAutomataTester#test(rationals.ioautomata.IOStateMachine,
     *      rationals.ioautomata.IOAutomaton)
     */
    public void test(IOStateMachine impl, IOAutomaton spec)
            throws IOTestFailure {
        this.spec = spec;
        this.impl = impl;
        this.evaluator.setTester(this);
        List trace = new ArrayList();
        /* the set of test executed so far */
        Set /* < List < Object > > */traces = new HashSet();
        traces.add(trace);
        /* the current state - assume non deterministic automata */
        Set state = q0 = TransformationsToolBox.epsilonClosure(spec.initials(),
                spec);
        while (true) {
            /* select a move */
            Move mv = select(trace, traces, state, depth);
            if (mv == Move.terminate) {
                return;
            } else if (mv == Move.reset) {
                impl.reset();
                evaluator.reset();
                traces.add(trace);
                trace = new ArrayList();
                state = TransformationsToolBox.epsilonClosure(spec.initials(),
                        spec);
                System.err.println("Resetting");
                continue;
            } else if (mv == Move.out) {
                System.err.println("Waiting for output");
                /* get output from impl */
                Object o = impl.output();
                /* transform as output for spec */
                IOTransition.IOLetter lt = new IOTransition.IOLetter(o,
                        IOAlphabetType.OUTPUT);
                /* find next state */
                Set next = spec.step(state, lt);
                if (!next.isEmpty()) {
                    trace.add(lt);
                    evaluator.step(state, next);
                    state = next;
                } else {
                    /* failure */
                    IOTestFailure fail = new IOTestFailure("unexpected output : "+lt);
                    fail.setFailureTrace(trace);
                    throw fail;
                }
            } else {
                System.err.println("Selecting input : "+mv.move);
                /* send input to impl */
                impl.input(mv.move);
                IOTransition.IOLetter lt = new IOTransition.IOLetter(mv.move,
                        IOAlphabetType.INPUT);
                Set next = spec.step(state, lt);
                evaluator.step(state, next);
                state = next;
                trace.add(lt);
            }
        }
    }

    /**
     * @param trace
     *            the current trace
     * @param traces
     *            the set of test traces
     * @param state
     *            the current state
     * @param depth2
     *            the search depth
     * @return
     */
    private Move select(List trace, Set traces, Set state, int depth2) {
        /* split transitions into input/output sets */
        Set itrs = spec.delta(state);
        Set otrs = new HashSet(itrs);
        filterInputs(itrs);
        filterOutputs(otrs);
        /* basic tests */
        if ((itrs.size() == 0 && otrs.size() == 0))
            return Move.reset;
        /* check eta-coverage */
        if (evaluator.isEnough(traces))
            return Move.terminate;
        /* check possible outputs */
        if (!otrs.isEmpty())
            return Move.out;
        Map /* < Transition, Double > */desirability = new HashMap();
        /* inputs evaluation */
        double maxi = -Double.MAX_VALUE;
        IOTransition maxt = null;
        for (Iterator i = itrs.iterator(); i.hasNext();) {
            IOTransition tr = (IOTransition) i.next();
            Set end = spec.step(state, tr.label());
            double e;
            e = eval(end, tr, traces, Double.MAX_VALUE, -Double.MAX_VALUE,
                    depth, false);
            if (e >= maxi) {
                maxi = e;
                maxt = tr;
            }
        }
        System.err.println(" Maximum value of input "+maxi);
        /* reset evaluation */
        if (!state.equals(q0)) {
            Set q0trs = spec.delta(q0);
            List qtr = new ArrayList();
            for (Iterator i = q0trs.iterator(); i.hasNext();) {
                IOTransition tr = (IOTransition) i.next();
                double e;
                Set end = spec.step(q0, tr.label());
                e = -eval(end, tr, traces, Double.MAX_VALUE, -Double.MAX_VALUE,
                        depth, false);
                if (e > maxi) {
                    System.err.println("Found reset move "+tr +"with value"+e);
                    return Move.reset;
                }
            }
        }
        return new Move(((IOTransition.IOLetter) maxt.label()).label);

    }

    /**
     * Alpha-beta evaluation.
     * 
     * @param st
     *            the state reached by trans
     * @param trans
     *            the transition to eval
     * @param traces
     *            the set of test traces
     * @param alpha
     *            the alpha threshold
     * @param beta
     *            the beta threshold
     * @param curdepth
     *            the current depth of search
     * @param in
     *            true if state is a player state, false otherwise
     * @return an evaluation for state st
     */
    private double eval(Set st, IOTransition trans, Set traces, double alpha,
            double beta, int curdepth, boolean in) {
        /* end of recursion */
        if (curdepth == 0) {
            /* return result of evaluation function */

            /*
             * evaluate min distance of trace to traces double min =
             * Double.MAX_VALUE; for (Iterator i = traces.iterator();
             * i.hasNext();) { List l = (List) i.next(); double d =
             * distance.distance(trace, l); if (d < min) min = d; }
             */
            return evaluator.eval(traces, trans, st);
        }
        Set itrs = spec.delta(st);
        /* basic tests */
        if ((itrs.size() == 0))
            return evaluator.eval(traces, trans, st);
        /* inputs evaluation */
        double maxi = alpha;
        IOTransition maxt = null;
        for (Iterator i = itrs.iterator(); i.hasNext();) {
            IOTransition tr = (IOTransition) i.next();
            Set end = spec.step(st, tr.label());
            /* update trace and recurse */
            double e = -eval(end, tr, traces, -beta, -alpha, curdepth - 1, true);
            if (e > beta) {
                beta = e;
            }
            if (beta >= alpha)
                return beta;
        }
        return beta;
    }

    /**
     * @return Returns the depth.
     */
    public int getDepth() {
        return depth;
    }

    /**
     * @param depth
     *            The depth to set.
     */
    public void setDepth(int depth) {
        this.depth = depth;
    }

    /**
     * @return Returns the evaluator.
     */
    public Evaluator getEvaluator() {
        return evaluator;
    }

    /**
     * @param evaluator
     *            The evaluator to set.
     */
    public void setEvaluator(Evaluator evaluator) {
        this.evaluator = evaluator;
    }

    /**
     * @return Returns the impl.
     */
    public IOStateMachine getImpl() {
        return impl;
    }

    /**
     * @return Returns the q0.
     */
    public Set getQ0() {
        return q0;
    }

    /**
     * @return Returns the spec.
     */
    public IOAutomaton getSpec() {
        return spec;
    }
}
