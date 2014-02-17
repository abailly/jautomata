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
package rationals.transductions.testing;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import rationals.Automaton;
import rationals.properties.AreEquivalent;
import rationals.properties.TraceEquivalence;
import rationals.transductions.DeterministicTransducer;
import rationals.transductions.Transduction;

/**
 * A class for testing transducers.
 * This class is used to check conformance of finite state machines 
 * whose structure is unknown against a specification. It uses two
 * {@see rationals.transductions.Transduction} objects and, given 
 * a set of input words, check that output from both objects 
 * matches.
 * 
 * @author nono
 * @version $Id: TransducerTester.java 2 2006-08-24 14:41:48Z oqube $
 */
public class TransducerTester {

	/**
	 * Base method for testing.
	 * This method returns properly if the <code>suite</code> is passed, that is
	 * the output of <code>implem</code> for each word in suite is the same
	 * as the output of <code>specif</code>. Same mean that they are the same 
	 * languages. This method is expensive as it needs to determinize each resulting
	 * automaton from {@see rationals.transduction.Transduction.image(List)} 
	 * to check equivalence. <p>
	 * When implementation is known to be a {@see rationals.transductions.RationalFunction},
	 * use method {@see testDeterministic}.
	 * 
	 * 
	 * @param specif a specification transduction
	 * @param implem implementation under test
	 * @param suite a Set of List of input letters.
	 * @throws TestFailure if some test is not passed. The object 
	 * thrown contains the faulty test case.
	 */
	public void test(Transduction specif, Transduction implem, Set suite)
			throws TestFailure {
		Set failures = new HashSet();
		AreEquivalent equiv = new AreEquivalent(new TraceEquivalence());
		for (Iterator it = suite.iterator(); it.hasNext();) {
			Object[] l = (Object[]) it.next();
			Automaton expect = specif.image(l);
			Automaton real = implem.image(l);
			if (!equiv.test(expect, real))
				failures.add(l);
		}
		if (!failures.isEmpty())
			throw new TestFailure(failures);
	}

	/**
	 * Method for testing deterministic  implementations and specifications.
	 * This method works like {@see test(rationals.transductions.Transduction,rationals.transductions.Transduction,java.util.Set)}.
	 * 
	 * @param specif a specification function
	 * @param implem implementation under test
	 * @param suite a Set of arrays of input letters.
	 * @throws TestFailure if some test is not passed. The object 
	 * thrown contains the faulty test case.
	 */
	public void testDeterministic(DeterministicTransducer specif,
			DeterministicTransducer implem, Set suite) throws TestFailure {
		Set failures = new HashSet();
		for (Iterator it = suite.iterator(); it.hasNext();) {
			Object[] l = (Object[]) it.next();
			Object[] expect = specif.imageWord(l);
			Object[] real = implem.imageWord(l);
			//            System.err.println("expect ="+ expect+", real = "+real);
			/* basic tests */
			if (expect == null && real == null)
				continue;
			if ((expect == null && real != null)
					|| (expect != null && real == null)
					|| (expect.length != real.length)) {
				failures.add(l);
				continue;
			}
			/* real test */
			for (int i = 0; i < expect.length; i++)
				if (expect[i] == null ? real[i] != null : !expect[i]
						.equals(real[i]))
					failures.add(l);
		}
		if (!failures.isEmpty())
			throw new TestFailure(failures);
	}

}
