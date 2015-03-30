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
package rationals.transformations;

import java.util.Set;

import rationals.State;


public final class StatesCouple {
    public final Set<State> sa;
    public final Set<State> sb;
    private final int hash;

    public StatesCouple(Set<State> sa, Set<State> sb) {
        this.sa = sa;
        this.sb = sb;
        this.hash = sa.hashCode() + sb.hashCode();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
    	if (!(obj instanceof StatesCouple)) return false;
        StatesCouple sc = (StatesCouple) obj;
        return sc.sa.equals(sa) && sc.sb.equals(sb);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return hash;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return " < " + sa.toString() + "," + sb.toString() + " >";
    }
}