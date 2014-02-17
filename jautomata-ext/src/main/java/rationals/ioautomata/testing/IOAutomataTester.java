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
 * Basic interface for testers of IO Automaton.
 * 
 * @author nono
 * @version $Id: IOAutomataTester.java 2 2006-08-24 14:41:48Z oqube $
 */
public interface IOAutomataTester {

    /**
     * Test a given implementation against a specification.
     * This method returns silently if testing is a success, or throws
     * an IOTestFailure exception containing the failed trace of execution
     * if an error is found.
     * 
     * @param impl
     * @param spec
     * @throws IOTestFailure
     */
    public void test(IOStateMachine impl,IOAutomaton spec) throws IOTestFailure;
}
