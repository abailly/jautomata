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
