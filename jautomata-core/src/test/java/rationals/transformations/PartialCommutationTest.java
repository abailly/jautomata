/**
 *  Copyright Murex S.A.S., 2003-2013. All Rights Reserved.
 * 
 *  This software program is proprietary and confidential to Murex S.A.S and its affiliates ("Murex") and, without limiting the generality of the foregoing reservation of rights, shall not be accessed, used, reproduced or distributed without the
 *  express prior written consent of Murex and subject to the applicable Murex licensing terms. Any modification or removal of this copyright notice is expressly prohibited.
 */
package rationals.transformations;

import junit.framework.TestCase;
import rationals.Automaton;
import rationals.converters.Expression;

import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;


class PartialCommutationTes extends TestCase {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    public void _ignored_test_two_letters_commute_when_they_are_adjacent() throws Exception {
        Automaton a = new Expression().fromString("abcd");
        Set bc = new HashSet();
        bc.add("b");
        bc.add("c");
        Automaton commuted = new PartialCommutation(bc).transform(a);

        assertTrue("should recognize commuted word", commuted.accept(asList("a", "c", "b", "d")));
    }
}
