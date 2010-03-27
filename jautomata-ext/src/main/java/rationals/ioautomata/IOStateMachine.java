/*______________________________________________________________________________
 * 
 * Copyright 2005 Arnaud Bailly - NORSYS/LIFL
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * (1) Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 * (2) Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in
 *     the documentation and/or other materials provided with the
 *     distribution.
 *
 * (3) The name of the author may not be used to endorse or promote
 *     products derived from this software without specific prior
 *     written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Created on 14 avr. 2005
 *
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
