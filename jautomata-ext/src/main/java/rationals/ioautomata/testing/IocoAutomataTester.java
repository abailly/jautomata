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

import rationals.ioautomata.IOAutomaton;
import rationals.ioautomata.IOStateMachine;

/**
 * An IOAutomaton that checks ioco conformance.
 * The implementation StateMachine is run in parallel with 
 * this tester which sends and receives messages according to
 * spec. The <strong>ioco</strong> conformance is checked which means
 * that all suspension traces of the  spec are tested against 
 * <code>impl</code>.
 * 
 * @author nono
 * @version $Id: IocoAutomataTester.java 2 2006-08-24 14:41:48Z oqube $
 */
public class IocoAutomataTester implements IOAutomataTester {

    /* (non-Javadoc)
     * @see rationals.ioautomata.testing.IOAutomataTester#test(rationals.ioautomata.IOStateMachine, rationals.ioautomata.IOAutomaton)
     */
    public void test(IOStateMachine impl, IOAutomaton spec)
            throws IOTestFailure {
        boolean done = false;
        /* start machine */
        Thread th  = new Thread(impl);
        th.start();
        /* loop */
        while(!done) {
            
        }
    }

}
