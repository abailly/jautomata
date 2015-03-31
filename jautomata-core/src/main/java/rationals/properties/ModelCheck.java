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
package rationals.properties;

import rationals.Automaton;
import rationals.Builder;
import rationals.Transition;
import rationals.transformations.Complement;
import rationals.transformations.Mix;
import rationals.transformations.Pruner;
import rationals.transformations.ToDFA;

/**
 * This class implements a basic model-checking algorithm.
 * <p>
 * The first automata is first complemented on its alphabet using the operation
 * {@see rationals.transformations.Complement}. It is then synchronized with
 * the second automaton using the {@see rationals.transformations.Mix}
 * operation.
 * <p>
 * If the language produced is empty, then the test returns true which means that
 * automaton <code>b</code> contains the language of <code>a</code>. Else,
 * the language produced represents counterexamples of the property modelled by
 * <code>a</code> in <code>b</code>: the test returns false.
 * <p>
 * The resulting automaton can be retrieved using the method
 * {@see #counterExamples()}.
 * 
 * @version $Id: ModelCheck.java 2 2006-08-24 14:41:48Z oqube $
 */
public class ModelCheck<L, Tr extends Transition<L>, T extends Builder<L, Tr, T>> implements BinaryTest<L, Tr, T> {

    private Automaton<L, Tr, T> cex;

    /*
     * (non-Javadoc)
     * 
     * @see rationals.properties.BinaryTest#test(rationals.Automaton,
     *      rationals.Automaton)
     */
    public boolean test(Automaton<L, Tr, T> a, Automaton<L, Tr, T> b) {
    	Automaton<L, Tr, T> aDFA = new ToDFA<L, Tr, T>().transform(a);
    	Automaton<L, Tr, T> bDFA = new ToDFA<L, Tr, T>().transform(b);
        Automaton<L, Tr, T> caDFA = new Complement<L, Tr, T>().transform(aDFA);
        cex = new Pruner<L, Tr, T>().transform(new Mix<L, Tr, T>().transform(caDFA, bDFA));
        if (new isEmpty<L, Tr, T>().test(cex))
            return true;
        else
            return false;
    }

    /**
     * Return the automaton resulting from this test.
     * 
     * @return an Automaton or null if
     *         {@see #test(rationals.Automaton,rationals.Automaton)}has not
     *         been called yet.
     */
    public Automaton<L, Tr, T> counterExamples() {
        return cex;
    }
}
