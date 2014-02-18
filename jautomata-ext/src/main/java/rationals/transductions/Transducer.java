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
package rationals.transductions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import rationals.Automaton;
import rationals.NoSuchStateException;
import rationals.State;
import rationals.Transition;
import rationals.transformations.Reducer;

/**
 * An implementation of rational transductions using transducers. This
 * implementation expects each transition's label to be an instance of the class
 * {@link rationals.transductions.TransducerRelation} that contains input/output letters -
 * not words - and epsilon transitions. According to (Berstel, 1979), any
 * rational transduction can be encoded in such a transducer.
 * 
 * @author nono
 * @version $Id: Transducer.java 2 2006-08-24 14:41:48Z oqube $
 */
public class Transducer extends Automaton implements Transduction {

    /* input and output automata isomorphic ot this transducer */
    private Automaton input = new Automaton();

    private Automaton output = new Automaton();

    /* maps from states */
    /* map states of input automaton to this transducer states */
    private Map ins = new HashMap();

    /* map states of output automaton to this transudcer states */
    private Map outs = new HashMap();

    /* inverse maps */
    private Map sin = new HashMap();

    private Map sout = new HashMap();

    /*
     * (non-Javadoc)
     * 
     * @see rationals.transductions.Transduction#image(rationals.Automaton)
     */
    public Automaton image(Automaton a) {
    	/*
    	 * construct morphisms, inverse morphisms and rational 
    	 * languages that are equivalent to transduciton
    	 */  
    	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see rationals.Rational#newState(boolean, boolean)
     */
    public State addState(boolean initial, boolean terminal) {
        State s = super.addState(initial, terminal);
        State is = input.addState(initial, terminal);
        State os = output.addState(initial, terminal);
        ins.put(is, s);
        outs.put(os, s);
        sin.put(s, is);
        sout.put(s, os);
        return s;
    }

    /**
     * The label of a transition in a transducer is a couple of letters,
     * possibliy null.
     * 
     * @throws ClassCastException if transition is not a TransducerTransition
     * @see rationals.transductions.TransducerRelation
     * @see rationals.Rational#addTransition(rationals.Transition)
     */
    public void addTransition(Transition transition)
            throws NoSuchStateException {
        // check transition's object is a couple of lists
        TransducerRelation rel = (TransducerRelation) transition.label();
        /* update input and output automata */
        input.addTransition(new Transition((State) sin.get(transition.start()),
                rel.getIn(), (State) sin.get(transition.end())));
        output.addTransition(new Transition((State) sout
                .get(transition.start()), rel.getOut(), (State) sout
                .get(transition.end())));
        super.addTransition(transition);
    }

    /*
     * (non-Javadoc)
     * 
     * @see rationals.Transduction#image(java.util.List)
     */
    public Automaton image(List word) {
        return image(word.toArray());
    }

    /*
     * (non-Javadoc)
     * 
     * @see rationals.Transduction#image(java.util.List)
     */
    public Automaton image(Object[] word) {
        /* set of transitions fired */
        Set s = initials();
        List /* < Set < Transition > > */trs = new ArrayList(); /*
                                                                 * list of set
                                                                 * of
                                                                 * transitions
                                                                 */
        List /* < Set < State > > */sts = new ArrayList(); /*
                                                            * list of set of
                                                            * states - does not
                                                            * contain initials
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
            sts.add(s);
        }
        s.retainAll(terminals());
        Automaton ret = makeOutputAutomata(trs, sts);
        return new Reducer().transform(ret);
    }

    
    /**
     * Create the FSA representing the output language according to lists of
     * transitions and list of states reached while reading the word to
     * transduce.
     * 
     * @param trs
     *            a List of Set of transitions reached in one letter
     * @param sts
     *            a List of set of states reached in one step.
     * @return an Automaton object on the output alphabet
     */
    private Automaton makeOutputAutomata(List trs, List sts) {
        Set s;
        /*
         * construct the output automaton from tf This is the concatenation of
         * all the sub automata induced by the list of set of transitions
         */
        Automaton ret = new Automaton();
        Map sm = new HashMap(); /* nap old states to new states */
        s = initials();
        for (Iterator i = s.iterator(); i.hasNext();) {
            Set es = new HashSet();
            es.add(ret.addState(true, false));
            sm.put(i.next(), es);
        }
        Iterator tit = trs.iterator();
        Iterator sit = sts.iterator();
        while (tit.hasNext()) {
            /*
             * set of states and transitions resp. attained and crossed in one
             * step
             */
            Set arr = (Set) sit.next();
            Set trr = (Set) tit.next();
            Map om = new HashMap();
            Map /* < State, Set < State > > */ em = new HashMap(); 
            /* construct a new automaton */
            /* start states */
            for (Iterator it2 = s.iterator(); it2.hasNext();) {
                State state = (State) it2.next();
                State sst = ret.addState(false, false);
                om.put(state, sst);
                try {
                    /*
                     * connect start states in this step to end states in last
                     * step
                     */
                    Iterator it3 = ((Set)sm.get(state)).iterator();
                    while(it3.hasNext()) 
                        ret.addTransition(new Transition((State) it3.next(),
                            null, sst));
                } catch (NoSuchStateException e) {
                }
            }
            /* end states */
            for (Iterator it2 = arr.iterator(); it2.hasNext();) {
                State state = (State) it2.next();
                State sst = (State) om.get(state);
                if (sst == null)
                    om.put(state, sst = ret.addState(false, false));
                Set est = new HashSet();
                est.add(sst);
                em.put(state,est);                
            }
            /* transitions */
            for (Iterator it2 = trr.iterator(); it2.hasNext();) {
                Transition trans = (Transition) it2.next();
                TransducerRelation rel = (TransducerRelation)trans.label();
                State ast, bst;
                State start = trans.start();
                ast = (State) om.get(start);
                if (ast == null) {
                    ast = ret.addState(false, false);
                    om.put(trans.start(), ast);
                }
                /* handle differently null transitions from others */
                State end = trans.end();
                if(start == end && rel.getIn() != null) {
                    Set est = (Set)em.get(end);
                    bst = (State) om.get(end);
                    ret.addState(false, false);
                    om.put(trans.end(), bst);
                    est.add(bst);
                } else {
                    bst = (State) om.get(end);
                    if (bst == null) {
                        bst = ret.addState(false, false);
                        om.put(trans.end(), bst);
                    }
                }
                try {
                    ret.addTransition(new Transition(ast, rel.getOut(), bst));
                } catch (NoSuchStateException e1) {
                    e1.printStackTrace();
                }
            }
            /* connect sm to om */
            sm = em;
            /* append to ret */
            s = arr;
        }
        /* complete automaton with a start and end state */
        State end = ret.addState(false, true);
        sit = s.iterator();
        while (sit.hasNext()) {
            State old = (State) sit.next();
            if (old.isTerminal())
                try {
                    Set niou = (Set) sm.get(old);
                    for (Iterator iter = niou.iterator(); iter.hasNext();) {
                        ret.addTransition(new Transition((State)iter.next(), null, end));
                    }
                } catch (NoSuchStateException e1) {
                }

        }
        return ret;
    }

    /*
     *  
     */
    protected Set step(Set s, Object o, Set trans) {
        Set arr = new HashSet();
        Iterator it = s.iterator();
        while (it.hasNext()) {
            State st = (State) it.next();
            Iterator it2 = delta(st).iterator();
            while (it2.hasNext()) {
                Transition tr = (Transition) it2.next();
                TransducerRelation rel = (TransducerRelation)tr.label();
                if (rel.getIn() != null && rel.getIn().equals(o)) {
                    arr.add(tr.end());
                    trans.add(tr);
                }
            }
        }
        return epsilonClosure(arr, trans);
    }

    /*
     * Return the set of states that can be <em> reached </em> from s using
     * epsilon transitions only and update trans set. The reached states does
     * not contains the starting states.
     */
    private Set epsilonClosure(Set s, Set trans) {
        Set exp = new HashSet(s); /* set of states to visit */
        Set view = new HashSet(); /* set of states visited */
        Set arr = new HashSet(); /* the set of arrival states */
        do {
            Set ns = new HashSet(exp); /* arrival states */
            Iterator it = ns.iterator();
            while (it.hasNext()) {
                State st = (State) it.next();
                exp.remove(st);
                Iterator it2 = delta(st).iterator();
                while (it2.hasNext()) {
                    Transition tr = (Transition) it2.next();
                    TransducerRelation rel = (TransducerRelation)tr.label();                    
                    if (rel.getIn() == null && !view.contains(tr.end())) {
                        /* compute closure of epsilon transitions */
                        trans.add(tr);
                        exp.add(tr.end());
                    } else {
                        arr.add(tr.start());
                    }
                }
                view.add(st);
            }
        } while (!exp.isEmpty());
        return arr;
    }

    /*
     * (non-Javadoc)
     * 
     * @see rationals.Transduction#inverse(java.util.List)
     */
    public Set inverse(List word) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @return the input alphabet of this transducer
     */
    public Set inputAlphabet() {
        return input.alphabet();
    }
    
    /**
     * 
     * @return the output alphabet of this transducer
     */
    public Set outputAlphabet() {
        return output.alphabet();
    }

    /**
     * This method creates a set of input letters that covers all the
     * transitions occuring in this transducer.
     * 
     * @return a Set of Object arrays
     */
    public Set makeTransitionCover() {
        Set tr = new HashSet();
        Stack /* < State > */todost = new Stack();
        Stack /* < List < Object > > */todoword = new Stack();
        Set /* < Set > */exp = new HashSet();
        todost.addAll(initials());
        int l = initials().size();
        for (int i = 0; i < l; i++)
            todoword.push(new ArrayList());
        do {
            /* build all sequences accessible from a state */
            State s = (State) todost.pop();
            List word = (List) todoword.pop();
            /* state already viewed */
            if (exp.contains(s)) {
                tr.add(word);
                continue;
            }
            exp.add(s);
            Iterator it = delta(s).iterator();
            boolean first = true;
            while (it.hasNext()) {
                Transition t = (Transition) it.next();
                TransducerRelation rel = (TransducerRelation)t.label();
                List nw;
                if (!first)
                    nw = new ArrayList(word);
                else
                    nw = word;
                nw.add(rel.getIn());
                /* push end state and new sequence */
                todost.push(t.end());
                todoword.push(nw);
                first = false;
            }
        } while (!todost.isEmpty());
        return tr;
    }
}