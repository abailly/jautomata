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
package rationals.ioautomata;

import org.hamcrest.Matcher;
import rationals.Synchronization;
import rationals.ioautomata.IOTransition.IOLetter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static rationals.ioautomata.IOAlphabetType.*;

/**
 * A synchronization between input/output alphabets. This implementation of
 * Synchronization interface expects instances of {@see
 * rationals.ioautomat.IOTransition}. It returns the label on transition if one
 * of the transition is input and the other is output.
 * 
 * @author nono
 * @version $Id: IOSynchronization.java 2 2006-08-24 14:41:48Z oqube $
 */
public class IOSynchronization implements Synchronization {

	public static boolean inputMatchesLetter(Object o, IOTransition.IOLetter lt) {
		if (lt.label instanceof Matcher<?>)
			return ((Matcher<?>) lt.label).matches(o);
		else
			return lt.label.equals(o);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see rationals.Synchronization#synchronize(rationals.Transition,
	 * rationals.Transition)
	 */
	public Object synchronize(Object t1, Object t2) {
		if (t1 == null || t2 == null)
			return null;
		IOLetter io1 = (IOLetter) t1;
		IOLetter io2 = (IOLetter) t2;
		if (!io1.label.equals(io2.label))
			return null;
		switch (io1.type) {
		case INPUT:
			if (io2.type == OUTPUT)
				return new IOLetter(io1.label, INTERNAL);
			break;
		case OUTPUT:
			if (io2.type == INPUT)
				return new IOLetter(io1.label, INTERNAL);
			break;
		case INTERNAL:
			return null;
		}
		return null;
	}

	/*
	 * TODO
	 * 
	 * @see rationals.Synchronization#synchronizing(java.util.Set,
	 * java.util.Set)
	 */
	public Set<?> synchronizable(Set a, Set b) {
		HashSet<Object> result = new HashSet<Object>();
		for (Object la : a) {
			for (Object lb : b) {
				Object sync = synchronize(la, lb);
				if (sync != null) {
					result.add(la);
					result.add(lb);
				}
			}
		}
		result.remove(null);
		return result;
	}

    @Override
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
		boolean result = false;
		for (Object o : alph) {
			result |= (synchronize(object, o) != null);
		}
		return result;
	}

}
