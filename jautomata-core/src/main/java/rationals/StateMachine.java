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

import java.util.List;
import java.util.Set;

/**
 * An interface for abstract state machines.
 * <p>
 * A state machine - or labelled transition system - is defined by
 * a tuple <code>(Q,S,q0,d)</code> where:
 * <ul>
 * <li><code>Q</code> is a set of states ;</li>
 * <li><code>S</code> is a finite set of labels ;</li>
 * <li><code>q0</code> is the initial state of the machine ;</li>
 * <li><code>d</code> is a transition relation in <code>Q x S x Q</code>.
 * </ul>
 * This definition is slightly modified for this interface as the initials 
 * is defined as a {@see java.util.Set} instead of a single state. <code>null</code>
 * may be used to denote <em>silent transitions</em>, that is unobservable 
 * internal behavior of the machine, which can lead to non determinism.
 * <p>
 * The {@see rationals.Automaton} is the main implementation for this interface.
 * 
 * @author nono
 * @version $Id: StateMachine.java 10 2007-05-30 17:25:00Z oqube $
 */
public interface StateMachine {
    
    /**
     * Returns the alphabet - <code>S</code> - of this state machine.
     * 
     * @return a Set of Object.
     */
    Set<Object> alphabet();
    
    /**
     * @return Returns the id.
     */
    Object getId();

    /**
     * @param id
     *            The id to set.
     */
    void setId(Object id);

    /**
     * Retrieves the state factory associated to this SM.
     * 
     * @return a StateFactory instance
     */
    StateFactory getStateFactory();

    /**
     * Defines the state factory to use for this SM.
     * 
     * @param factory a StateFactory instance.
     */
    void setStateFactory(StateFactory factory);

    /**
     * Returns the set of all transitions of this machine starting from a
     * given state and labelled 
     * with  a given label.
     * 
     * @param state
     *            a state of this SM.
     * @param label
     *            a label used in this SM.
     * @return the set of all transitions of this automaton starting from state
     *         <tt>state</tt> and labelled by <tt>label</tt>. Objects which
     *         are contained in this set are instances of class
     *         <tt>Transition</tt>.
     * @see Transition
     */
    Set<Transition> delta(State state, Object label);

    /**
     * Return all transitions from a State.
     * 
     * @param state
     *            start state
     * @return a new Set of transitions (maybe empty)
     */
    Set<Transition> delta(State state);

    /**
     * Returns all transitions from a given set of states.
     * 
     * @param s a Set of State objects
     * @return a Set of Transition objects 
     */
    Set<Transition> delta(Set<State> s);

    /**
     * Return the set of states this SM will be in
     * after reading the word from start states <code>s</code>.
     * 
     * @param s the set of starting states
     * @param word the word to read.
     * @return the set of reached states. Maybe empty or <code>null</code>.
     */
    Set<State> steps(Set<State> s, List<Object> word);

    /**
     * Return the set of states this SM will be in
     * after reading the word from single start state s.
     * 
     * @param st the starting state
     * @param word the word to read.
     * @return the set of reached states. Maybe empty or <code>null</code>
     */
    Set<State> steps(State st, List<Object> word);

    /**
     * Return the set of states accessible in one transition from given set of
     * states s and letter o.
     * 
     * @param s the starting states
     * @param o the letter 
     * @return a set of reachable states. Maybe empty or <code>null</code>.
     */
    Set<State> step(Set<State> s, Object o);

    /**
     * Returns the set of initial states for this machine.
     * 
     * @return a Set of State objects.
     */
    Set<State> initials();
    
    /**
     * Returns the set of states that can access the given states' set <code>st</code>.
     * This is the inverse relation of <code>d</code>
     * 
     * @param st end states
     * @return a set of states that can reach <code>st</code>. May be empty or null.
     */
    Set<Transition> deltaMinusOne(State st);
}