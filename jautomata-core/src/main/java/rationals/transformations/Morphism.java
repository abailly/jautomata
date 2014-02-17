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
package rationals.transformations;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import rationals.Automaton;
import rationals.NoSuchStateException;
import rationals.State;
import rationals.Transition;

/**
 * A general  class for alphabetic morphism over automaton.
 * <p>
 * A morphism is constructed from a {@see java.util.Map} from letters to
 * letters (ie. from Object to Object). To distinguish between explicit 
 * mapping to <code>null</code> and implicit identity, if a letter is mapped 
 * as is, then it should not be included as a key.
 *  
 * @author nono
 * @version $Id: Morphism.java 2 2006-08-24 14:41:48Z oqube $
 */
public class Morphism implements UnaryTransformation {

    private Map morph;

    public Morphism(Map m) {
        this.morph = m;
    }
    
    /* (non-Javadoc)
     * @see rationals.transformations.UnaryTransformation#transform(rationals.Automaton)
     */
    public Automaton transform(Automaton a) {
        Automaton b = new Automaton();
        /* state map */
        Map stm = new HashMap();
        for(Iterator i = a.delta().iterator();i.hasNext();) {
            Transition tr = (Transition)i.next();
            State ns = tr.start();
            State nss = (State)stm.get(ns);
            if(nss == null) {
                nss = b.addState(ns.isInitial(),ns.isTerminal());
                stm.put(ns,nss);
            }
            State ne = tr.end();
            State nse = (State)stm.get(ne);
            if(nse == null) {
                nse = b.addState(ne.isInitial(),ne.isTerminal());
                stm.put(ne,nse);
            }
            Object lbl = tr.label();
            if(!morph.containsKey(lbl))
                try {
                    b.addTransition(new Transition(nss,lbl,nse));
                } catch (NoSuchStateException e) {
                }
            else
                try {
                    b.addTransition(new Transition(nss,morph.get(lbl),nse));
                } catch (NoSuchStateException e1) {
                }
        }
        return b;
    }

}
