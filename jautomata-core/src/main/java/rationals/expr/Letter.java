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
 * A letter expression is simply a wrapper over any object. 
 * Note that the semantics of equals and hashcode must be properly
 * implemented by the wrapped object as this may be used in 
 * Collections, for example as keys in hashtables.
 * 
 * @author nono
 * @version $Id: Letter.java 2 2006-08-24 14:41:48Z oqube $
 */
public class Letter extends RationalExpr {

    private Object label;

    public static final Letter epsilon = new Letter(null) {

        public boolean equals(Object o) {
            return o == epsilon;
        }
        

        public SemiRing mult(SemiRing s2) {
            return s2;
        }
        
        public String toString() {
            return "1";
        }

        public int hashCode() {
            return 0;
        }
    };

    /**
     * Construct a new letter expression.
     * 
     * @param o
     *            label of the letter. May not be null. (use epsilon constant).
     */
    public Letter(Object o) {
        this.label = o;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        Letter lt = (Letter) obj;
        if (lt == null)
            return false;
        return lt.label == null ? this.label == null : lt.label.equals(label);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return label.hashCode();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return label.toString();
    }
}
