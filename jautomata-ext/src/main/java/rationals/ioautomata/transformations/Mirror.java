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
package rationals.ioautomata.transformations;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import rationals.NoSuchStateException;
import rationals.State;
import rationals.ioautomata.IOAlphabetType;
import rationals.ioautomata.IOAutomaton;
import rationals.ioautomata.IOTransition;

/**
 * This class constructs the mirror of an IOAutomaton: Input and Ouput
 * transitions are reversed, internal transitions are preserved.
 * 
 * @author nono
 * @version $Id: Mirror.java 2 2006-08-24 14:41:48Z oqube $
 */
public class Mirror {

    public IOAutomaton transform(IOAutomaton a) {
        IOAutomaton ret = new IOAutomaton();
        Map sm = new HashMap();
        for (Iterator i = a.states().iterator(); i.hasNext();) {
            State st = (State) i.next();
            State ns = ret.addState(st.isInitial(), st.isTerminal());
            sm.put(st, ns);
        }
        /* transitions */
        for (Iterator i = a.delta().iterator(); i.hasNext();) {
            IOTransition tr = (IOTransition) i.next();
            IOTransition.IOLetter lt = (IOTransition.IOLetter) tr.label();
            try {
                ret
                        .addTransition(new IOTransition(
                                (State) sm.get(tr.start()),
                                lt.label,
                                (State) sm.get(tr.end()),
                                lt.type == IOAlphabetType.INPUT ? IOAlphabetType.OUTPUT
                                        : (lt.type == IOAlphabetType.OUTPUT ? IOAlphabetType.INPUT
                                                : IOAlphabetType.INTERNAL)));
            } catch (NoSuchStateException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }
}
