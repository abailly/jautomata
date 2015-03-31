/*
 * (C) Copyright 2001 Arnaud Bailly (arnaud.oqube@gmail.com),
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

/**
 * Defines a Transition (an edge from a state to a state) in an Automaton
 * 
 * This class defines the notion of transition of an automaton. a transition is
 * a triple <em>(q , l , q')</em> where <em>q, q'</em> are states and
 * <em>l</em> a label. States <em>q</em> and <em>q'</em> must belong to
 * the same automaton <em>A</em> and the transition may only be used with this
 * automaton <em>A</em>.
 * 
 * @author yroos@lifl.fr
 * @version 1.0
 * @see Automaton
 */
public class Transition<L> {

  private int hashCodeCache = Integer.MIN_VALUE;

  private State start;

  private L label;

  private State end;

  /**
   * Creates a new transition <em>(q , l , q')</em>.
   * 
   * @param start
   *          the state <em>q</em> for this transition <em>(q , l , q')</em>.
   * @param label
   *          the label <em>l</em>
   * @param end
   *          the state <em>q'</em> for this transition <em>(q , l , q')</em>.
   */
  public Transition(State start, L label, State end) {
    this.start = start;
    this.label = label;
    this.end = end;
  }

  /**
   * Creates a new (invalid) transition. This transition is meant to be updated
   * internally by an automaton.
   * 
   * @param start
   *          the starting state.
   */
  Transition(State start) {
    this.start = start;
  }

  /**
   * Returns the starting state of this transition.
   * 
   * @return the starting state of this transition, that is the state <em>q</em>
   *         for this transition <em>(q , l , q')</em>.
   */
  public State start() {
    return start;
  }

  /**
   * Returns the label this transition.
   * 
   * @return the label state of this transition, that is the object <em>l</em>
   *         for this transition <em>(q , l , q')</em>.
   */
  public L label() {
    return label;
  }

  /**
   * Returns the ending state of this transition.
   * 
   * @return the ending state of this transition, that is the state <em>q'</em>
   *         for this transition <em>(q , l , q')</em>.
   */
  public State end() {
    return end;
  }

  /**
   * returns a textual representation of this transition.
   * 
   * @return a textual representation of this transition based
   */
  @Override
  public String toString() {
    if (label == null) {
      return "(" + start + " , epsilon , " + end + ")";
    } else {
      return "(" + start + " , " + label + " , " + end + ")";
    }
  }

  /**
   * Determines if this transition is equal to the parameter.
   * 
   * @param o
   *          any object.
   * @return true iff this transition is equal to the parameter. That is if
   *         <tt>o</tt> is a transition which is composed same states and
   *         label (in the sense of method <tt>equals</tt>).
   */
  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Transition)) return false;
    @SuppressWarnings("unchecked")
	Transition<L> t = (Transition<L>) o;
    if (label != t.label) {
      if (label == null || t.label == null)
        return false;
      if (!t.label.equals(label))
        return false;
    }
    return (start == t.start()) && (end == t.end());
  }

  /**
   * Returns a hashcode value for this transition.
   * 
   * @return a hashcode value for this transition.
   */
  @Override
  public int hashCode() {
    /* store computed value */
    if (hashCodeCache != Integer.MIN_VALUE)
      return hashCodeCache;
    int x = start == null ? 0 : start.hashCode();
    int y = end == null ? 0 : end.hashCode();  
    int z = label == null ? 0 : label.hashCode();  
    int hash = 17;
    hash = hash * 31 + x;
    hash = hash * 31 + y;
    hash = hash * 31 + z;
    hashCodeCache = hash;
    return hash;
  }

  /**
   * Replaces the label for this transition
   * <p>
   * WARNING: this method is extremely dangerous as it does not update the
   * alphabet of the automaton this transition is part of. Be sure you know what
   * you are doing or else everything could break down
   * 
   * @param msg
   */
  void setLabel(L obj) {
    this.label = obj;
  }

}
