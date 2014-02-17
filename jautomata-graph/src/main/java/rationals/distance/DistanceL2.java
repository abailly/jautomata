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
package rationals.distance;

import rationals.Automaton;

/**
 * Standard euclidean norm based distance.
 * 
 * @author nono
 * @version $Id: DistanceL2.java 2 2006-08-24 14:41:48Z oqube $
 */
public class DistanceL2 extends Distance {

    /**
     * @param a
     */
    public DistanceL2(Automaton a) {
        super(a);
    }

    /*
     * (non-Javadoc)
     * 
     * @see rationals.distance.Distance#norm(double[])
     */
    public double norm(double[] vec) {
        double acc = 0.0;
        int l = vec.length;
        for (int i = 0; i < l; i++)
            acc += vec[i] * vec[i];
        return Math.sqrt(acc);
    }

    /*
     * (non-Javadoc)
     * 
     * @see rationals.distance.Distance#exponent(double)
     */
    public double exponent(double d) {
        return Math.sqrt(d);

    }

}
