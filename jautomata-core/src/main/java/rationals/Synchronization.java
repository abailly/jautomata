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
package rationals;

import java.util.Collection;
import java.util.Set;

/**
 * An interface for defining various synchronization schemes.
 * This interface is used in {@see rationals.transformations.Synchronize} for
 * and allows various strategies of synchronization between transitions
 * of two automata.
 * 
 * @author nono
 * @version $Id: Synchronization.java 2 2006-08-24 14:41:48Z oqube $
 */
public interface Synchronization {

    /**
     * Synchronize two transitions.
     * This method should return a letter denoting the result of synchronizing 
     * the two transitions' labels. If the result is <code>null</code>, then no synchronization
     * occurs.
     * 
     * @param t1 first label to synchronize
     * @param t2 second label to synchronize
     * @return a non null Object if the two transitions can be synchronized.
     */
    Object synchronize(Object t1, Object t2);
    
    /**
     * Compute the synchronizing letters from two alphabets.
     * This method returns the set of letters from a and b that can be synchronized.
     * In the default case, this method simply computes the intersection of the two
     * sets. More precisely, the resultant set will contain the result of all 
     * possible synchronizations between both input sets.
     * 
     * @param a an alphabet 
     * @param b another alphabet
     * @return a new Set of letters (may be empty) from a and b that can be synchronized.
     */
    Set synchronizable(Set a,Set b);

    /**
     * Construct the synchronization alphabet from a collection of
     * alphabets.
     * 
     * <p>This is an extension to more than 2 alphabets of {@link #synchronizable(Set, Set)}.</p>
     * 
     * @param alphabets a collection of alphabets (sets) on which to compute synchronizing letters. 
     * @return a Set implementation containing all letters of all alphabets 
     * in <code>alphabets</code> that could be synchronized.
     */
    Set synchronizing(Collection<Set> alphabets);

    /**
     * Checks whether or not the given letter is synchronizing in the
     * given automaton's alphabet.
     * This method checks in a synchronization dependant way that the 
     * given letter pertains to the synchronization set.
     *  
     * @param object the letter to check
     * @param alph the alphabet
     * @return true if object is synchronizing with some letter in <code>alph</code>, 
     * false otherwise.
     */
    boolean synchronizeWith(Object object, Set alph);
    
}
