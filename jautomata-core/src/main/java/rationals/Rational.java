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
package rationals;

import java.util.Set;

/**
 * @author nono
 * @version $Id: Rational.java 10 2007-05-30 17:25:00Z oqube $
 */
public interface Rational {
  /**
   * Returns a new instance of state which will be initial and terminal or not
   * depending of parameters.
   * 
   * @param initial
   *          if true, the new state will be initial; otherwise this state will
   *          be non initial.
   * @param terminal
   *          if true, the new state will be terminal; otherwise this state will
   *          be non terminal.
   * @return a new state, associated with this automaton. This new state should
   *         be used only with this automaton in order to create a new
   *         transition for this automaton.
   * @see Transition
   */
  State addState(boolean initial, boolean terminal);

  /**
   * Returns the alphabet <em>X</em> associated with this automaton.
   * 
   * @return the alphabet <em>X</em> associated with this automaton.
   */
  Set<Object> alphabet();

  /**
   * Returns the set of states <em>Q</em> associated with this automaton.
   * 
   * @return the set of states <em>Q</em> associated with this automaton.
   *         Objects which are contained in this set are instances of class
   *         <tt>State</tt>.
   * @see State
   */
  Set<State> states();

  /**
   * Returns the set of initial states <em>I</em> associated with this
   * automaton.
   * 
   * @return the set of initial states <em>I</em> associated with this
   *         automaton. Objects which are contained in this set are instances of
   *         class <tt>State</tt>.
   * @see State
   */
  Set<State> initials();

  /**
   * Returns the set of terminal states <em>T</em> associated with this
   * automaton.
   * 
   * @return set of terminal states <em>T</em> associated with this automaton.
   *         Objects which are contained in this set are instances of class
   *         <tt>State</tt>.
   * @see State
   */
  Set<State> terminals();

  /**
   * Returns the set of all accessible states in this automaton.
   * 
   * @return the set of all accessible states in this automaton. A state
   *         <em>s</em> is accessible if there exists a path from an initial
   *         state to <em>s</em>. Objects which are contained in this set are
   *         instances of class <tt>State</tt>.
   * @see State
   */
  Set<State> accessibleStates();

  /**
   * Returns the set of states that can be accessed in this automaton starting
   * from given set of states
   * 
   * @param states
   *          a non null set of starting states
   * @return a - possibly empty - set of accessible states
   */
  Set<State> accessibleStates(Set<State> states);

  /**
   * Returns the set of co-accesible states for a given set of states, that is
   * the set of states from this automaton from which there exists a path to a
   * state in <code>states</code>.
   * 
   * @param states
   *          a non null set of ending states
   * @return a - possibly empty - set of coaccessible states
   */
  Set<State> coAccessibleStates(Set<State> states);

  /**
   * Returns the set of all co-accessible states in this automaton.
   * 
   * @return the set of all co-accessible states in this automaton. A state
   *         <em>s</em> is co-accessible if there exists a path from this
   *         state <em>s</em> to a terminal state. Objects which are contained
   *         in this set are instances of class <tt>State</tt>.
   * @see State
   */
  Set<State> coAccessibleStates();

  /**
   * Returns the set of all states which are co-accessible and accessible in
   * this automaton.
   * 
   * @return the set of all states which are co-accessible and accessible in
   *         this automaton. A state <em>s</em> is accessible if there exists
   *         a path from an initial state to <em>s</em>. A state <em>s</em>
   *         is co-accessible if there exists a path from this state <em>s</em>
   *         to a terminal state. Objects which are contained in this set are
   *         instances of class <tt>State</tt>.
   * @see State
   */
  Set<State> accessibleAndCoAccessibleStates();

  /**
   * Returns the set of all transitions of this automaton
   * 
   * @return the set of all transitions of this automaton Objects which are
   *         contained in this set are instances of class <tt>Transition</tt>.
   * @see Transition
   */
  Set<Transition> delta();

  /**
   * Returns the set of all transitions of this automaton starting from a given
   * state and labelled b a given label.
   * 
   * @param state
   *          a state of this automaton.
   * @param label
   *          a label used in this automaton.
   * @return the set of all transitions of this automaton starting from state
   *         <tt>state</tt> and labelled by <tt>label</tt>. Objects which
   *         are contained in this set are instances of class
   *         <tt>Transition</tt>.
   * @see Transition
   */
  Set<Transition> delta(State state, Object label);

  /**
   * Return all transitions from a State
   * 
   * @param state
   *          start state
   * @return a new Set of transitions (maybe empty)
   */
  Set<Transition> delta(State state);

  Set<Transition> deltaFrom(State from, State to);

  /**
   * Returns the set of all transitions of the reverse of this automaton
   * 
   * @return the set of all transitions of the reverse of this automaton. A
   *         reverse of an automaton <em>A = (X , Q , I , T , D)</em> is the
   *         automaton <em>A' = (X , Q , T , I , D')</em> where <em>D'</em>
   *         is the set <em>{ (q , l , q') | (q' , l , q) in D}</em>. Objects
   *         which are contained in this set are instances of class
   *         <tt>Transition</tt>.
   * @see Transition
   */
  Set<Transition> deltaMinusOne(State state, Object label);

  /**
   * Adds a new transition in this automaton if it is a new transition for this
   * automaton. The parameter is considered as a new transition if there is no
   * transition in this automaton which is equal to the parameter in the sense
   * of method <tt>equals</tt> of class <tt>Transition</tt>.
   * 
   * @param transition
   *          the transition to add.
   * @throws NoSuchStateException
   *           if <tt>transition</tt> is <tt>null</<tt>
   * or if <tt>transition</tt> = <em>(q , l , q')</em> and <em>q</em> or
   * <em>q'</em> does not belong to <em>Q</em> the set of the states
   * of this automaton.
   */
  void addTransition(Transition transition) throws NoSuchStateException;

  /**
   * @param st
   * @return
   */
  Set<Transition> deltaMinusOne(State st);

  /**
   * @param st
   * @return
   */
  Set<State> accessibleStates(State st);
}