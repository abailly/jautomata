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
 * Created on 6 avr. 2005
 *
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
