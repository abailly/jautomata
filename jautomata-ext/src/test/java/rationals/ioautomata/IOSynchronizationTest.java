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
package rationals.ioautomata;

import junit.framework.TestCase;
import rationals.Automaton;
import rationals.ioautomata.IOTransition.IOLetter;
import rationals.properties.Bisimilar;
import rationals.transformations.Mix;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static rationals.ioautomata.IOAlphabetType.*;

public class IOSynchronizationTest extends TestCase {

	IOLetter l1 = new IOLetter("toto", INPUT);
	IOLetter l2 = new IOLetter("toto", OUTPUT);
	IOLetter l3 = new IOLetter("tutu", OUTPUT);
	IOLetter l4 = new IOLetter("toto", INTERNAL);
	IOLetter l6 = new IOLetter("tutu", INTERNAL);
	IOLetter l5 = new IOLetter("tutu", INPUT);
	IOLetter l7 = new IOLetter("tata", INTERNAL);
	IOSynchronization iosynch = new IOSynchronization();

	public void testCanSynchronizeTwoLettersToANewLetter() throws Exception {
		assertEquals(l4, iosynch.synchronize(l1, l2));
		assertEquals(l4, iosynch.synchronize(l2, l1));
		assertNull(iosynch.synchronize(l1, l3));
		assertNull(iosynch.synchronize(l1, l4));
		assertNull(iosynch.synchronize(l2, l4));
		assertNull(iosynch.synchronize(l1, l1));
	}

	public void testCanTellIfSomeLetterIsSynchronizableWithSomeSet()
			throws Exception {
		assertNotSynchableWith(l3);
		assertNotSynchableWith(l4);
		assertNotSynchableWith(l4, l3);
		assertSynchableWith(l2);
		assertSynchableWith(l2, l3);
	}

	public void testCanProduceSynchronizableSetFromTwoAlphabets()
			throws Exception {
		Set<IOLetter> alph1 = alphabet(l1);
		Set<IOLetter> alph2 = alphabet(l2);
		Set<IOLetter> alph3 = alphabet(l3);
		assertEquals(alphabet(l1, l2), iosynch.synchronizable(alph1, alph2));
		assertEquals(Collections.emptySet(), iosynch.synchronizable(alph1,
				alph3));
		Set<IOLetter> synchable = alphabet(l1, l3, l2, l5);
		assertEquals(synchable, iosynch.synchronizable(alphabet(l1, l3),
				alphabet(l2, l5)));
		assertEquals(synchable, iosynch.synchronizable(alphabet(l1, l3),
				alphabet(l2, l5, l7)));
	}

	public void testCanComputeMixProductOfTwoIOAutomata() throws Exception {
		IOAutomaton<IOTransition, IOTransitionBuilder> a = new IOAutomaton<IOTransition, IOTransitionBuilder>(
				new IOTransitionBuilder());
		IOAutomaton<IOTransition, IOTransitionBuilder> b = new IOAutomaton<IOTransition, IOTransitionBuilder>(
				new IOTransitionBuilder());
		a.from("q0").receive("a").go("q1").from("q1").send("b").go("q2");
		a.state("q0").setInitial(true);
		b.from("q0").send("a").go("q1").from("q1").receive("b").go("q0");
		b.state("q0").setInitial(true);
		Mix mix = new Mix(new IOSynchronization());
		Automaton transform = mix.transform(a, b);
		IOAutomaton<IOTransition, IOTransitionBuilder> c = new IOAutomaton<IOTransition, IOTransitionBuilder>(
				new IOTransitionBuilder());
		c.from("q0").on("a").go("q1").from("q1").on("b").go("q2");
		c.state("q0").setInitial(true);
		assertTrue(new Bisimilar().test(transform, c));

	}

	private void assertNotSynchableWith(IOLetter... letter) {
		Set<IOLetter> alph = alphabet(letter);
		assertFalse(iosynch.synchronizeWith(l1, alph));
	}

	private Set<IOLetter> alphabet(IOLetter... letter) {
		Set<IOLetter> alph = new HashSet<IOLetter>();
		alph.addAll(Arrays.asList(letter));
		return alph;
	}

	private void assertSynchableWith(IOLetter... letter) {
		Set<IOLetter> alph = alphabet(letter);
		assertTrue(iosynch.synchronizeWith(l1, alph));
	}
}
