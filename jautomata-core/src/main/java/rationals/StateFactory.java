/*
 * (C) Copyright 2004 Arnaud Bailly (arnaud.oqube@gmail.com),
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
 * This class is used by Automaton objects to create new states on A user can
 * implement its own version of StateFactory by providing an implementation for
 * createState
 *
 * @author Arnaud.Bailly - bailly@lifl.fr
 * @version $Id: StateFactory.java 10 2007-05-30 17:25:00Z oqube $
 */
public interface StateFactory<L, Tr extends Transition<L>, T extends Builder<L, Tr, T>> {

    /**
     * @param initial
     * @param terminal
     * @return
     */
    State create(boolean initial, boolean terminal);

    /**
     * Return a new empty set that can contains State instances created by this
     * factory. This method is provided for optimisation purposes so that more
     * efficient implementations than plain sets can be used for handling sets of
     * states.
     *
     * @return an - opaque - implementation of Set.
     */
    Set<State> stateSet();

    /**
     * Returns a new Set which is a copy of <code>s</code>. The given set s
     * must have been created through <strong>this</strong> StateFactory object
     * to ensure consistent behavior.
     *
     * @param s a Set
     * @return a shallow copy of <code>s</code>
     */
    Set<State> stateSet(Set<State> s);

    /**
     * Returns a new StateFactory object which is the same as this StateFactory.
     *
     * @return an initialized StateFactory.
     */
    Object clone();

    /**
     * @param automaton
     */
    void setAutomaton(Automaton<L, Tr, T> automaton);

    State create(boolean initial, boolean terminal, Object label);
}
// /*
// * $Log: StateFactory.java,v $
// * Revision 1.3 2004/07/20 13:21:25 bonte
// * *** empty log message ***
// *
// */=======
// /*
// * $Log: StateFactory.java,v $
// * Revision 1.3 2004/07/20 13:21:25 bonte
// * *** empty log message ***
// *
// * Revision 1.2 2004/07/19 06:39:02 bailly
// * made Automaton, State and Transition subclasses of Graph API
// * modified StateFactory API
// *
