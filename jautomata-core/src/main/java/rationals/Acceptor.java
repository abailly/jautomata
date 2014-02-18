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
package rationals;

import java.util.List;
import java.util.Set;

/**
 * @author nono
 * @version $Id: Acceptor.java 10 2007-05-30 17:25:00Z oqube $
 */
public interface Acceptor {
    /**
     * Checks this automaton accepts the given "word".
     * A word is a list of objects. This method checks that reading <code>word</code>
     * starting from initials state leads to at least one terminal state.
     * 
     * @param word
     * @return
     */
    boolean accept(List<Object> word);

    /**
     * Return a trace of states reading word from start state. 
     * If start state is null, assume reading from
     * initials().
     * This method returns a List of Set objects showing all the states 
     * reached by this run while reading <code>word</code> starting from <code>start</code>.
     * 
     * @param word a List of objects in this automaton's alphabet
     * @param start a starting State. Maybe null
     * @return a List of Set of State objects
     */
    List<Set<State>> traceStates(List<Object> word, State start);
    
    /*
     *  (non-Javadoc)
     * @see rationals.Acceptor#steps(java.util.List)
     */
    Set<State> steps(List<Object> word);

}