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

import java.util.Iterator;
import java.util.Set;

import rationals.ioautomata.IOAlphabetType;
import rationals.ioautomata.IOTransition;

/**
 * Base class for testing IOAutomaton.
 * Provides some utilities to be used by underlying classes.
 * 
 * @author nono
 * @version $Id: AbstractIOTester.java 2 2006-08-24 14:41:48Z oqube $
 */
public abstract class AbstractIOTester implements IOAutomataTester {

    /**
     * Remove all input transitions from this set of transitions
     * 
     * @param trs
     */
    public void filterInputs(Set trs) {
        for (Iterator i = trs.iterator(); i.hasNext();)
            if (((IOTransition) i.next()).getType() != IOAlphabetType.INPUT)
                i.remove();
    }

    /**
     * Remove all output transitions from this set of transitions
     * 
     * @param trs
     */
    public void filterOutputs(Set trs) {
        for (Iterator i = trs.iterator(); i.hasNext();)
            if (((IOTransition) i.next()).getType() != IOAlphabetType.OUTPUT)
                i.remove();
    }

    static class Move {
    
        static final Move out = new Move("out");
    
        static final Move reset = new Move("reset");
    
        static final Move terminate = new Move("terminate");
    
        Object move;
    
        Move(Object o) {
            this.move = o;
        }
    }


    
}
