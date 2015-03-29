/*
 * (C) Copyright 2001 Arnaud Bailly (arnaud.oqube@gmail.com),
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

import rationals.transformations.TransformationsToolBox;

import java.util.*;

/**
 * A class defining Automaton objects
 * 
 * This class defines the notion of automaton. Following notations are used to
 * describe this class.
 * <p>
 * An automaton is a 5-uple <em>A = (X , Q , I , T , D)</em> where
 * <ul>
 * <li><em>X</em> is a finite set of labels named alphabet ,
 * <li><em>Q</em> is a finite set of states,
 * <li><em>I</em>, included in <em>Q</em>, is the set of initial states,
 * <li><em>T</em>, included in <em>Q</em>, is the set of terminal states
 * <li>and <em>D</em> is the set of transitions, which is included in
 * <em>Q times X times Q</em> (transitions are triples <em>(q , l , q')</em> where <em>q, q'</em>
 * are states and <em>l</em> a label).
 * </ul>
 * The empty word, usually denoted by <em>epsilon</em> will be denoted here by
 * the symbol <em>@</em>.
 * <p>
 * In this implementation of automaton, any object may be a label, states are
 * instance of class <tt>State</tt> and transitions are intances of class
 * <tt>Transition</tt>. Only automata should create instances of states through
 * <tt>Automaton</tt> method <tt>newState</tt>.
 * 
 * @author yroos@lifl.fr
 * @author bailly@lifl.fr
 * @version $Id: Automaton.java 10 2007-05-30 17:25:00Z oqube $
 * @see Transition State
 */
public class Automaton<L, Tr extends Transition<L>, T extends Builder<L, Tr, T>>
		implements Acceptor<L>, StateMachine<L>, Rational<L>, Cloneable {
	/* the identification of this automaton */
	private Object id;

	protected Builder<L, Tr, T> builder;

	/**
	 * @return Returns the id.
	 */
	public Object getId() {
		return id;
	}

	/**
	 * @param id
	 *            The id to set.
	 */
	public void setId(Object id) {
		this.id = id;
	}

	// The set of all objects which are labels of
	// transitions of this automaton.
	protected Set<L> alphabet;

	// The set of all states of this automaton.
	private Set<State> states;

	// the set of initial states
	private Set<State> initials;

	// the set of terminal states
	private Set<State> terminals;

	// Allows access to transitions of this automaton
	// starting from a given state and labelled by
	// a given object. The keys of this map are instances
	// of class Key and
	// values are sets of transitions.
	private Map<Key, Set<Transition<L>>> transitions;

	// Allows access to transitions of this automaton
	// arriving to a given state and labelled by
	// a given object. The keys of this map are instances
	// of class Key and
	// values are sets of transitions.
	private Map<Key, Set<Transition<L>>> reverse;

	// bonte
	private StateFactory stateFactory = new DefaultStateFactory(this);

    private StateLabels stateLabels = new StateLabels();
    
	/**
	 * @return
	 */
	public StateFactory getStateFactory() {
		return this.stateFactory;
	}

	/**
	 * @param factory
	 */
	public void setStateFactory(StateFactory factory) {
		this.stateFactory = factory;
		factory.setAutomaton(this);
	}

	/**
	 * Returns an automaton which recognizes the regular language associated
	 * with the regular expression <em>@</em>, where <em>@</em> denotes the
	 * empty word.
	 * 
	 * @return an automaton which recognizes <em>@</em>
	 */
	public static Automaton<?, ?, ?> epsilonAutomaton() {
		Automaton<?, ?, ?> v = new Automaton<>();
		v.addState(true, true);
		return v;
	}

	/**
	 * Returns an automaton which recognizes the regular language associated
	 * with the regular expression <em>l</em>, where <em>l</em> is a given
	 * label.
	 * 
	 * @param label
	 *            any object that will be used as a label.
	 * @return an automaton which recognizes <em>label</em>
	 */
	public static <L> Automaton<L, ?, ?> labelAutomaton(L label) {
		Automaton<L, ?, ?> v = new Automaton<>();
		State start = v.addState(true, false);
		State end = v.addState(false, true);
		try {
			v.addTransition(new Transition<L>(start, label, end));
		} catch (NoSuchStateException x) {
		}
		return v;
	}

	/**
	 * Returns an automaton which recognizes the regular language associated
	 * with the regular expression <em>u</em>, where <em>u</em> is a given word.
	 * 
	 * @param word
	 *            a List of Object interpreted as a word
	 * @return an automaton which recognizes <em>label</em>
	 */
	public static <L> Automaton<L, ?, ?> labelAutomaton(List<L> word) {
		Automaton<L, ?, ?> v = new Automaton<>();
		State start = null;
		if (word.isEmpty()) {
			v.addState(true, true);
			return v;
		} else
			start = v.addState(true, false);
		State end = null;
		try {
			for (Iterator<L> i = word.iterator(); i.hasNext();) {
				L o = i.next();
				end = v.addState(false, !i.hasNext());
				v.addTransition(new Transition<L>(start, o, end));
				start = end;
			}
		} catch (NoSuchStateException x) {
		}
		return v;
	}

	/**
	 * Creates a new empty automaton which contains no state and no transition.
	 * An empty automaton recognizes the empty language.
	 */
	public Automaton() {
		this(null);
	}

	/**
	 * Create a new empty automaton with given state factory.
	 * 
	 * @param sf
	 *            the StateFactory object to use for creating new states. May be
	 *            null.
	 */
	public Automaton(StateFactory sf) {
		this.stateFactory = sf == null ? new DefaultStateFactory(this) : sf;
		alphabet = new HashSet<>();
		states = stateFactory.stateSet();
		initials = stateFactory.stateSet();
		terminals = stateFactory.stateSet();
		transitions = new HashMap<>();
		reverse = new HashMap<>();
	}

	/**
	 * Returns a new instance of state which will be initial and terminal or not
	 * depending of parameters.
	 * 
	 * @param initial
	 *            if true, the new state will be initial; otherwise this state
	 *            will be non initial.
	 * @param terminal
	 *            if true, the new state will be terminal; otherwise this state
	 *            will be non terminal.
	 * @return a new state, associated with this automaton. This new state
	 *         should be used only with this automaton in order to create a new
	 *         transition for this automaton.
	 * @see Transition
	 */
	public State addState(boolean initial, boolean terminal) {

		State state = stateFactory.create(initial, terminal);
		if (initial)
			initials.add(state);
		if (terminal)
			terminals.add(state);
		states.add(state);
		return state;
	}

	/**
	 * Returns the alphabet <em>X</em> associated with this automaton.
	 * 
	 * @return the alphabet <em>X</em> associated with this automaton.
	 */
	@Override
	public Set<L> alphabet() {
		return alphabet;
	}

	/**
	 * Returns the set of states <em>Q</em> associated with this automaton.
	 * 
	 * @return the set of states <em>Q</em> associated with this automaton.
	 *         Objects which are contained in this set are instances of class
	 *         <tt>State</tt>.
	 * @see State
	 */
	public Set<State> states() {
		return states;
	}

	/**
	 * Returns the set of initial states <em>I</em> associated with this
	 * automaton.
	 * 
	 * @return the set of initial states <em>I</em> associated with this
	 *         automaton. Objects which are contained in this set are instances
	 *         of class <tt>State</tt>.
	 * @see State
	 */
	public Set<State> initials() {
		return initials;
	}

	/**
	 * Returns the set of terminal states <em>T</em> associated with this
	 * automaton.
	 * 
	 * @return set of terminal states <em>T</em> associated with this automaton.
	 *         Objects which are contained in this set are instances of class
	 *         <tt>State</tt>.
	 * @see State
	 */
	public Set<State> terminals() {
		return terminals;
	}

	// Computes and return the set of all accessible states, starting
	// from a given set of states and using transitions
	// contained in a given Map
	protected Set<State> access(Set<State> start, Map<Key, Set<Transition<L>>> map) {
		Set<State> current = start;
		Set<State> old;
		do {
			old = current;
			current = stateFactory.stateSet();
			Iterator<State> i = old.iterator();
			while (i.hasNext()) {
				State e = i.next();
				current.add(e);
				Iterator<L> j = alphabet.iterator();
				while (j.hasNext()) {
					Iterator<Transition<L>> k = find(map, e, j.next()).iterator();
					while (k.hasNext()) {
						current.add(k.next().end());
					}
				}
			}
		} while (current.size() != old.size());
		return current;
	}

	/**
	 * Returns the set of all accessible states in this automaton.
	 * 
	 * @return the set of all accessible states in this automaton. A state
	 *         <em>s</em> is accessible if there exists a path from an initial
	 *         state to <em>s</em>. Objects which are contained in this set are
	 *         instances of class <tt>State</tt>.
	 * @see State
	 */
	public Set<State> accessibleStates() {
		return access(initials, transitions);
	}

	/**
	 * Returns the set of states that can be accessed in this automaton starting
	 * from given set of states
	 * 
	 * @param states
	 *            a non null set of starting states
	 * @return a - possibly empty - set of accessible states
	 */
	public Set<State> accessibleStates(Set<State> states) {
		return access(states, transitions);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see rationals.Rational#accessibleStates(rationals.State)
	 */
	public Set<State> accessibleStates(State state) {
		Set<State> s = stateFactory.stateSet();
		s.add(state);
		return access(s, transitions);
	}

	/**
	 * Returns the set of co-accesible states for a given set of states, that is
	 * the set of states from this automaton from which there exists a path to a
	 * state in <code>states</code>.
	 * 
	 * @param states
	 *            a non null set of ending states
	 * @return a - possibly empty - set of coaccessible states
	 */
	public Set<State> coAccessibleStates(Set<State> states) {
		return access(states, reverse);
	}

	/**
	 * Returns the set of all co-accessible states in this automaton.
	 * 
	 * @return the set of all co-accessible states in this automaton. A state
	 *         <em>s</em> is co-accessible if there exists a path from this
	 *         state <em>s</em> to a terminal state. Objects which are contained
	 *         in this set are instances of class <tt>State</tt>.
	 * @see State
	 */
	public Set<State> coAccessibleStates() {
		return access(terminals, reverse);
	}

	/**
	 * Returns the set of all states which are co-accessible and accessible in
	 * this automaton.
	 * 
	 * @return the set of all states which are co-accessible and accessible in
	 *         this automaton. A state <em>s</em> is accessible if there exists
	 *         a path from an initial state to <em>s</em>. A state <em>s</em> is
	 *         co-accessible if there exists a path from this state <em>s</em>
	 *         to a terminal state. Objects which are contained in this set are
	 *         instances of class <tt>State</tt>.
	 * @see State
	 */
	public Set<State> accessibleAndCoAccessibleStates() {
		Set<State> ac = accessibleStates();
		ac.retainAll(coAccessibleStates());
		return ac;
	}

	// Computes and return the set of all transitions, starting
	// from a given state and labelled by a given label
	// contained in a given Map
	protected Set<Transition<L>> find(Map<Key, Set<Transition<L>>> m, State e, L l) {
		Key n = new Key(e, l);
		if (!m.containsKey(n))
			return new HashSet<Transition<L>>();
		return m.get(n);
	}

	// add a given transition in a given Map
	protected void add(Map<Key, Set<Transition<L>>> m, Transition<L> t) {
		Key n = new Key(t.start(), t.label());
		Set<Transition<L>> s;
		if (!m.containsKey(n)) {
			s = new HashSet<>();
			m.put(n, s);
		} else
			s = m.get(n);
		s.add(t);
	}

	/**
	 * Returns the set of all transitions of this automaton
	 * 
	 * @return the set of all transitions of this automaton Objects which are
	 *         contained in this set are instances of class <tt>Transition</tt>.
	 * @see Transition
	 */
	public Set<Transition<L>> delta() {
		Set<Transition<L>> s = new HashSet<>();
		for (Set<Transition<L>> tr : transitions.values())
			s.addAll(tr);
		return s;
	}

	/**
	 * Returns the set of all transitions of this automaton starting from a
	 * given state and labelled b a given label.
	 * 
	 * @param state
	 *            a state of this automaton.
	 * @param label
	 *            a label used in this automaton.
	 * @return the set of all transitions of this automaton starting from state
	 *         <tt>state</tt> and labelled by <tt>label</tt>. Objects which are
	 *         contained in this set are instances of class <tt>Transition</tt>.
	 * @see Transition
	 */
	public Set<Transition<L>> delta(State state, L label) {
		return find(transitions, state, label);
	}

	/**
	 * Returns the set of all transitions from state <code>from</code> to state
	 * <code>to</code>.
	 * 
	 * @param from
	 *            starting state
	 * @param to
	 *            ending state
	 * @return a Set of Transition objects
	 */
	@Override
	public Set<Transition<L>> deltaFrom(State from, State to) {
		Set<Transition<L>> t = delta(from);
		for (Iterator<Transition<L>> i = t.iterator(); i.hasNext();) {
			Transition<L> tr = i.next();
			if (!to.equals(tr.end()))
				i.remove();
		}
		return t;
	}

	/**
	 * Return all transitions from a State
	 * 
	 * @param state
	 *            start state
	 * @return a new Set of transitions (maybe empty)
	 */
	public Set<Transition<L>> delta(State state) {
		Set<Transition<L>> s = new HashSet<>();
		for (L lt : alphabet)
			s.addAll(delta(state, lt));
		return s;
	}

	/**
	 * Returns all transitions from a given set of states.
	 * 
	 * @param s
	 *            a Set of State objects
	 * @return a Set of Transition objects
	 */
	public Set<Transition<L>> delta(Set<State> s) {
		Set<Transition<L>> ds = new HashSet<>();
		for (State st : s)
			ds.addAll(delta(st));
		return ds;
	}

	/**
	 * Return a mapping from couples (q,q') of states to all (q,l,q')
	 * transitions from q to q'
	 * 
	 * @return a Map
	 */
	public Map<Couple, Set<Transition<L>>> couples() {
		// loop on transition map keys
		Iterator<Map.Entry<Key, Set<Transition<L>>>> it = transitions.entrySet().iterator();
		Map<Couple, Set<Transition<L>>> ret = new HashMap<>();
		while (it.hasNext()) {
			Map.Entry<Key, Set<Transition<L>>> e = it.next();
			// get start and end state
			State st = e.getKey().s;
			Iterator<Transition<L>> trans = e.getValue().iterator();
			while (trans.hasNext()) {
				Transition<L> tr = trans.next();
				State nd = tr.end();
				Couple cpl = new Couple(st, nd);
				Set<Transition<L>> s = ret.get(cpl);
				if (s == null)
					s = new HashSet<>();
				s.add(tr);
				ret.put(cpl, s);
			}
		}
		return ret;
	}

	/**
	 * Returns the set of all transitions of the reverse of this automaton
	 * 
	 * @return the set of all transitions of the reverse of this automaton. A
	 *         reverse of an automaton <em>A = (X , Q , I , T , D)</em> is the
	 *         automaton <em>A' = (X , Q , T , I , D')</em> where <em>D'</em> is the set <em>{ (q , l , q') | (q' , l , q) in D}</em>. Objects
	 *         which are contained in this set are instances of class
	 *         <tt>Transition</tt>.
	 * @see Transition
	 */
	@Override
	public Set<Transition<L>> deltaMinusOne(State state, L label) {
		return find(reverse, state, label);
	}

	/**
	 * Adds a new transition in this automaton if it is a new transition for
	 * this automaton. The parameter is considered as a new transition if there
	 * is no transition in this automaton which is equal to the parameter in the
	 * sense of method <tt>equals</tt> of class <tt>Transition</tt>.
	 * 
	 * @param transition
	 *            the transition to add.
	 * @throws NoSuchStateException
	 *             if <tt>transition</tt> is <tt>null</<tt>
	 * or if <tt>transition</tt> = <em>(q , l , q')</em> and <em>q</em> or <em>q'</em> does
	 *             not belong to <em>Q</em> the set of the states of this
	 *             automaton.
	 */
	public void addTransition(Transition<L> transition) throws NoSuchStateException {
		if (!states.contains(transition.start())
				|| !states.contains(transition.end()))
			throw new NoSuchStateException();
		if (!alphabet.contains(transition.label())) {
			alphabet.add(transition.label());
		}
		add(transitions, transition);
		add(reverse, new Transition<>(transition.end(), transition.label(), transition.start()));
	}

	/**
	 * the project method keeps from the Automaton only the transitions labelled
	 * with the letters contained in the set alph, effectively computing a
	 * projection on this alphabet.
	 * 
	 * @param alph
	 *            the alphabet to project on
	 */
	public void projectOn(Set<?> alph) {
		// remove unwanted transitions from ret
		Iterator<Map.Entry<Key, Set<Transition<L>>>> trans = transitions.entrySet().iterator();
		Set<Transition<L>> newtrans = new HashSet<>();
		while (trans.hasNext()) {
			Map.Entry<Key, Set<Transition<L>>> entry = trans.next();
			Key k = entry.getKey();
			Iterator<Transition<L>> tit = entry.getValue().iterator();
			while (tit.hasNext()) {
				Transition<?> tr = tit.next();
				if (!alph.contains(k.l)) {
					// create epsilon transition
					newtrans.add(new Transition<L>(k.s, null, tr.end()));
					// remove transition
					tit.remove();
				}
			}
		}
		// add newly created transitions
		if (!newtrans.isEmpty()) {
			for (Transition<L> tr : newtrans) {
				add(transitions, tr);
				add(reverse, new Transition<>(tr.end(), tr.label(), tr.start()));
			}
		}
		// remove alphabet
		alphabet.retainAll(alph);
	}

	/**
	 * returns a textual representation of this automaton.
	 * 
	 * @return a textual representation of this automaton based on the converter
	 *         <tt>toAscii</tt>.
	 * @see rationals.converters.toAscii
	 */
	public String toString() {
		return new rationals.converters.toAscii().toString(this);
	}

	/**
	 * returns a copy of this automaton.
	 * 
	 * @return a copy of this automaton with new instances of states and
	 *         transitions.
	 */
	@Override
	public Automaton<L, Tr, T> clone() {
		Automaton<L, Tr, T> b = new Automaton<L, Tr, T>();
		Map<State, State> map = new HashMap<>();
		for (State e : states)
			map.put(e, b.addState(e.isInitial(), e.isTerminal()));
		for (Transition<L> t : delta()) {
			try {
				b.addTransition(new Transition<>(map.get(t.start()), t.label(), map.get(t.end())));
			} catch (NoSuchStateException x) {
			}
		}
		return b;
	}

    /**
     * 
     * @return the set of labels matching initial states.
     */
    public Set<Object> labelledInitials() {
        return stateLabels.labels(initials());
    }

    /**
     * 
     * @return the set of labels for terminal states.
     */
    public Set<Object> labelledTerminals() {
        return stateLabels.labels(terminals());
    }

    /**
     * 
     * @return labels for states of this automaton.
     */
    public Set<Object> labelledStates() {
        return stateLabels.labels(states);
    }

    private class Key {
		private State s;

		private L l;

		protected Key(State s, L l) {
			this.s = s;
			this.l = l;
		}

		public boolean equals(Object o) {
			if (o == null || !(o instanceof Automaton.Key))
				return false;
			Key t = (Key) o;
			boolean ret = (l == null ? t.l == null : l.equals(t.l))	&& (s == null ? t.s == null : s.equals(t.s));
			return ret;
		}

		public int hashCode() {
			int x, y;
			if (s == null)
				x = 0;
			else
				x = s.hashCode();
			if (l == null)
				y = 0;
			else
				y = l.hashCode();
			return y << 16 | x;
		}
	}

	/**
	 * Returns true if this automaton accepts given word -- ie. sequence of
	 * letters. Note that this method accepts words with letters not in this
	 * automaton's alphabet, effectively recognizing all words from any alphabet
	 * projected to this alphabet.
	 * <p>
	 * If you need standard recognition, use
	 * 
	 * @see{accept(java.util.List) .
	 * @param word
	 * @return
	 */
	public boolean prefixProjection(List<L> word) {
		Set<?> s = stepsProject(word);
		return !s.isEmpty();
	}

	/**
	 * Return the set of steps this automaton will be in after reading word.
	 * Note this method skips letters not in alphabet instead of rejecting them.
	 * 
	 * @param l
	 * @return
	 */
	public Set<State> stepsProject(List<L> word) {
		Set<State> s = initials();
		Iterator<L> it = word.iterator();
		while (it.hasNext()) {
			L o = it.next();
			if (!alphabet.contains(o))
				continue;
			s = step(s, o);
			if (s.isEmpty())
				return s;
		}
		return s;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see rationals.Acceptor#accept(java.util.List)
	 */
	@Override
	public boolean accept(List<L> word) {
		Set<State> s = TransformationsToolBox.epsilonClosure(steps(word), this);
		s.retainAll(terminals());
		return !s.isEmpty();
	}

	/**
	 * Return true if this automaton can accept the given word starting from
	 * given set. <em>Note</em> The ending state(s) need not be terminal for
	 * this method to return true.
	 * 
	 * @param state
	 *            a starting state
	 * @param word
	 *            a List of objects in this automaton's alphabet
	 * @return true if there exists a path labelled by word from s to at least
	 *         one other state in this automaton.
	 */
	public boolean accept(State state, List<L> word) {
		Set<State> s = stateFactory.stateSet();
		s.add(state);
		return !steps(s, word).isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see rationals.Acceptor#steps(java.util.List)
	 */
	@Override
	public Set<State> steps(List<L> word) {
		Set<State> s = TransformationsToolBox.epsilonClosure(initials(), this);
		return steps(s, word);
	}

	/**
	 * Return the set of states this automaton will be in after reading the word
	 * from start states s.
	 * 
	 * @param s
	 *            the set of starting states
	 * @param word
	 *            the word to read.
	 * @return the set of reached states.
	 */
	@Override
	public Set<State> steps(Set<State> s, List<L> word) {
		Iterator<L> it = word.iterator();
		while (it.hasNext()) {
			L o = it.next();
			s = step(s, o);
			if (s.isEmpty())
				return s;
		}
		return s;
	}

	/**
	 * Return the set of states this automaton will be in after reading the word
	 * from single start state s.
	 * 
	 * @param st
	 *            the starting state
	 * @param word
	 *            the word to read.
	 * @return the set of reached states.
	 */
	@Override
	public Set<State> steps(State st, List<L> word) {
		Set<State> s = stateFactory.stateSet();
		s.add(st);
		Iterator<L> it = word.iterator();
		while (it.hasNext()) {
			L o = it.next();
			s = step(s, o);
			if (s.isEmpty())
				return s;
		}
		return s;
	}

	/**
	 * Return the list of set of states this automaton will be in after reading
	 * word from start state. Is start state is null, assume reading from
	 * initials().
	 * 
	 * @param word
	 * @param start
	 */
	@Override
	public List<Set<State>> traceStates(List<L> word, State start) {
		List<Set<State>> ret = new ArrayList<Set<State>>();
		Set<State> s = null;
		if (start != null) {
			s = stateFactory.stateSet();
			s.add(start);
		} else {
			s = initials();
		}
		Iterator<L> it = word.iterator();
		while (it.hasNext()) {
			L o = it.next();
			if (!alphabet.contains(o))
				continue;
			s = step(s, o);
			ret.add(s);
			if (s.isEmpty())
				return null;
		}
		return ret;
	}

	/**
	 * Returns the size of the longest word recognized by this automaton where
	 * letters not belonging to its alphabet are ignored.
	 * 
	 * 
	 * @param word
	 * @return
	 */
	public int longestPrefixWithProjection(List<L> word) {
		int lret = 0;
		Set<State> s = initials();
		Iterator<L> it = word.iterator();
		while (it.hasNext()) {
			L o = it.next();
			if ((o == null) || !alphabet.contains(o)) {
				lret++;
				continue;
			}
			s = step(s, o);
			if (s.isEmpty())
				break;
			lret++;
		}
		return lret;
	}

	/**
	 * Return the set of states accessible in one transition from given set of
	 * states s and letter o.
	 * 
	 * @param s
	 * @param o
	 * @return
	 */
	public Set<State> step(Set<State> s, L o) {
		Set<State> ns = stateFactory.stateSet();
		Set<State> ec = TransformationsToolBox.epsilonClosure(s, this);
		Iterator<State> it = ec.iterator();
		while (it.hasNext()) {
			State st = it.next();
			Iterator<?> it2 = delta(st).iterator();
			while (it2.hasNext()) {
				Transition<?> tr = (Transition<?>) it2.next();
				if (tr.label() != null && tr.label().equals(o))
					ns.add(tr.end());
			}
		}
		return ns;
	}

	/**
	 * @param tr
	 * @param msg
	 */
	public void updateTransitionWith(Transition<L> tr, L msg) {
		L lbl = tr.label();
		alphabet.remove(lbl);
		alphabet.add(msg);
		/* update transition map */
		Key k = new Key(tr.start(), lbl);
		Set<Transition<L>> s = transitions.remove(k);
		if (s != null)
			transitions.put(new Key(tr.start(), msg), s);
		/* update reverse map */
		k = new Key(tr.end(), lbl);
		s = reverse.remove(k);
		if (s != null)
			reverse.put(new Key(tr.end(), msg), s);
		tr.setLabel(msg);
	}

	/**
	 * @param st
	 * @return
	 */
	@Override
	public Set<Transition<L>> deltaMinusOne(State st) {
		Set<Transition<L>> s = new HashSet<>();
		Iterator<L> alphit = alphabet().iterator();
		while (alphit.hasNext()) {
			s.addAll(deltaMinusOne(st, alphit.next()));
		}
		return s;
	}

	/**
	 * Enumerate all prefix of words of length lower or equal than i in this
	 * automaton. This method takes exponential time and space to execute: <em>
   * use with care !</em>
	 * .
	 * 
	 * @param i
	 *            maximal length of words.
	 * @return a Set of List of Object
	 */
	public Set<List<L>> enumerate(int ln) {
		Set<List<L>> ret = new HashSet<>();
		class EnumState {
			/**
			 * @param s
			 * @param list
			 */
			public EnumState(State s, List<L> list) {
				st = s;
				word = new ArrayList<L>(list);
			}

			State st;

			List<L> word;
		}
		;
		LinkedList<EnumState> ll = new LinkedList<EnumState>();
		List<L> cur = new ArrayList<>();
		for (Iterator<State> i = initials.iterator(); i.hasNext();) {
			State s = i.next();
			if (s.isTerminal())
				ret.add(new ArrayList<L>());
			ll.add(new EnumState(s, cur));
		}

		do {
			EnumState st = ll.removeFirst();
			Set<Transition<L>> trs = delta(st.st);
			List<L> word = st.word;
			for (Iterator<Transition<L>> k = trs.iterator(); k.hasNext();) {
				Transition<L> tr = k.next();
				word.add(tr.label());
				if (word.size() <= ln) {
					EnumState en = new EnumState(tr.end(), word);
					ll.add(en);
					ret.add(en.word);
				}
				word.remove(word.size() - 1);
			}
		} while (!ll.isEmpty());
		return ret;
	}

	/**
	 * Create a new state with given label. The state is created with as neither
	 * initial nor terminal.
	 * 
	 * @param label
	 *            the state's label. May not be null.
	 * @return the newly created state.
	 */
	public State state(L label) {
		State s = stateLabels.state(label);
		if (s == null) {
			s = stateFactory.create(false, false, label);
			states.add(s);
			stateLabels.bind(s, label);
		}
		return s;
	}

	/**
	 * Starts creation of a new transition from the given state. Note that the
	 * state is created with given label if it does not exists.
	 * 
	 * @param o
	 *            the label of state to create transition from. may not be null.
	 * @return a TransitionBuilder that can be used to create a new transition.
	 */
	public T from(L o) {
		return builder.build(state(o), this);
	}

	public void setBuilder(Builder<L, Tr, T> t) {
		this.builder = t;
		this.builder.setAutomaton(this);
	}

	public void build(State from, L l, State to)
			throws NoSuchStateException {
		addTransition(this.builder.build(from, l, to));
	}
}
