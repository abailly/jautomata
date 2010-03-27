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
 * Created on 30 mars 2005
 *
 */
package rationals.transductions.testing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import rationals.State;
import rationals.Automaton;
import rationals.Transition;
import rationals.transductions.DeterministicTransducer;
import rationals.transductions.TransducerRelation;
import rationals.transductions.Transducer;
import rationals.transductions.TransductionException;

/**
 * Testing FSM with UIO sequences, basic method. Test suite is generated for
 * each transition <code>(qi,i/o,qj)</code> by concatenating :
 * <ul>
 * <li>A reset input</li>
 * <li>A shortest sequence from <code>q0</code> to <code>qi</code></li>
 * <li>The input <code>i</code></li>
 * <li>The UIO for <code>qj</code></li>
 * </ul>
 * 
 * This method is proposed in
 * <p>
 * <em>"A new technique for generating protocol tests"</em>, Sabnani and
 * Dahbura, 1985
 * </p>
 * 
 * @author nono
 * @version $Id: UTestGenerator.java 2 2006-08-24 14:41:48Z oqube $
 */
public class UTestGenerator implements TransducerTestGenerator {

    /**
     *  
     */
    public UTestGenerator() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see rationals.transductions.testing.TransducerTester#testSequence(rationals.transductions.Transducer)
     */
    public Set testSuite(DeterministicTransducer t) throws TransductionException {
        Set ret = new HashSet();
        /* construct UIO set */
        Map m = t.makeUIOSet();
        /* transform into input sequences */
        Map im = new HashMap();
        for(Iterator i= m.entrySet().iterator();i.hasNext();) {
            Map.Entry me = (Map.Entry)i.next();
            List l = (List)me.getValue();
            List il = new ArrayList(l.size());
            for(Iterator i2=l.iterator();i2.hasNext();) {
                il.add(((TransducerRelation)i2.next()).getIn());
            }
            im.put(me.getKey(),il);
        }
        /* construct shortest path for each state - use Dijkstra algorithm */
        Map sp = new HashMap();
        makeShortestPathMap(t, sp);
        /* compute test set */
        for(Iterator i=t.delta().iterator();i.hasNext();) {
            Transition tr = (Transition)i.next();
            TransducerRelation rel = (TransducerRelation)tr.label();
            State from = tr.start();
            State to = tr.end();
            Object lbl = rel.getIn();
            List l = from.isInitial() ? new ArrayList() : (List)sp.get(from);
            l.add(lbl);
            l.addAll((List)im.get(to));
            ret.add(l.toArray());
        }
        return ret;
    }

    /**
     * @param t
     * @param sp
     * @throws TransductionException
     */
    private void makeShortestPathMap(Transducer t, Map sp) throws TransductionException {
        Set s = t.initials();
        if (s.size() != 1)
            throw new TransductionException(
                    "Transducer appears not deterministic or has no starting states");
        State init = (State) s.iterator().next();
	// create list of all states in bf order
        List vis = breadthFirstOrder(init,t);
        sp.put(init, new ArrayList());
        for (Iterator it = vis.iterator(); it.hasNext();) {
            State st = (State) it.next();
            if (st == init)
                continue;
            List word = null;
            /* find an already visited state that is a parent of this states */
            sti: for (Iterator iter = sp.keySet().iterator(); iter.hasNext();) {
                State par = (State) iter.next();
                Set trans = t.delta(par);
                for (Iterator iterator = trans.iterator(); iterator.hasNext();) {
                    Transition tr = (Transition) iterator.next();
                    TransducerRelation rel = (TransducerRelation)tr.label();
                    if (tr.end() == st) {
                        word = new ArrayList((List) sp.get(par));
                        word
                                .add(rel.getIn());
                        break sti;
                    }

                }
            }
            sp.put(st,word);
        }
    }

  private List /* < State > */ breadthFirstOrder(State start, Automaton a) {
    LinkedList todo = new LinkedList();
    Set done = new LinkedHashSet(a.states().size());
    todo.add(start);
    while(!todo.isEmpty()) {
      State st = (State)todo.removeFirst();
      if(done.contains(st))
	continue;
      done.add(st);
      /* add successors */
      for(Iterator i=a.delta(st).iterator();i.hasNext();) {
	Transition tr = (Transition)i.next();	
	if(tr.end() != st) 
	  todo.add(tr.end());
      }
    }
    return new ArrayList(done);
  }

}
