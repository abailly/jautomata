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
package rationals.converters;

import rationals.*;
import rationals.properties.isNormalized;
import rationals.transformations.Normalizer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * This Converter takes an Automaton and generates an equivalent Regular
 * expression.
 * <p>
 * This conversion applies classical conversion algorithm based on state
 * removal. 
 * cf. Algorithm BMC (Brozowski et  al.) from J.sakarovitch "El&eacute;ments de th&eacute;orie
 * des automates", sec. 2
 * 
 * @author nono
 * @version $Id: ToRExpression.java 2 2006-08-24 14:41:48Z oqube $
 */
public class ToRExpression implements ToString {

    private Map /* < Key, String > */keys = new HashMap();

    /*
     * (non-Javadoc)
     * 
     * @see rationals.converters.ToString#toString(rationals.Automaton)
     */
    public String toString(Automaton a) {
        if(a == null)
            return "0";
        Automaton ret = (Automaton)a.clone();
        if (!new isNormalized().test(a))
            ret = new Normalizer().transform(a);
        /* special case for empty automaton */
        if (ret.initials().isEmpty())
            return "0";        
        /* add all transitions from start to end state */
        State init  = (State)ret.initials().iterator().next();
        State fini  = (State)ret.terminals().iterator().next();
        String re = "";
        for(Iterator i = ret.deltaFrom(init,fini).iterator();i.hasNext();) {
            Transition tr = (Transition)i.next();
            if("".equals(re)) {
                re = (tr.label() == null) ? "1" : tr.label().toString();
            }else
                re += "+" + ((tr.label() == null) ? "1" : tr.label().toString());
        }
        if(!"".equals(re))
            keys.put(new Couple(init,fini), re);
        Iterator it = ret.states().iterator();
        while (it.hasNext()) {
            State st = (State) it.next();
            if (st.isInitial() || st.isTerminal())
                continue;
          
            re = "";
            /* first handle self transitions */
            Iterator it2 = ret.delta(st).iterator();
            while (it2.hasNext()) {
                Transition t1 = (Transition) it2.next();
                if (!t1.end().equals(st))
                    continue;
                re += "+" + t1.label();
            }
            /* clean first '+' */
            if (!"".equals(re)) {
                re = re.substring(1);
                if (re.length() > 1)
                    re = "(" + re + ")*";
                else
                    re = re + "*";
            }
            Set to = ret.delta(st); /* outgoing */
            Set from = ret.deltaMinusOne(st); /* incoming */
            it2 = from.iterator();
            while (it2.hasNext()) {
                /* beware : this is reverse transition */
                Transition t1 = (Transition) it2.next();
                if (t1.end().equals(st)) /* skip self transitions */
                    continue;
                Iterator it3 = to.iterator();
                while (it3.hasNext()) {
                    Transition t2 = (Transition) it3.next();
                    if (t2.end().equals(st))
                        continue;
                    /* find completed expression from start to end */
                    State s2 = t2.end();
                    State s1 = t1.end();
                    Couple k = new Couple(s1, s2);
                    String oldre = (String) keys.get(k);
                    String nre = t1.label() + "" + re + t2.label();
                    if (oldre == null) {
                        oldre = nre;
                    } else {
                        oldre += "+" + nre;
                    }
                    try {
                        keys.put(k, oldre);
                        ret.addTransition(new Transition(s1, oldre, s2));
                    } catch (NoSuchStateException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
        /* returns the transition from stat to end */
        re = (String) keys.get(new Couple(init,fini));
        return re;
    }

}

/*
 * $Log: ToRExpression.java,v $ Revision 1.3 2005/03/23 07:22:42 bailly created
 * transductions package corrected EpsilonRemover added some tests removed
 * DirectedGRaph Interface from Automaton
 * 
 * Revision 1.2 2005/02/02 14:21:10 bailly corrected bad import
 * 
 * Revision 1.1 2004/09/21 11:50:28 bailly added interface BinaryTest added
 * class for testing automaton equivalence (isomorphism of normalized automata)
 * added computation of RE from Automaton
 *  
 */
