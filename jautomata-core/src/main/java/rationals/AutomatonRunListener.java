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

import java.util.Set;

/**
 * An interface to communicate run events.
 * <p>
 * This interface should be implemented by objects interested in being notified
 * of run events, that is the firing of transitions during a run of an automaton.
 * 
 * @author nono
 * @version $Id: AutomatonRunListener.java 2 2006-08-24 14:41:48Z oqube $
 */
public interface AutomatonRunListener {

    /**
     * Callback method for notification of fire events occuring during the 
     * run of an automaton.
     * 
     * @param automatonutomaton where the event took place
     * @param transitions the set of transitions which have been fired
     * @param o the object effectively "read" for firing transitions 
     */
    public void fire(Automaton automaton,Set transitions,Object o);

}

/* 
 * $Log: AutomatonRunListener.java,v $
 * Revision 1.2  2004/07/23 14:36:34  bailly
 * ajout setTag
 *
 * Revision 1.1  2004/07/23 11:59:17  bailly
 * added listener interfaces
 *
*/