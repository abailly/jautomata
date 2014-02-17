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

import java.util.Set;

public class StatesTuple {
    public final Set[] sets;

    final int hash;

    public StatesTuple(Set[] sets) {
        this.sets = new Set[sets.length];
        System.arraycopy(sets,0,this.sets,0,sets.length);
        int h = 0;
        for (int i = 0; i < sets.length; i++)
            h ^= sets[i].hashCode() << i;
        this.hash = h;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        StatesTuple sc = (StatesTuple) obj;
        for (int i = 0; i < sc.sets.length; i++)
            if (!sets[i].equals(sc.sets[i]))
                return false;
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return hash;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("( ");
        for(int i=0;i<sets.length;i++){
            sb.append(sets[i]);
            if(i != sets.length-1)
                sb.append(",");
        }
        sb.append(")");
        return sb.toString();
    }
}