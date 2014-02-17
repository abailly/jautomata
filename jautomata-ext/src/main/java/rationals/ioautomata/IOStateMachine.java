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
package rationals.ioautomata;

/**
 * This interface models the behavior of a opaque state machine with input and
 * output letters.
 * <p>
 * An IOStateMachine runs by accepting inputs and generating outputs. If it is
 * input-enabled, a property which can be queried using {@see #isInputEnabled()},
 * then inputs can be sent at any moment to the machine which means that calling
 * the method {@see #input(Object)} should never block. If it is not
 * input-enabled, then calling this method may or may not block depending on the
 * underlying implementation and state of the machine.
 * <p>
 * Outputs from the machine can be polled using the {@see #output()} or
 * {@see #output(Object[],int,int)} method. The first method returns one
 * available output from the machine if there is some output available, or null
 * if no output is available. The second one fills the given array with
 * available output up size or length of array (whichever is shorter), starting
 * at given index. The number of output events available can be queried using
 * {@see #outputAvailable()} method.
 * 
 * 
 * @author nono
 * @version $Id: IOStateMachine.java 13 2007-06-01 16:11:03Z oqube $
 */
public interface IOStateMachine extends Runnable {

  /**
   * Sends an input to this machine.
   * 
   * @param o
   *          the input event.
   */
  public void input(Object o);

  /**
   * Returns true if input never blocks, false otherwise.
   * 
   * @return inputenabledness of this machine.
   */
  public boolean isInputEnabled();

  /**
   * Receives an output from this machine or null.
   * 
   * @return an output event or null if no output is available.
   */
  public Object output();

  /**
   * Fills array <code>o</code>, starting at <code>index</code> with at
   * most <code>len</code> available output events. The number of events
   * actually output is returned.
   * 
   * @param o
   *          the array to fill with output events.
   * @param start
   *          start index of array filling.
   * @param len
   *          maximum number of events to receive.
   * @return number of events sent.
   */
  public int output(Object[] o, int start, int len);

  /**
   * Returns the number of available output events from this machine.
   * 
   * @return number of available outputs.
   */
  public int availableOutput();

  /**
   * Reset this machine to its initial state.
   * 
   * 
   */
  public void reset();

  /**
   * Stop this machine's thread if running.
   * 
   */
  public void stop();

  /**
   * Set the function associated with processing internal messages in this state
   * machine.
   * 
   * @param hdl
   *          the function. May not be null.
   */
  void setInternalHandler(Function hdl);
}
