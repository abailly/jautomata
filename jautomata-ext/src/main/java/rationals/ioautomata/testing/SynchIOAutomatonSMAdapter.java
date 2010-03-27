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
 * Created on 14 avr. 2005
 * 
 */
package rationals.ioautomata.testing;

import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;

import org.hamcrest.Matcher;

import fr.lifl.utils.Barrier;

import rationals.State;
import rationals.Transition;
import rationals.ioautomata.IOAlphabetType;
import rationals.ioautomata.IOAutomaton;
import rationals.ioautomata.IOStateMachine;
import rationals.ioautomata.IOSynchronization;
import rationals.ioautomata.IOTransition;
import rationals.ioautomata.IOTransitionBuilder;
import rationals.ioautomata.Function;
import rationals.ioautomata.IOTransition.IOLetter;

/**
 * Provide an implementation of IOStateMachine which is controlled by an
 * IOAutomaton.
 * <p>
 * Silent transitions in the IOAutomaton represents non deterministic internal
 * computations. They take a certain time quantum to be performed. This
 * implementation is fair, mono threaded and non-deterministic if the underlying
 * IOAutomaton is non deterministic:
 * <ul>
 * <li>Probability of firing several transitions from a state or set of state is
 * uniform</li>
 * <li>Only one letter is input or output at each step</li>
 * </ul>
 * This class is an instance of Runnable so that it can run in its own thread.
 * Basically, method {@see #run()}sits in a loop waiting for input events and
 * generating output events according to current state of the IOAutomaton. If an
 * input event cannot be consumed, the machine is stopped and its error status
 * is set to true.
 * <p>
 * Input-enabledness behavior can be turned on/off using {@see
 * #setInputEnabled(boolean)}. This implementation is synchronous: the input and
 * output methods are blocking.
 * 
 * @author nono
 * @version $Id: SynchIOAutomatonSMAdapter.java 13 2007-06-01 16:11:03Z oqube $
 */
public class SynchIOAutomatonSMAdapter implements IOStateMachine, Runnable {

	/* the automaton which is run */
	private IOAutomaton<IOTransitionBuilder> auto;

	/* the synchronization test */
	private IOSynchronization synch = new IOSynchronization();

	/* current state of automaton */
	private Set<State> state;

	/* error status */
	private boolean error;

	/* stop flag */
	private boolean stop;

	private Random rand = new Random();

	private final Barrier barrier = new Barrier(2);

	private Object in;

	private Object out;

	/* the input-enabledness of this machine */
	private boolean inputEnabled;

	/* timeout for synchronization */
	private long timeout = 100;

	private boolean ready;

	private MessageAdapter internalHandler = new MessageAdapter(Function.VOID,
			this);

	/**
	 * Create an IOSM with given IOAutomaton as implementation.
	 * 
	 * @param auto
	 *            a non-null IOAutomaton instance.
	 */
	public SynchIOAutomatonSMAdapter(IOAutomaton<IOTransitionBuilder> auto) {
		this.auto = auto;
		this.auto.setBuilder(new IOTransitionBuilder());
		this.state = auto.getStateFactory().stateSet(auto.initials());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see rationals.ioautomata.IOStateMachine#input(java.lang.Object)
	 */
	public void input(Object o) {
		barrier.await();
		synchronized (this) {
			boolean ok = doInput(o);
			/* check input is OK, else error */
			if (!ok && !inputEnabled) {
				error = true;
				stop = true;
			}
			ready = true;
			/* wake up sm */
			this.notify();
		}
	}

	/**
	 * @param o
	 * @return
	 */
	private boolean doInput(Object o) {
		boolean ok = false;
		for (Transition tr : auto.delta(state)) {
			IOTransition.IOLetter lt = (IOLetter) ((IOTransition) tr).label();
			if (IOSynchronization.inputMatchesLetter(o, lt)) {
				doTransition(tr.label());
				ok = true;
			}
		}
		return ok;
	}

	private Object doOutput() {
		Set<Transition> trs = auto.delta(state);
		selectOutputs(trs);
		if (trs.size() == 0)
			return null;
		int r = rand.nextInt(trs.size());
		IOTransition tr = null;
		for (Iterator it = trs.iterator(); r >= 0; r--)
			tr = (IOTransition) it.next();
		doTransition(tr.label());
		return ((IOTransition.IOLetter) tr.label()).label;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see rationals.ioautomata.IOStateMachine#isInputEnabled()
	 */
	public boolean isInputEnabled() {
		return inputEnabled;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see rationals.ioautomata.IOStateMachine#output()
	 */
	public Object output() {
		barrier.await();
		synchronized (this) {
			Object ret = doOutput();
			if (ret == null) {
				error = true;
				stop = true;
			}
			ready = true;
			this.notify();
			return ret;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see rationals.ioautomata.IOStateMachine#output(java.lang.Object[], int,
	 * int)
	 */
	public int output(Object[] o, int start, int len) {
		Object ret = output();
		if (ret == null)
			return 0;
		else {
			o[start] = ret;
			return 1;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see rationals.ioautomata.IOStateMachine#availableOutput()
	 */
	public int availableOutput() {
		return out != null ? 1 : 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		_run: while (!stop) {
			/* get all fireable transitions in current state */
			Set trs = auto.delta(state);
			if (trs.size() == 0)
				break _run;
			int r = rand.nextInt(trs.size());
			IOTransition tr = null;
			for (Iterator it = trs.iterator(); r >= 0; r--)
				tr = (IOTransition) it.next();
			/*
			 * if transition is internal, then execute, else, wait for
			 * communication
			 */
			if (tr.getType() == IOAlphabetType.INTERNAL) {
				doTransition(tr.label());
				internalHandler.handle(tr.label());
			} else {
				ready = false;
				barrier.await();
				synchronized (this) {
					try {
						while (!ready)
							this.wait(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
		/* break barrier */
		synchronized (barrier) {
			barrier.close();
			barrier.notifyAll();
		}
	}

	/**
	 * @param object
	 */
	private void doTransition(Object object) {
		state = auto.step(state, object);
	}

	/**
	 * Remove all input transitions from this set of transitions
	 * 
	 * @param trs
	 */
	private void selectOutputs(Set trs) {
		for (Iterator i = trs.iterator(); i.hasNext();)
			if (((IOTransition) i.next()).getType() != IOAlphabetType.OUTPUT)
				i.remove();
	}

	public boolean isError() {
		return error;
	}

	public void setInputEnabled(boolean inputEnabled) {
		this.inputEnabled = inputEnabled;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see rationals.ioautomata.IOStateMachine#reset()
	 */
	public void reset() {
		this.state = auto.initials();
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
	 * @see
	 * rationals.ioautomata.IOStateMachine#setInternalHandler(rationals.ioautomata
	 * .Function)
	 */
	public void setInternalHandler(Function hdl) {
		this.internalHandler = new MessageAdapter(hdl, this);
	}
}
