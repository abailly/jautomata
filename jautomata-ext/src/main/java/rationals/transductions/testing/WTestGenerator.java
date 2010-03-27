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