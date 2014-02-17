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
package rationals.expr;

/**
 * @author nono
 * @version $Id: BinaryExpr.java 2 2006-08-24 14:41:48Z oqube $
 */
public abstract class BinaryExpr extends RationalExpr {
    
    private RationalExpr left;
    private RationalExpr right;

    /**
     * Construct a binary expression from two sub-expressions.
     * 
     * @param e
     * @param f
     */
    public BinaryExpr(RationalExpr e,RationalExpr f) {
        this.left = e ;
        this.right = f;
    }
    
    /**
     * @return Returns the left.
     */
    public RationalExpr getLeft() {
        return left;
    }
    /**
     * @param left The left to set.
     */
    public void setLeft(RationalExpr left) {
        this.left = left;
    }
    /**
     * @return Returns the right.
     */
    public RationalExpr getRight() {
        return right;
    }
    /**
     * @param right The right to set.
     */
    public void setRight(RationalExpr right) {
        this.right = right;
    }
    
}
