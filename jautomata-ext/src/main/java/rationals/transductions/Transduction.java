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
package rationals.transductions;

import java.util.List;
import java.util.Set;

import rationals.Automaton;

/**
 * Definition and requirements for  a rational transduction.
 * A rational transduction between two monoids X* and Y* is 
 * a relation in X* x Y* whose graph is rational. Equivalently,
 * a rational transduction is a relation that can be computed 
 * using a transducer.
 * 
 * @author nono
 * @version $Id: Transduction.java 2 2006-08-24 14:41:48Z oqube $
 */
public interface Transduction {
    
    /**
     * Compute the image of the regular language recognized by <code>a</code> 
     * by this transduction.
     *
     * @param a the language on inuput alphabet of this transduction
     * @return a - possibly empty - regular language which is the image
     * of a through this relation.
     */
    public Automaton image(Automaton a);
    
    /**
     * Compute the image of <code>word</code> by this relation.
     * 
     * @param word a List of letters in the domain of this transduction
     * @return an Automaton representing the image of word.
     */
    public Automaton image(List word);
    
    /**
     * Compute the image of <code>word</code> by this relation.
     * 
     * @param word an array of Objects representing the word to be transformed
     * @return an Automaton representing the image of word.
     */
    public Automaton image(Object[] word);
    
    /**
     * Compute the image of <code>word</code> by the inverse of this relation.
     * 
     * @param word a List of letters in the codomain of this transduction
     * @return a Set of List of words which are the images of word 
     */
    public Set inverse(List word);
    
}
