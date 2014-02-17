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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import rationals.ioautomata.IOAlphabetType;
import rationals.ioautomata.IOAutomaton;
import rationals.ioautomata.IOStateMachine;
import rationals.ioautomata.IOTransition;
import rationals.transformations.TransformationsToolBox;

/**
 * A tester implementing the algorithm from Pyhala and Heljanko.
 * 
 * @author nono
 * @version $Id: PyhalaIOAutomataTester.java 2 2006-08-24 14:41:48Z oqube $
 */
public class PyhalaIOAutomataTester extends AbstractIOTester {

    /* set of covered transitions */
    private Set cover = new HashSet();

    /* number of transitions in spec */
    private int numtrans;

    /* current coverage achieved */
    private double coverage;

    /* random number generator */
    private Random rand = new Random();

    // Algorithm parameters
    private final int maxNbRun;

    private final double minCoverage;

    private final int resetInterval;

    private double probGreedy;

    private IOAutomaton spec;

    private IOStateMachine impl;

    protected final double probTerminate;

    protected final double probReset;

    protected final double probInput;

    public PyhalaIOAutomataTester(int maxrun, double mincover,
            int resetinterval, double probgreedy, double probTerminate,
            double probReset, double probInput) {
        this.maxNbRun = maxrun;
        this.minCoverage = mincover;
        this.resetInterval = resetinterval;
        if (probgreedy < 0 || probgreedy > 1)
            throw new IllegalArgumentException(
                    "Probability of selecting greedy move must be between 0.0 and 1.0");
        this.probGreedy = probgreedy;
        if (probTerminate + probReset + probInput >= 1)
            throw new IllegalArgumentException(
                    "Sum of probabilities for random move cannot excess 1");
        this.probTerminate = probTerminate;
        this.probReset = probReset;
        this.probInput = probInput;

    }

    /*
     * (non-Javadoc)
     * 
     * @see rationals.ioautomata.testing.IOAutomataTester#test(rationals.ioautomata.IOStateMachine,
     *      rationals.ioautomata.IOAutomaton)
     */
    public void test(IOStateMachine impl, IOAutomaton spec)
            throws IOTestFailure {
        cover = new HashSet();
        numtrans = spec.delta().size();
        coverage = 0;
        this.spec = spec;
        this.impl = impl;
        List trace = new ArrayList();
        Set /* < List < Object > > */traces = new HashSet();
        Set state = TransformationsToolBox
                .epsilonClosure(spec.initials(), spec);
        while (true) {
            Move mv = testMove(trace, traces, state);
            if (mv == Move.terminate) {
                return;
            } else if (mv == Move.reset) {
                impl.reset();
                traces.add(trace);
                trace = new ArrayList();
                state = TransformationsToolBox.epsilonClosure(spec.initials(),
                        spec);
                continue;
            } else if (mv == Move.out) {
                /* get output from impl */
                Object o = impl.output();
                /* transform as output for spec */
                IOTransition.IOLetter lt = new IOTransition.IOLetter(o,
                        IOAlphabetType.OUTPUT);
                /* find next state */
                Set next = spec.step(state, lt);
                if (!next.isEmpty()) {
                    trace.add(lt);
                    state = next;
                } else {
                    /* failure */
                    IOTestFailure fail = new IOTestFailure("unexpected output");
                    fail.setFailureTrace(trace);
                    throw fail;
                }
            } else {
                /* send input to impl */
                impl.input(mv.move);
                IOTransition.IOLetter lt = new IOTransition.IOLetter(mv.move,
                        IOAlphabetType.INPUT);
                state = spec.step(state, lt);
                trace.add(lt);
            }
        }
    }

    /**
     * @param state
     * @return
     */
    private Move randomTestMove(Set state) {
        double rnd = rand.nextDouble();
        if (rnd < probTerminate)
            return Move.terminate;
        else if (rnd < probReset + probTerminate)
            return Move.reset;
        else {
            Set st = TransformationsToolBox.epsilonClosure(state, spec);
            Set trs = spec.delta(st);
            filterInputs(trs);
            if (trs.size() == 0 || rnd > probReset + probTerminate + probInput)
                return Move.out;
            /* select an input randomly */
            int n = rand.nextInt(trs.size());
            IOTransition tr = null;
            for (Iterator i = trs.iterator(); n >= 0; n--)
                tr = (IOTransition) i.next();
            return new Move(((IOTransition.IOLetter) tr.label()).label);
        }
    }

    /**
     * Implements HeuristicTestMove
     * 
     * @param trace
     * @param traces
     * @param state
     * @return
     */
    private Move testMove(List trace, Set traces, Set state) {
        if (traces.size() > maxNbRun || coverage > minCoverage)
            return Move.terminate;
        if (trace.size() > resetInterval)
            return Move.reset;
        if (rand.nextDouble() < probGreedy)
            return greedyTestMove(state);
        else
            return randomTestMove(state);
    }

    /**
     * Implementation of GreedyTestMove
     * 
     * @param state
     * @return
     */
    private Move greedyTestMove(Set state) {
        boolean inputUncovered = uncoveredInputs(state);
        boolean outputUncovered = uncoveredOutputs(state);
        int choice = 0;
        /* select move according to coverage */
        if (inputUncovered && outputUncovered) {
            if (rand.nextDouble() < probReset + probTerminate + probInput) {
                choice = 1; /* input */
            } else
                choice = 2; /* output */
        } else if (inputUncovered && !outputUncovered)
            choice = 1;
        else if (!inputUncovered && outputUncovered)
            choice = 2;
        switch (choice) {
        case 1: /* input */
            return randomUncoveredInput(state);
        case 2:
            return Move.out;
        default:
            /* all transitions covered - try to find one by searching in graph */
            return lookaheadTestMove(state);
        }
    }

    /**
     * Find an uncovered move.
     * 
     * @param state
     * @return
     */
    private Move lookaheadTestMove(Set state) {
        return null;
    }

    /**
     * @param state
     * @return
     */
    private Move randomUncoveredInput(Set state) {
        Set st = TransformationsToolBox.epsilonClosure(state, spec);
        Set trs = spec.delta(st);
        filterInputs(trs);
        trs.removeAll(cover);
        /* select an input randomly */
        int n = rand.nextInt(trs.size());
        IOTransition tr = null;
        for (Iterator i = trs.iterator(); n >= 0; n--)
            tr = (IOTransition) i.next();
        return new Move(((IOTransition.IOLetter) tr.label()).label);
    }

    /**
     * @param state
     * @return
     */
    private boolean uncoveredOutputs(Set state) {
        Set trs = spec
                .delta(TransformationsToolBox.epsilonClosure(state, spec));
        filterOutputs(trs);
        if (trs.isEmpty())
            return false;
        /* check there exists uncovered input transitions */
        return !cover.containsAll(trs);
    }

    /**
     * @param state
     * @return
     */
    private boolean uncoveredInputs(Set state) {
        Set trs = spec
                .delta(TransformationsToolBox.epsilonClosure(state, spec));
        filterInputs(trs);
        if (trs.isEmpty())
            return false;
        /* check there exists uncovered input transitions */
        return !cover.containsAll(trs);
    }

}
