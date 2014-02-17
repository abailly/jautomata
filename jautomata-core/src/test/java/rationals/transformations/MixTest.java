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
package rationals.transformations;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;
import rationals.Automaton;
import rationals.converters.ConverterException;
import rationals.converters.Expression;
import rationals.converters.ToRExpression;
import rationals.properties.isEmpty;

/**
 * @author nono
 * @version $Id: MixTest.java 10 2007-05-30 17:25:00Z oqube $
 */
public class MixTest extends TestCase {

    /**
     * Constructor for MixTest.
     * 
     * @param arg0
     */
    public MixTest(String arg0) {
        super(arg0);
    }

    public void testMix1() throws ConverterException {
        Automaton a = new Expression().fromString("ab*cd");
        Automaton b = new Pruner().transform(new Expression()
                .fromString("a*ebc"));
        Automaton c = new Mix().transform(a, b);
        String re = new ToRExpression().toString(c);
        System.out.println(re);
        assertEquals("aebcd", re);
    }

    public void testMix2() throws ConverterException {
        Automaton a = new Pruner().transform(new Expression()
                .fromString("a(bb)*e"));
        Automaton b = new Pruner().transform(new Expression()
                .fromString("a(bbb)*e"));
        Automaton c = new Reducer().transform(new Mix().transform(a, b));
        System.out.println(new ToRExpression().toString(c));
        assertTrue("automata should accept word", c.accept(makeList("abbbbbbbbbbbbe")));
        assertTrue("automata should accept word", c.accept(makeList("ae")));
        assertTrue("automata should not accept word", !c.accept(makeList("abbe")));
    }

    private List<Object> makeList(String string) {
      List<Object> l = new ArrayList<Object>();
      for(int i=0;i<string.length();i++)
        l.add(string.charAt(i)+"");
      return l;
    }

    public void testMix4() throws ConverterException {
        Automaton a = new Expression().fromString("a(b+c)(ab)*");
        Automaton b = new Expression().fromString("(a+b)*c");
        Automaton c = new Reducer().transform(new Mix().transform(a, b));
        String re = new ToRExpression().toString(c);
        System.out.println(re);
        assertEquals("ac", re);
    }

    public void testMixCommute() throws ConverterException {
        Automaton a = new Expression().fromString("ab*cd");
        Automaton b = new Pruner().transform(new Expression()
                .fromString("a*ebc"));
        Automaton c = new Mix().transform(a, b);
        Automaton d = new Mix().transform(b, a);
        String rec = new ToRExpression().toString(c);
        System.err.println("a m b =" +rec);
        String red = new ToRExpression().toString(d);
        System.err.println("b m a =" +red);
        assertEquals(rec,red);
    }

    public void testMixEmpty() throws ConverterException {
        Automaton a = new Expression().fromString("abc");
        Automaton b = new Expression()
                .fromString("acb");
        Automaton c = new Mix().transform(a, b);
        assertTrue(new isEmpty().test(c));
    }

}
