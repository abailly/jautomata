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

import java.util.HashSet;
import java.util.Set;

/**
 * This class is used by Automaton objects to create new states on A user can
 * implement its own version of StateFactory by providing an implementation for
 * createState
 *
 * @author Arnaud.Bailly - bailly@lifl.fr
 */
public class DefaultStateFactory<L, Tr extends Transition<L>, T extends Builder<L, Tr, T>> implements StateFactory<L, Tr, T>, Cloneable {

    private int id = 0;

    private Automaton<L, Tr, T> automaton;
	
    public class DefaultState implements State {

        public final int i;

        boolean initial;

        boolean terminal;

        Automaton<L, Tr, T> a;

        private Object label = null;

        DefaultState(int i, boolean initial, boolean terminal) {
            this.i = i;
            this.a = automaton;
            this.initial = initial;
            this.terminal = terminal;
        }

        /*
         * (non-Javadoc)
         * 
         * @see rationals.State#setInitial(boolean)
         */
        public State setInitial(boolean initial) {
            this.initial = initial;
            if (initial)
                a.initials().add(this);
            else
                a.initials().remove(this);
            return this;
        }

        /*
         * (non-Javadoc)
         * 
         * @see rationals.State#setTerminal(boolean)
         */
        public State setTerminal(boolean terminal) {
            this.terminal = terminal;
            if (terminal)
                a.terminals().add(this);
            else
                a.terminals().remove(this);
            return this;
        }

        /*
         * (non-Javadoc)
         * 
         * @see rationals.State#isInitial()
         */
        public boolean isInitial() {
            return this.initial;
        }

        /*
         * (non-Javadoc)
         * 
         * @see rationals.State#isTerminal()
         */
        public boolean isTerminal() {
            return this.terminal;
        }

        @Override
        public String toString() {
            return label == null ? Integer.toString(i) : label.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof DefaultStateFactory.DefaultState)) return false;
            @SuppressWarnings("unchecked")
			DefaultState ds = (DefaultState) o;
            return (ds.i == i) && (a == ds.a);
        }

        @Override
        public int hashCode() {
            return i;
        }
    }
    
    DefaultStateFactory(Automaton<L, Tr, T> a) {
        this.automaton = a;
    }

    /**
     * Creates a new state which is initial and terminal or not, depending on the
     * value of parameters.
     *
     * @param initial  if true, this state will be initial; otherwise this state will be non initial.
     * @param terminal if true, this state will be terminal; otherwise this state will be non terminal.
     */
    public State create(boolean initial, boolean terminal) {
        return new DefaultState(id++, initial, terminal);
    }

    /*
     * (non-Javadoc)
     * 
     * @see rationals.StateFactory#stateSet()
     */
    public Set<State> stateSet() {
    	return new HashSet<State>();
    }

    /*
     * (non-Javadoc)
     * 
     * @see rationals.StateFactory#stateSet(java.util.Set)
     */
    @Override
    public Set<State> stateSet(Set<State> s) {
    	Set<State> result = new HashSet<>();
    	result.addAll(s);
        return s;
    }

    public DefaultStateFactory<L, Tr, T> clone() {
        try {
        	@SuppressWarnings("unchecked")
			DefaultStateFactory<L, Tr, T> cl = (DefaultStateFactory<L, Tr, T>) super.clone();
            cl.id = 0;
            return cl;
        } catch (CloneNotSupportedException ex) {
            throw new Error(ex);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see rationals.StateFactory#setAutomaton(rationals.Automaton)
     */
    public void setAutomaton(Automaton<L, Tr, T> automaton) {
        this.automaton = automaton;
    }

    @Override
    public State create(boolean initial, boolean terminal, Object label) {
        DefaultState defaultState = new DefaultState(id++, initial, terminal);
        defaultState.label = label;
        return defaultState;
    }
}
