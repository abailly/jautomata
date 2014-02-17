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
package rationals.transductions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rationals.Automaton;
import rationals.NoSuchStateException;
import rationals.State;
import rationals.Transition;

/**
 * A deterministic transducer implementation.
 * 
 * @author nono
 * @version $Id: DeterministicTransducer.java 8 2007-05-29 19:52:29Z oqube $
 */
public class DeterministicTransducer extends Transducer {

    /**
     * There may be no more than one starting state in a deterministic
     * transducer. <em>Note</em>: this is not really useful as
     * determinization can be later applied to this automaton, but it helps.
     * 
     * @see rationals.Automaton#addState(boolean, boolean)
     */
    public State addState(boolean initial, boolean terminal) {
        if (initial && initials().size() > 0)
            throw new IllegalArgumentException(
                    "Cannot have more than one start state in a deterministic transducer");
        return super.addState(initial, terminal);
    }

    /**
     * This method ensures that no nondeterminism is introduced in the
     * transducer. For each added transition, it checks that there is no other
     * transition with same input label starting from the given start state and
     * that the input label is not null.
     * 
     * @see rationals.Automaton#addTransition(rationals.Transition)
     */
    public void addTransition(Transition transition)
            throws NoSuchStateException {
        TransducerRelation rel = (TransducerRelation)transition.label();
        if (rel.getIn() == null)
            throw new IllegalArgumentException(
                    "Cannot have epsilon input transition in a deterministic transducer");
        Set s = delta(transition.start());
        for (Iterator i = s.iterator(); i.hasNext();) {
            Transition tr = (Transition) i.next();
            TransducerRelation r2 = (TransducerRelation)tr.label();
            if (rel.getIn().equals(r2.getIn()))
                throw new IllegalArgumentException(
                        "Cannot duplicate input transition in a deterministic transducer");
        }
        super.addTransition(transition);
    }

    /*
     * (non-Javadoc)
     * 
     * @see rationals.transductions.Transduction#image(rationals.Automaton)
     */
    public Automaton image(Automaton a) {
        // TODO Auto-generated method stub
        return super.image(a);
    }

    /*
     * (non-Javadoc)
     * 
     * @see rationals.transductions.Transduction#image(java.lang.Object[])
     */
    public Automaton image(Object[] word) {
        /* set of transitions fired */
        Set s = initials();
        List /* < Set < Transition > > */trs = new ArrayList(); /*
                                                                 * list of set
                                                                 * of
                                                                 * transitions
                                                                 */
        /*
         * first compute a the list of set of transition that the input word
         * crosses
         */
        int ln = word.length;
        for (int i = 0; i < ln; i++) {
            Set tf = new HashSet();
            s = step(s, word[i], tf);
            if (s.isEmpty())
                return new Automaton(); /* language is empty */
            trs.add(tf);
        }
        s.retainAll(terminals());
        if (s.isEmpty())
            return new Automaton();
        Automaton a = new Automaton();
        State cur = a.addState(true, false);
        Iterator it = trs.iterator();
        while (it.hasNext()) {
            /* always ONE transition */
            Transition tr = (Transition) ((Set) it.next()).iterator().next();
            TransducerRelation rel = (TransducerRelation)tr.label();
            State ns = a.addState(false, it.hasNext() ? false : true);
            try {
                a.addTransition(new Transition(cur, rel.getOut(), ns));
            } catch (NoSuchStateException e) {
            }
            cur = ns;
        }
        return a;
    }

    /**
     * Returns the set of UIO sequences for each state in this transducer (if
     * they exist). A set <code>{UIO1,UIO2,... , UIOn} </code>, for
     * <code>n</code> the number of states in this automaton is a sequence of
     * transitions such that for any two states <code>qi,qj</code> in the
     * transducer, there exists <code>UIOi != UIOj</code>. It allows
     * distinction of any two states by both input and output sequences.
     * <p>
     * This method returns a Map from State of this transducer to UIO sequences,
     * that is List of {@see rationals.transductions.TransducerRelation}objects. It
     * repeteadly breaks a partition of states using transitions and is an
     * adaptation of the original UIO algorithm found in
     * <p>"<em>A new technique for generating protocol tests</em>", Sabnani
     * and Dahbura, 1985
     * <p>
     * This algorithm assumer that this transducer is minimal.
     * 
     * @return a Map from State to List of TransducerRelation
     */
    public Map makeUIOSet() throws TransductionException {
        /* return map */
        Map ret = new HashMap();
        /* map from words to set of states that can read this word */
        Map /* < Object , Set < State > > */am = new HashMap();
        /* set of states not assigned an UIO */
        /* initial mapping from singletons to states */
        for (Iterator it = delta().iterator(); it.hasNext();) {
            Transition tr = (Transition) it.next();
            List l = new ArrayList();
            l.add(tr.label());
            Set s = (Set) am.get(l);
            if (s == null) {
                s = getStateFactory().stateSet();
                am.put(l, s);
            }
            s.add(tr.start());
        }
        do {
            Set rm = null;
            /* remove singleton sets and clear remaining sets */
            do {
                /* clean set of states */
                if (rm != null && !rm.isEmpty()) {
                    for (Iterator i = am.values().iterator(); i.hasNext();) {
                        Set s = (Set) i.next();
                        s.removeAll(rm);
                    }
                }
                rm = getStateFactory().stateSet();
                for (Iterator i = am.entrySet().iterator(); i.hasNext();) {
                    Map.Entry e = (Map.Entry) i.next();
                    Set s = (Set) e.getValue();
                    if (s.size() == 1) {
                        State st = (State) s.iterator().next();
                        ret.put(st, e.getKey());
                        i.remove();
                        rm.add(st);
                    }
                }
            } while (!rm.isEmpty());

            /* duplicate */
            Map nam = new HashMap(am);
            /* handle multiple sets */
            for (Iterator i = nam.entrySet().iterator(); i.hasNext();) {
                Map.Entry e = (Map.Entry) i.next();
                List word = (List) e.getKey();
                Set states = (Set) e.getValue();
                /* partition states set */
                Set step = steps(states, word);
                Set dt = delta(step);
                for (Iterator i3 = dt.iterator(); i3.hasNext();) {
                    Transition tr = (Transition) i3.next();
                    List nw = new ArrayList(word);
                    Set ns = getStateFactory().stateSet();
                    nw.add(tr.label());
                    for (Iterator i2 = states.iterator(); i2.hasNext();) {
                        State st = (State) i2.next();
                        Set tos = steps(st, nw);
                        if (tos.size() == 0)
                            continue;
                        ns.add(st);
                    }
                    /* add word with list of states */
                    am.put(nw, ns);
                }
                i.remove();
                am.remove(word);
            }
        } while (!am.isEmpty());
        return ret;
    }

    /**
     * Compute - if it exists - a characterizing set for this transducer. A
     * characterizing set <code>W</code> is a set of input words
     * <code>{w1,w2, ..., wn}</code> such that for any two states
     * <code>qi,qj</code> in the transducer, there exists <code>wk</code> in
     * <code>W</code> such that <code>out(qi,wk) != out(qj,wk)</code>.
     * <p>
     * This algorithm is similar to the algorithm for computing UIO sequences.
     * It computes a sequence for each word
     * 
     * @return a Set of List of Object - input letters.
     */
    public Set makeCharacterizingSet() {
        /* return map */
        Set ret = new HashSet();
        /*
         * map from words to equivalence class of states for output with this
         * word
         */
        Map /* < Object , Set < State > > */am = new HashMap();
        /* set of states not assigned an w */
        Set /* < State > */sm = getStateFactory().stateSet();
        /* initial partition */
        List l = new ArrayList();
        am.put(l, getStateFactory().stateSet(states()));
        do {
            /* duplicate */
            Map nam = new HashMap(am);
            /* handle multiple sets */
            for (Iterator i = nam.entrySet().iterator(); i.hasNext();) {
                Map.Entry e = (Map.Entry) i.next();
                List word = (List) e.getKey();
                Set states = (Set) e.getValue();
                states.removeAll(sm);
                /* local map storing output word equivalent states */
                Map /* < List < Object > , Set < State > > */equiv = new HashMap();
                /* local map storing output to input */
                Map /* < List < Object > , List < Object > > */wwm = new HashMap();
                /* partition states set */
                for (Iterator i3 = inputAlphabet().iterator(); i3.hasNext();) {
                    /* append letter */
                    List nw = new ArrayList(word);
                    nw.add(i3.next());
                    for (Iterator i2 = states.iterator(); i2.hasNext();) {
                        State st = (State) i2.next();
                        List out = Arrays.asList(image(st, nw.toArray()));
                        if (out == null)
                            continue;
                        Set ns = (Set) equiv.get(out);
                        if (ns == null) {
                            ns = getStateFactory().stateSet();
                            equiv.put(out, ns);
                            wwm.put(out, nw);
                        }
                        ns.add(st);
                    }
                    /* remove states in singleton equiv set */
                    for (Iterator i2 = equiv.entrySet().iterator(); i2.hasNext();) {
                        Map.Entry me = (Map.Entry) i2.next();
                        List out = (List) me.getKey();
                        Set s = (Set) me.getValue();
                        if (s.size() == 1) {
                            /* nw is a new characteristic word */
                            ret.add(wwm.remove(out));
                            /* remove state from states */
                            states.removeAll(s);
                            sm.addAll(s);
                        } else {
                            am.put(wwm.remove(out), s);                            
                        }
                        i2.remove();
                    }
                }
                am.remove(word);
                if (am.isEmpty())
                    break;
            }
        } while (!am.isEmpty());
        return ret;
    }

    /**
     * Compute output word from given state and given input word. The output is
     * a list of objects without <code>null</code>.
     * 
     * @param st
     *            the starting state
     * @param word
     *            the word to read.
     * @return a List of output letters. May be null if input word cannot be
     *         read from this state.
     */
    public Object[] image(State st, Object[] word) {
        Set s = new HashSet();
        s.add(st);
        List /* < Set < Transition > > */trs = new ArrayList(); /*
                                                                 * list of set
                                                                 * of
                                                                 * transitions
                                                                 */
        /*
         * first compute a the list of set of transition that the input word
         * crosses
         */
        for (int i =0;i<word.length;i++) {
            Set tf = new HashSet();
            s = step(s, word[i], tf);
            if (s.isEmpty())
                return null; /* not recognized */
            trs.add(tf);
        }
        List ret = new ArrayList();
        Iterator it = trs.iterator();
        while (it.hasNext()) {
            /* always ONE transition */
            Transition tr = (Transition) ((Set) it.next()).iterator().next();
            TransducerRelation rel = (TransducerRelation)tr.label();
            Object l = rel.getOut();
            if (l != null)
                ret.add(l);
        }
        return ret.toArray();
    }

    /**
     * Check that a given set of input sequences is a characterizing set
     * for this transducer.
     * 
     * @param s a Set of List of Object input letters.
     * @return true if <code>s</code> is a characterizing set for this, false otherwise.
     */
    public boolean verifyW(Set s) {
        /* construct map from state to set of output words */
        State[] sts  = (State[])states().toArray(new State[states().size()]);
        Set[] words = new Set[sts.length];
        final int len = sts.length;
        for (Iterator i = s.iterator(); i.hasNext();) {
            List in = (List) i.next();
            for (int j =0;j<len;j++) {
                State st = sts[j];
                Object[] out = image(st, in.toArray());
                if(out == null)
                    continue;
                Set ss = words[j];
                if (ss == null) {
                    ss = words[j] = new HashSet();
                }
                ss.add(out);
            }
        }
        /* ensure that no two states hava same set of output words */
        for(int i=0;i<len;i++) 
            for(int j = i+1;j<len;j++) {
                if(words[i].equals(words[j]))
                    return false;
            }
        return true;
    }

    /**
     * @param m
     */
    public boolean verifyUIO(Map m) {
        if(m.size() != states().size())
            return false;
        /* check is sequence is different from any other */
        List[] words = new List[m.size()];
        words = (List[]) m.values().toArray(words);
        final int len = words.length;
        for(int i=0;i<len;i++)
            for(int j=i+1;j<len;j++)
                if(words[i].equals(words[j]))
                    return false;
        return true;
    }

    /* (non-Javadoc)
     * @see rationals.transductions.RationalFunction#imageWord(java.util.List)
     */
    public Object[] imageWord(List word) {
        return image((State)initials().iterator().next(),word.toArray());
    }

    /* (non-Javadoc)
     * @see rationals.transductions.RationalFunction#imageWord(java.lang.Object[])
     */
    public Object[] imageWord(Object[] word) {
        return image((State)initials().iterator().next(),word);
    }
}
