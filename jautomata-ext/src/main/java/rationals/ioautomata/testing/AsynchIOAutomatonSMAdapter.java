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
