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
package rationals.algebra;

/**
 * An interface implemented by objects that can be coefficients of a 
 * Matrix.
 * <p>
 * A semi-ring is a structure <code>(R,+,*,0,1)</code> such that:
 * <ol>
 * <li><code>(R,+,0)</code> is a commutative monoid </li>
 * <li><code>(R,*,1)</code> is a monoid </li>
 * <li><code>x*(y+z) = x*y + x*z</code> and <code>(y+z)*x = y*x + z*x</code> : multiplication
 * is distributive with respect to addition </li>
 * <li><code>x*0 = 0*x = 0</code>: 0 is an absorbing element for *</li>
 * </ol>
 * 
 * @author nono
 * @version $Id: SemiRing.java 2 2006-08-24 14:41:48Z oqube $
 * @see Matrix
 */
public interface SemiRing {

    /**
     * Addition of a Semi-ring element with another element.
     * 
     * @param s1
     * @param s2
     * @return
     */
    public SemiRing plus(SemiRing s2);
    
    /**
     * Multiplication of semiring element with another element.
     * 
     * @param s1
     * @param s2
     * @return
     */
    public SemiRing mult(SemiRing s2);
    
    /**
     * Neutral element for multiplication.
     * 
     * @return
     */
    public SemiRing one();
    
    /**
     * Neutral element for addition.
     * 
     * @return
     */
    public SemiRing zero();
    
}
