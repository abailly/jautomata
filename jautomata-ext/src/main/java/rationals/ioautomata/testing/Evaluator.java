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
 * Created on 3 juin 2005
 *
 */
package rationals.ioautomata.testing;

import java.util.Set;

import rationals.ioautomata.IOTransition;

/**
 * Base interface for evaluating test traces during a test run.
 * 
 * @author nono
 * @version $Id: Evaluator.java 2 2006-08-24 14:41:48Z oqube $
 */
public interface Evaluator {

    /**
     * Sets the tester that is going to interact with this evaluator.
     * 
     * @param spec an AlphaBetaTester.
     */
    void setTester(AlphaBetaIOTester tester);

    /**
     * This method is called by tester to indicate that 
     * testing process has started.
     *
     */
    void start();
    
    /**
     * Given a set of already executed test traces and a current state,
     * evaluate  a <code>trace</code> that lead to this state.
     * 
     * @param traces the set of test traces already executed.
     * @param trans the transition to evaluate.
     * @param state the state set. This is the arrival state of <code>trans</code>
     * @return a number which is greater than 0 denoting relative interest in 
     * executing this trace.
     */
    double eval(Set traces, IOTransition tr, Set st);

    /**
     * Return true if the given test suite has reached the goal of 
     * this evaluator.
     * 
     * @param traces a set of list of objects denoting messages.
     * @return true if testing should stop, false otherwise.
     */
    boolean isEnough(Set traces);

    /**
     * Called by tester when a transition is fired.
     * 
     * @param state the starting state set.
     * @param next the ending state set.
     */
    void step(Set state, Set next);

    /**
     * Called by tester when a reset occurs.
     * 
     */
    void reset();

}
