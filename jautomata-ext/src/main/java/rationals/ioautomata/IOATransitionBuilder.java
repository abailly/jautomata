/*****************************************************************************
 * Copyright 2013 (C) Codehaus.org                                                *
 * ------------------------------------------------------------------------- *
 * Licensed under the Apache License, Version 2.0 (the "License");           *
 * you may not use this file except in compliance with the License.          *
 * You may obtain a copy of the License at                                   *
 *                                                                           *
 * http://www.apache.org/licenses/LICENSE-2.0                                *
 *                                                                           *
 * Unless required by applicable law or agreed to in writing, software       *
 * distributed under the License is distributed on an "AS IS" BASIS,         *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *
 * See the License for the specific language governing permissions and       *
 * limitations under the License.                                            *
 *****************************************************************************/
package rationals.ioautomata;

import rationals.Automaton;
import rationals.Builder;
import rationals.NoSuchStateException;
import rationals.State;


/** Builds transitions containing explicit information about send and/or receive messages. */
public class IOATransitionBuilder implements Builder<IOTransition, IOATransitionBuilder> {

  /* unique id, used for completing messages sources/destination */
  private final String id;

  /*
   * counter is used for creating new unique states within this automaton context.
   */
  private int counter = 0;

  private State currentState;
  private IOTransition.IOLetter currentLabel;
  private Automaton<IOTransition, IOATransitionBuilder> automaton;

  public IOATransitionBuilder(String id) {
    this.id = id;
  }

  /**
   * Creates a new {@link IOAutomaton} with a given id.
   *
   * @param  id identifies the created automaton to provide context for messages sent and received.
   *
   * @return a new (empty) IOAutomaton.
   */
  public static IOAutomaton<IOTransition, IOATransitionBuilder> automaton(String id) {
    return new IOAutomaton<IOTransition, IOATransitionBuilder>(new IOATransitionBuilder(id));
  }

  @Override
  public IOATransitionBuilder build(State state, Automaton<IOTransition, IOATransitionBuilder> auto) {
    this.currentState = state;
    this.currentLabel = null;
    this.automaton = auto;
    return this;

  }

  @Override
  /**
   * Starts creating a new {@link IOAlphabetType#INTERNAL} transition from the current state.
   * <p>
   *     If a transition was in progress, through previous calls to {@link #send(Object)} or {@link #receive(Object)}
   *     then this closes the transition to a new "anonymous" state which becomes the <em>current state</em>.
   * </p>
   *
   * @param message the message which is .
   * @return this  builder for chaining purpose.
   */
  public IOATransitionBuilder on(Object label) {
    Object message = label;
    if (label instanceof String) {
      message = Messages.message("^" + id + "." + message);
    }
    addStateIfNeeded(message, IOAlphabetType.INTERNAL);
    return this;
  }

  @Override
  /**
   * This method <strong>does not</strong> change the <em>current state</em> which implies using it after a succession
   * of {@link #send(Object)} or {@link #receive(Object)} calls will leave the builder in an intermediary state. Use
   * {@link #to(Object)} to explicitly change the current state.
   */
  public IOATransitionBuilder go(Object o) {
    transitionTo(automaton.state(o));
    return this;
  }

  @Override
  public IOATransitionBuilder loop() {
    try {
      automaton.addTransition(new IOTransition(currentState, currentLabel, currentState));
      currentLabel = null;
    } catch (NoSuchStateException e) {
      throw new IllegalStateException("inconsistent automaton structure, missing state " + currentState + " in " + automaton);
    }
    return this;
  }

  @Override
  public IOATransitionBuilder from(Object label) {
    this.currentState = automaton.state(label);
    this.currentLabel = null;
    return this;
  }

  @Override
  public IOTransition build(State from, Object label, State to) {
    return new IOTransition(from, ((IOTransition.IOLetter) label).label, to, ((IOTransition.IOLetter) label).type);
  }

  @Override
  public void setAutomaton(Automaton<IOTransition, IOATransitionBuilder> a) {
    this.automaton = a;
  }

  /**
   * Starts creating a new {@link IOAlphabetType#INPUT} transition from the current state.
   *
   * <p>If a transition was in progress, through previous calls to {@link #send(Object)} or this method
   * then this closes the transition to a new "anonymous" state which becomes the <em>current state</em>.</p>
   *
   * @param  message the message which is input.
   *
   * @return this builder for chaining purpose.
   */
  public IOATransitionBuilder receive(Object message) {
    Object label = message;
    if (message instanceof String) {
      label = Messages.message(id + "<-" + message);
    }
    addStateIfNeeded(label, IOAlphabetType.INPUT);
    return this;
  }

  /**
   * Starts creating a new {@link IOAlphabetType#OUTPUT} transition from the current state.
   *
   * <p>If a transition was in progress, through previous calls to this method or {@link #receive(Object)}
   * then this closes the transition to a new "anonymous" state which becomes the <em>current state</em>.</p>
   *
   * @param  message the message which is output.
   *
   * @return this builder for chaining purpose.
   */
  public IOATransitionBuilder send(Object message) {
    Object label = message;
    if (message instanceof String) {
      label = Messages.message(id + "->" + message);
    }
    addStateIfNeeded(label, IOAlphabetType.OUTPUT);
    return this;
  }

  private void addStateIfNeeded(Object message, IOAlphabetType output) {
    if (currentLabel == null) {
      currentLabel = new IOTransition.IOLetter(message, output);
    } else {
      String newState = hiddenState();
      go(newState).from(newState);
      currentLabel = new IOTransition.IOLetter(message, output);
    }
  }

  private String hiddenState() {
    return "s" + (counter++);
  }

  /**
   * Changes the current state to {@code state} and optionally closes the current transition.
   *
   * <p>This closes the current transition if called after a label has been set with {@link #send(Object)} or {@link
   * #receive(Object)}. Otherwise, it has the same effect than {@link #from(Object)}.</p>
   *
   * @param  state end state of transition to close.
   *
   * @return this object for chaining.
   */
  public IOATransitionBuilder to(Object state) {
    State s = automaton.state(state);
    if (currentLabel != null) {
      transitionTo(s);
    }
    currentState = s;
    return this;
  }

  private void transitionTo(State s) {
    try {
      automaton.addTransition(new IOTransition(currentState, currentLabel, s));
    } catch (NoSuchStateException e) {
      throw new IllegalStateException("no state (" + s + " or " + currentState + ") to build transition");
    }
    currentLabel = null;
  }
}
