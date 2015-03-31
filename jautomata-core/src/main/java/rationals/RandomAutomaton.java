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
package rationals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Instances of this class are random automata. A RandomAutomaton is generated
 * according to following parameters :
 * <ul>
 * <li>The number of states in the automaton</li>
 * <li>The number of terminal states</li>
 * <li>The alphabet : if the alphabet contains a <code>null</code> element,
 * it will be used and the resulting automaton will contain epsilon-transitions
 * </li>
 * <li>The mean transition density which is the number of transitions in the
 * automaton divided by the square of the number of states times the size of the
 * alphabet</li>
 * <li>The standard deviation of the transition density</li>
 * <li>A flag indicating if the automaton should be deterministic or not. Note
 * that if the alphabet contains epsilon, then the resulting automaton will most
 * probably be non-deterministic even if this flag is set.</li>
 * </ul>
 * The result is an - non reduced - automaton with a single start state and
 * random transitions following a normal distribution according to preceding
 * parameters over the alphabet.
 * 
 * @version $Id: RandomAutomaton.java 2 2006-08-24 14:41:48Z oqube $
 */
public class RandomAutomaton<L, Tr extends Transition<L>, T extends Builder<L, Tr, T>> extends Automaton<L, Tr, T> {

    private static final Random rand = new Random();

    private int nstate;

    private int fstate;

    private L[] alph;

    private double density;

    private double deviation;

    /**
     * Construct a RandomAutomaton according to the given parameters.
     * 
     * @param nstate
     *            number of total states
     * @param fstate
     *            number of final states
     * @param alphabet
     *            alphabet
     * @param density
     *            mean transition density
     * @param deviation
     *            transition density standard deviation
     * @param det
     *            is the result deterministic
     */
    public RandomAutomaton(int nstate, int fstate, L[] alph, double density, double deviation, boolean det) {
        this.nstate = nstate;
        this.fstate = fstate;
        this.alph = alph;
        this.density = density;
        this.deviation = deviation;
        if (det)
            makeDFA();
        else
            makeNFA();
    }

    /**
     *  
     */
    private void makeNFA() {
        /* create initial state and other states */
    	addState(true, false);
        for (int i = 0; i < fstate; i++)
            addState(false, true);
        for (int i = fstate; i < nstate; i++)
            addState(false, false);
        State[] sts = states().toArray(new State[nstate + 1]);
        /* create transitions */
        Iterator<State> it = states().iterator();
        while (it.hasNext()) {
            State from = it.next();
            int c = alph.length * sts.length * sts.length;
            /* number of transitions from this state to other state */
            int nt = (int) (c * (deviation * rand.nextGaussian() + density));
            for (int i = 0; i < nt; i++) {
                State to = sts[rand.nextInt(sts.length)];
                L lbl = alph[rand.nextInt(alph.length)];
                try {
                    /* create transition */
                    addTransition(new Transition<L>(from, lbl, to));
                } catch (NoSuchStateException e1) {
                }
            }
        }
    }

    /**
     *  
     */
    private void makeDFA() {
        /* create initial state and other states */
        State init = addState(true, false);
        List<State> todo = new ArrayList<>();
        List<State> done = new ArrayList<>();
        int fs = fstate;
        int ns = nstate;
        todo.add(init);
        while (ns > 0) {
            /* pop state */
            State from = (State) todo.remove(0);
            done.add(from);
            /* list for alph */
            List<L> l = new ArrayList<>(Arrays.asList(alph));
            /* number of transitions from this state to other state */
            int nt = (int) (deviation * rand.nextGaussian() + density);
            for (int i = 0; i < nt && !l.isEmpty(); i++) {
                /*
                 * select a state : this an already visited state with
                 * probability (done.size() / nstate)
                 */
                State to = null;
                double r = rand.nextDouble() * (nstate - 1);
                if ((int) r < done.size()) {
                    to = done.get((int) r);
                } else {
                    /*
                     * state is final with probability fs / ns
                     */
                    r = rand.nextDouble() * ns;
                    to = addState(false, r < fs);
                    todo.add(to);
                    ns--;
                    if (r < fs)
                        fs--;
                }
                L lbl = l.remove(rand.nextInt(l.size()));
                try {
                    /* create transition */
                    addTransition(new Transition<>(from, lbl, to));
                } catch (NoSuchStateException e1) {
                }
            }
        }
    }
}