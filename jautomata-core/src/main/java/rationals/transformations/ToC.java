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
package rationals.transformations;

import rationals.Automaton;
import rationals.Builder;
import rationals.NoSuchStateException;
import rationals.State;
import rationals.Transition;

import java.util.*;

public class ToC<L, Tr extends Transition<L>, T extends Builder<L, Tr, T>> implements UnaryTransformation<L, Tr, T> {

    public Automaton<L, Tr, T> transform(Automaton<L, Tr, T> a) {
        Automaton b = new EpsilonTransitionRemover().transform(a);
        Set Ib = b.initials();
        Set Tb = b.terminals();
        Map /* < Set <State>, Set < State > > */ subsets = new HashMap();
        Map /* < Set < State > , Map < Object, Set < State > > > */delta = new HashMap();
        LinkedList l = new LinkedList();
        Set /* < Set < State > > */ done = new HashSet();
        l.add(Ib);
        while (!l.isEmpty()) {
            Set e1 = (Set) l.removeFirst();
            Set sub = b.getStateFactory().stateSet();
            subsets.put(e1, sub);
            Iterator j = done.iterator();
            while (j.hasNext()) {
                Set x = (Set) j.next();
                if (x.containsAll(e1) && !x.equals(e1))
                    ((Set) subsets.get(x)).addAll(e1);
                if (e1.containsAll(x) && !x.equals(e1))
                    sub.addAll(x);
            }
            done.add(e1);
            delta.put(e1, new HashMap());
            j = b.alphabet().iterator();
            while (j.hasNext()) {
                Object label = j.next();
                Iterator i = e1.iterator();
                Set e2 = b.getStateFactory().stateSet();
                while (i.hasNext()) {
                    Iterator k = b.delta((State) i.next(), label).iterator();
                    while (k.hasNext()) {
                        e2.add(((Transition) k.next()).end());
                    }
                }
                ((Map) delta.get(e1)).put(label, e2);
                if (!done.contains(e2))
                    l.add(e2);
            }
        }
        Automaton c = new Automaton();
        Map corr = new HashMap();
        Iterator i = done.iterator();
        while (i.hasNext()) {
            Set x = (Set) i.next();
            if (!x.isEmpty()) {
                if (!((Set) subsets.get(x)).containsAll(x)) {
                    boolean ini = Ib.containsAll(x);
                    boolean term = TransformationsToolBox
                            .containsATerminalState(x);
                    corr.put(x, c.addState(ini, term));
                }
            }
        }
        i = corr.keySet().iterator();
        while (i.hasNext()) {
            Set e = (Set) i.next();
            Iterator j = b.alphabet().iterator();
            while (j.hasNext()) {
                Object lab = j.next();
                Set f = (Set) ((Map) delta.get(e)).get(lab);
                Iterator k = corr.keySet().iterator();
                while (k.hasNext()) {
                    Set x = (Set) k.next();
                    if (f.containsAll(x)) {
                        try {
                            c.addTransition(new Transition((State) corr.get(e),
                                    lab, (State) corr.get(x)));
                        } catch (NoSuchStateException z) {
                        }
                    }
                }
            }
        }
        return c;
    }
}
