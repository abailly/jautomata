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
package rationals.expr;

import rationals.algebra.SemiRing;

/**
 * @author nono
 * @version $Id: RationalExpr.java 2 2006-08-24 14:41:48Z oqube $
 */
public abstract class RationalExpr implements SemiRing {

    
    public static final RationalExpr zero = new RationalExpr() {
        
        public SemiRing mult(SemiRing s1) {
            return zero;
        }

        public SemiRing plus(SemiRing s1) {
            return s1;
        }
        
        @Override
        public boolean equals(Object o) {
            return this == o;
        }
        
        @Override
        public int hashCode() {
            return -1;
        }
        
        @Override
        public String toString() {
            return "0";
        }
    };
    
    public static final RationalExpr one = Letter.epsilon;
    
    /* (non-Javadoc)
     * @see rationals.SemiRing#one()
     */
    public final SemiRing one() {
        return one;
    }
    
    /* (non-Javadoc)
     * @see rationals.SemiRing#zero()
     */
    public final SemiRing zero() {
        return zero;
    }
    
    
    public SemiRing mult(SemiRing s2) {
        if(s2 == zero)
            return zero;
        if(s2 == Letter.epsilon)
            return this;
        RationalExpr re = (RationalExpr)s2;
        return new Product(this,re);
    }
    
    public SemiRing plus(SemiRing s2) {
        if(s2 == zero)
            return this;
        return new Plus(this,(RationalExpr)s2);
    }

}
