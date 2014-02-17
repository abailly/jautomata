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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Default synchronization scheme for standard automaton. This class
 * synchronizes the labels of two transitions if they are equal as returned by
 * {@see java.lang.Object#equals(java.lang.Object)}.
 * 
 * @author nono
 * @version $Id: DefaultSynchronization.java 2 2006-08-24 14:41:48Z oqube $
 */
public class DefaultSynchronization implements Synchronization {

    /*
     * (non-Javadoc)
     * 
     * @see rationals.Synchronization#synchronize(rationals.Transition,
     *      rationals.Transition)
     */
    public Object synchronize(Object t1, Object t2) {
        return t1 == null ? null : (t1.equals(t2) ? t1 : null);
    }

    /* (non-Javadoc)
     * @see rationals.Synchronization#synchronizing(java.util.Set, java.util.Set)
     */
    public Set synchronizable(Set a, Set b) {
        Set r = new HashSet(a);
        r.retainAll(b);
        return r;
    }

    /*
     * TO VERIFY (non-Javadoc)
     * @see rationals.Synchronization#synchronizing(java.util.Collection)
     */
    public Set synchronizing(Collection alphabets) {
        Set niou = new HashSet();
        /*
         * synchronization set is the union of pairwise 
         * intersection of the sets in alphl
         */
        for(Iterator i = alphabets.iterator();i.hasNext();) {
            Set s = (Set)i.next();
            for(Iterator j = alphabets.iterator();j.hasNext();) {
                Set b = (Set)j.next();
                niou.addAll(synchronizable(s,b));
            }
        }
        return niou;
    }

    public boolean synchronizeWith(Object object, Set alph) {
        return alph.contains(object);
    }

}