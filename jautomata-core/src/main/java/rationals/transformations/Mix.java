/*
 * (C) Copyright 2002 Arnaud Bailly (arnaud.oqube@gmail.com),
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

import rationals.*;

import java.util.*;

/**
 * This class implements the mix - ie: synchronization product - operator
 * between two automatas.
 * <ul>
 * <li>C = A mix B</li>
 * <li>S(C) = { (a,b) | a in S(A) and b in S(B) }</li>
 * <li>S0(C) = (S0(A),SO(B))</li>
 * <li>T(C) = { (a,b) | a in T(A) and b in T(B) }</li>
 * <li>D(C) = { ((s1a,s1b),a,(s2a,s2b)) | exists (s1a,a,s2a) in D(A) and exists
 * (s1b,a,s2b) in D(b) } U { ((s1a,s1b),a,(s1a,s2b)) | a not in S(A) and exists
 * (s1b,a,s2b) in D(b) } U { ((s1a,s1b),a,(s2a,s1b)) | a not in S(B) and exists
 * (s1a,a,s2a) in D(a) }</li>
 * </ul>
 * 
 * @author Arnaud Bailly
 * @version 22032002
 */
public class Mix<L, Tr extends Transition<L>, T extends Builder<L, Tr, T>> implements BinaryTransformation<L, Tr, T> {

	private Synchronization<L> synchronization;

	/**
	 * Compute mix of two automata using default synchronization scheme which is
	 * the equality of labels.
	 * 
	 * @see rationals.DefaultSynchronization
	 * @see rationals.Synchronization
	 */
	public Mix() {
		this.synchronization = new DefaultSynchronization();
	}

	/**
	 * Compute mix of two automata using given synchronization scheme.
	 * 
	 * @param synch
	 *            a Synchronization object. Must not be null.
	 */
	public Mix(Synchronization synch) {
		this.synchronization = synch;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * rationals.transformations.BinaryTransformation#transform(rationals.Automaton
	 * , rationals.Automaton)
	 */
	public Automaton<L, Tr, T> transform(Automaton<L, Tr, T> a, Automaton<L, Tr, T> b) {
		Automaton<L, Tr, T> ret = new Automaton();
		// FIXME: Potentially unsafe cast, but adding generics reveals an inconsistency in the APIs 
		ret.setBuilder((T) new TransitionBuilder<L>());
		return transformTo(a, b, ret);
	}

	/**
	 * Compute transformation and stores the result in the given automaton.
	 * 
	 * @param a
	 * @param b
	 * @param ret
	 * @return the automaton {@code ret} containing the result of the
	 *         transformation.
	 */
	public Automaton<L, Tr, T> transformTo(Automaton<L, Tr, T> a, Automaton<L, Tr, T> b, Automaton<L, Tr, T> ret) {
		Set alph = synchronization.synchronizable(a.alphabet(), b.alphabet());
		/* check alphabets */
		Map<StatesCouple, State> amap = new HashMap();
		Map<StatesCouple, State> bmap = new HashMap();
		List<StatesCouple> todo = new ArrayList();
		Set<StatesCouple> done = new HashSet();
		Set<State> as = TransformationsToolBox.epsilonClosure(a.initials(), a);
		Set<State> bs = TransformationsToolBox.epsilonClosure(b.initials(), b);
		State from = ret.addState(true, TransformationsToolBox
				.containsATerminalState(as)
				&& TransformationsToolBox.containsATerminalState(bs));
		StatesCouple sc = new StatesCouple(as, bs);
		amap.put(sc, from);
		todo.add(sc);
		do {
			StatesCouple couple = (StatesCouple) todo.remove(0);
			from = (State) amap.get(couple);
			if (done.contains(couple))
				continue;
			done.add(couple);
			/* get transition sets */
			Map<L, Set<State>> tam = TransformationsToolBox.mapAlphabet(a.delta(couple.sa), a);
			Map<L, Set<State>> tbm = TransformationsToolBox.mapAlphabet(b.delta(couple.sb), b);
			/* create label map for synchronized trans */
			Map<L, StatesCouple> tcm = new HashMap();
			/* unsynchronizable transitions in A */
			for (Iterator<Map.Entry<L, Set<State>>> i = tam.entrySet().iterator(); i.hasNext();) {
				Map.Entry<L, Set<State>> me = i.next();
				L l = me.getKey();
				as = me.getValue();
				if (!alph.contains(l)) {
					Set<State> asc = TransformationsToolBox.epsilonClosure(as, a);
					tcm.put(l, sc = new StatesCouple(asc, couple.sb));
					State to = (State) amap.get(sc);
					makeNewState(ret, amap, sc, to);
					todo.add(sc);
					i.remove();
				}
			}
			/* unsynchronizable transition(s) in B */
			for (Iterator<Map.Entry<L, Set<State>>> i = tbm.entrySet().iterator(); i.hasNext();) {
				Map.Entry<L, Set<State>> me =  i.next();
				L l = me.getKey();
				bs = me.getValue();
				if (!alph.contains(l)) {
					Set<State> bsc = TransformationsToolBox.epsilonClosure(bs, b);
					tcm.put(l, sc = new StatesCouple(couple.sa, bsc));
					State to = amap.get(sc);
					makeNewState(ret, amap, sc, to);
					todo.add(sc);
					i.remove();
				}
			}
			/*
			 * there remains in tam and tbm only possibly synchronizing
			 * transitions
			 */
			for (Iterator<Map.Entry<L, Set<State>>> i = tam.entrySet().iterator(); i.hasNext();) {
				Map.Entry<L, Set<State>> me = i.next();
				L l = me.getKey();
				as = me.getValue();
				for (Iterator<Map.Entry<L, Set<State>>> j = tbm.entrySet().iterator(); j.hasNext();) {
					Map.Entry<L, Set<State>> mbe = j.next();
					L k = mbe.getKey();
					bs = mbe.getValue();
					L sy = synchronization.synchronize(l, k);
					if (sy != null) {
						Set<State> asc = TransformationsToolBox.epsilonClosure(as, a);
						Set<State> bsc = TransformationsToolBox.epsilonClosure(bs, b);
						tcm.put(sy, sc = new StatesCouple(asc, bsc));
						State to = amap.get(sc);
						makeNewState(ret, amap, sc, to);
						todo.add(sc);
					}
				}
			}
			/*
			 * 
			 * create new transitions in return automaton, update maps
			 */
			for (Iterator<Map.Entry<L, StatesCouple>> i = tcm.entrySet().iterator(); i.hasNext();) {
				Map.Entry<L, StatesCouple> me =  i.next();
				L l = me.getKey();
				sc = me.getValue();
				State to = amap.get(sc);
				makeNewState(ret, amap, sc, to);
				try {
					ret.build(from, l, to);
				} catch (NoSuchStateException e) {
				}
				// ret.from(from).on(l).go(to);
			}
		} while (!todo.isEmpty());
		return ret;
	}

	private void makeNewState(Automaton ret, Map amap, StatesCouple sc, State to) {
		if (to == null) {
			to = ret.addState(false, TransformationsToolBox
					.containsATerminalState(sc.sa)
					&& TransformationsToolBox.containsATerminalState(sc.sb));
			amap.put(sc, to);
		}
	}

}