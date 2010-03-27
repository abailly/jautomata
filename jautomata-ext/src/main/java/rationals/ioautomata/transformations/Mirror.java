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
 * Created on 14 avr. 2005
 *
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
