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
package rationals;


/** 
 * Interface for State objects
 * 
 * This class defines the notion of state of an automaton. 
 * @author yroos@lifl.fr
 * @version 1.0
 * @see Automaton
 * @see StateFactory
*/
public interface State {


    /** Determines if this state is initial.
     *  @return true iff this state is initial.
     */
    public boolean isInitial();
    
    /** Determines if this state is terminal.
     *  @return true iff this state is terminal.
     */
    public boolean isTerminal();
    
    /** returns a textual representation of this state.
     *  @return a textual representation of this state.
     */
    public String toString();

    /**
     * Sets the initial status of this state.
     * 
     * @param initial
     * @return
     */
    public State setInitial(boolean initial);

    public State setTerminal(boolean terminal);
}
