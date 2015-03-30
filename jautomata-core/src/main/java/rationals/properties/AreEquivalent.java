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
package rationals.properties;

import rationals.Automaton;
import rationals.Builder;
import rationals.Transition;

/**
 * Tests that two automata are equivalent according to some equivalence relation
 * between states.
 * <p>
 * Instances of this class are parameterized by an instance of {@link TransducerRelation}.
 * Given any such instance R, and two automata A1=(Q1,q01,T1,S1,d1) and
 * A2=(Q2,q02,T2,S2,d2), we say that <code>A1 R A2</code> iff
 * <code>q01 R q02</code>.
 * 
 * @author nono
 * @version $Id: AreEquivalent.java 2 2006-08-24 14:41:48Z oqube $
 */
public class AreEquivalent<L, Tr extends Transition<L>, T extends Builder<L, Tr, T>> implements BinaryTest<L, Tr, T> {

    private Relation<L, Tr, T> relation;

    public AreEquivalent(Relation<L, Tr, T> r) {
        this.setRelation(r);
    }

    /**
     * Defines the relation to be used for computing equivalence.
     * 
     * @param r
     */
    public void setRelation(Relation<L, Tr, T> r) {
        this.relation = r;
    }

    /*
     * (non-Javadoc)
     * 
     * @see rationals.tests.BinaryTest#test(rationals.Automaton,
     *      rationals.Automaton)
     */
    public boolean test(Automaton<L, Tr, T> a, Automaton<L, Tr, T> b) {
        relation.setAutomata(a, b);
        return relation.equivalence(a.initials(), b.initials());
    }

}

/*
 * $Log: AreEquivalent.java,v $ Revision 1.1 2005/03/23 07:22:42 bailly created
 * transductions package corrected EpsilonRemover added some tests removed
 * DirectedGRaph Interface from Automaton
 * 
 * Revision 1.3 2005/02/20 21:14:19 bailly added API for computing equivalence
 * relations on automata
 * 
 * Revision 1.2 2004/11/15 12:45:33 bailly changed equivalence algorithm
 * 
 * Revision 1.1 2004/09/21 11:50:28 bailly added interface BinaryTest added
 * class for testing automaton equivalence (isomorphism of normalized automata)
 * added computation of RE from Automaton
 *  
 */