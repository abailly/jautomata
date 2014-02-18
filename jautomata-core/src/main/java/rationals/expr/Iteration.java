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

/**
 * @author nono
 * @version $Id: Iteration.java 2 2006-08-24 14:41:48Z oqube $
 */
public class Iteration extends RationalExpr{

    
    private RationalExpr expr;

    public Iteration(RationalExpr expr) {
        this.expr = expr;
    }
    
    /**
     * @return Returns the expr.
     */
    public RationalExpr getExpr() {
        return expr;
    }
    
    /**
     * @param expr The expr to set.
     */
    public void setExpr(RationalExpr expr) {
        this.expr = expr;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
       return expr.toString() + "*";
    }
}
