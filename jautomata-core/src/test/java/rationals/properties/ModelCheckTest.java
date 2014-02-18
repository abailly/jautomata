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

import junit.framework.TestCase;
import rationals.Automaton;
import rationals.converters.ConverterException;
import rationals.converters.Expression;

/**
 * @author nono
 * @version $Id: ModelCheckTest.java 2 2006-08-24 14:41:48Z oqube $
 */
public class ModelCheckTest extends TestCase {

    /**
     * @param arg0
     */
    public ModelCheckTest(String arg0) {
        super(arg0);
    }
    
    public void test() throws ConverterException {
        Automaton a = new Expression().fromString("a(b+c)(ab)*");
        Automaton b = new Expression().fromString("(a+b)*c");
        ModelCheck mc = new ModelCheck();
        assertFalse(mc.test(b,a));
        System.err.println(mc.counterExamples());
    }
}
