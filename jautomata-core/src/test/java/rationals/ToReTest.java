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
package rationals;

import java.util.ArrayList;
import java.util.Arrays;

import junit.framework.TestCase;
import rationals.converters.ConverterException;
import rationals.converters.Expression;
import rationals.converters.ToRExpression;

/**
 * @author nono
 * @version $Id: ToReTest.java 2 2006-08-24 14:41:48Z oqube $
 */
public class ToReTest extends TestCase {

  public ToReTest(String n) {
    super(n);
  }
  
  public void testRE1() throws ConverterException {
      String re = "ab*c";
      Automaton a = new Expression().fromString(re);
      String er = new ToRExpression().toString(a);
      System.err.println(er);
      a = new Expression().fromString(er);
      assertTrue(a.accept(Arrays.asList(new Object[]{"a","b","b","c"})));    
      assertTrue(!a.accept(Arrays.asList(new Object[]{"a","b","b"})));  
    }
    
  public void testRESingleton() throws ConverterException {
      String re = "a";
      Automaton a = new Expression().fromString(re);
      String er = new ToRExpression().toString(a);
      System.err.println(er);
      a = new Expression().fromString(er);
      assertTrue(a.accept(Arrays.asList(new Object[]{"a"})));   
    }
    
  public void testREEpsilon() throws ConverterException {
    String re = "(ab*c)*";
    Automaton a = new Expression().fromString(re);
    String er = new ToRExpression().toString(a);
    System.err.println(er);
    a = new Expression().fromString(er);
    assertTrue(a.accept(new ArrayList()));    
    assertTrue(a.accept(Arrays.asList(new Object[]{"a","b","b","c"})));    
    assertTrue(!a.accept(Arrays.asList(new Object[]{"a","b","b"})));  
  }
  
  
}

/* 
 * $Log: ToReTest.java,v $
 * Revision 1.1  2004/09/21 11:50:28  bailly
 * added interface BinaryTest
 * added class for testing automaton equivalence (isomorphism of normalized automata)
 * added computation of RE from Automaton
 *
*/