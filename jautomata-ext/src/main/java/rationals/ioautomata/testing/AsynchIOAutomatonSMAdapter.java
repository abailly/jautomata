/*
 * ______________________________________________________________________________
 * 
 * Copyright 2005 Arnaud Bailly - NORSYS/LIFL
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * (1) Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * (2) Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * (3) The name of the author may not be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * Created on 16 avr. 2005
 * 
 */
package rationals.ioautomata.testing;

import rationals.ioautomata.IOAutomaton;
import rationals.ioautomata.IOAutomatonSMAdapter;
import rationals.ioautomata.IOTransition;
import rationals.ioautomata.IOTransitionBuilder;

import java.util.concurrent.BlockingQueue;

/**
 * An asynchronous implementation of StateMachine backed up by an IOAutomaton.
 * <p>
 * This implementation is asynchronous in the sense that input and output
 * methods are non blocking.
 * 
 * @author nono
 * @version $Id: AsynchIOAutomatonSMAdapter.java 14 2007-06-04 20:47:25Z oqube $
 */
public class AsynchIOAutomatonSMAdapter extends IOAutomatonSMAdapter {

  private BlockingQueue inQueue;

  private BlockingQueue outQueue;

  private boolean stop;

  private boolean error;

  public AsynchIOAutomatonSMAdapter(IOAutomaton<IOTransition,IOTransitionBuilder> auto) {
    super(auto);
    setInputEnabled(true);
  }

  public AsynchIOAutomatonSMAdapter(IOAutomaton<IOTransition,IOTransitionBuilder> a,
      BlockingQueue queue, BlockingQueue queue2) {
    this(a);
    this.inQueue = queue;
    this.outQueue = queue2;
  }

  /*
   * (non-Javadoc)
   * 
   * @see rationals.ioautomata.IOStateMachine#input(java.lang.Object)
   */
  public void input(Object o) {
    inQueue.add(o);
    synchronized (this) {
      notifyAll();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see rationals.ioautomata.IOStateMachine#output()
   */
  public Object output() {
    Object out = outQueue.poll();
    return out;
  }

  /*
   * (non-Javadoc)
   * 
   * @see rationals.ioautomata.IOStateMachine#output(java.lang.Object[], int,
   *      int)
   */
  public int output(Object[] out, int start, int len) {
    int i;
    for (i = start; i < len && i < out.length; i++) {
      Object o = outQueue.poll();
      if (o == null)
        break;
      out[i] = o;
    }
    return i - start;
  }

  /*
   * (non-Javadoc)
   * 
   * @see rationals.ioautomata.IOStateMachine#availableOutput()
   */
  public int availableOutput() {
    return outQueue.size();
  }

  /*
   * (non-Javadoc)
   * 
   * @see rationals.ioautomata.IOStateMachine#stop()
   */
  public void stop() {
    this.stop = true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Runnable#run()
   */
  public void run() {
    _run: while (!stop) {
      Object in = inQueue.poll();
      /* find a transition with this label */
      super.input(in);
      Object out = super.output();
      if (out == null) // nothing to do
        synchronized (this) {
          do {
            try {
              wait(200);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
            out = super.output();
          } while (inQueue.isEmpty() && out == null);
        }
      if (out != null) {
        try {
          outQueue.put(out);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }

  public boolean isError() {
    return error;
  }

  public BlockingQueue getInQueue() {
    return inQueue;
  }

  public void setInQueue(BlockingQueue inQueue) {
    this.inQueue = inQueue;
  }

  public BlockingQueue getOutQueue() {
    return outQueue;
  }

  public void setOutQueue(BlockingQueue outQueue) {
    this.outQueue = outQueue;
  }
}
