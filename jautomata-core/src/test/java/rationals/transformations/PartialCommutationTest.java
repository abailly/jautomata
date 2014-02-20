/*
 * (C) Copyright 2013 Arnaud Bailly (arnaud.oqube@gmail.com),
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

import junit.framework.TestCase;
import rationals.Automaton;
import rationals.converters.Expression;

import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;


class PartialCommutationTes extends TestCase {


    public void _ignored_test_two_letters_commute_when_they_are_adjacent() throws Exception {
        Automaton a = new Expression().fromString("abcd");
        Set bc = new HashSet();
        bc.add("b");
        bc.add("c");
        Automaton commuted = new PartialCommutation(bc).transform(a);

        assertTrue("should recognize commuted word", commuted.accept(asList("a", "c", "b", "d")));
    }
}
