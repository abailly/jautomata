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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import rationals.transductions.DeterministicTransducer;

/**
 * Generate a test suite according to the <em>W method</em>.
 * 
 * @author nono
 * @version $Id: WTestGenerator.java 2 2006-08-24 14:41:48Z oqube $
 */
public class WTestGenerator implements TransducerTestGenerator {

    private int sdelta;

    /**
     * Create a W tester with a maximum bound on the expected number of states
     * of the IUT.
     * 
     * @param k
     *            the maximum number of extra states of the IUT. Must be
     *            positive or null.
     */
    public WTestGenerator(int k) {
        this.sdelta = k;
    }

    /*
     * (non-Javadoc)
     * 
     * @see rationals.transductions.testing.TransducerTester#testSequence(rationals.transductions.Transducer)
     */
    public Set testSuite(DeterministicTransducer t) {
        /* construct test suite */
        Set s = new HashSet();
        /* generate transition cover */
        Set T = t.makeTransitionCover();
        /* generate distinguishing set */
        Set W = t.makeCharacterizingSet();
        /* current list of word */
        Set /* < List < Object >> */aw = new HashSet();
        aw.add(new ArrayList());
        Set alphi = t.inputAlphabet();
        for (int i = 0; i <= sdelta; i++) {
            /* concatenate input alphabet */
            Set naw = new HashSet(aw);
            for (Iterator it = aw.iterator(); it.hasNext();) {
                List l = (List) it.next();
                for (Iterator it2 = alphi.iterator(); it2.hasNext();) {
                    List nl = new ArrayList(l);
                    nl.add(it2.next());
                    naw.add(nl);
                }
            }
            aw = naw;
        }
        /* append everything */
        for (Iterator i = T.iterator(); i.hasNext();) {
            List l = (List) i.next();
            for (Iterator i2 = aw.iterator(); i2.hasNext();) {
                List l2 = (List) i2.next();
                for (Iterator i3 = W.iterator(); i3.hasNext();) {
                    List l3 = new ArrayList(l);
                    l3.addAll(l2);
                    l3.addAll((List) i3.next());
                    s.add(l3.toArray());
                }
            }
        }
        return s;
    }
}