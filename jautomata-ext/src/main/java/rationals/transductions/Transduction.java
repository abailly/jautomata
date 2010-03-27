/*______________________________________________________________________________
 * 
 * Copyright 2005 Arnaud Bailly - NORSYS/LIFL
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * (1) Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 * (2) Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in
 *     the documentation and/or other materials provided with the
 *     distribution.
 *
 * (3) The name of the author may not be used to endorse or promote
 *     products derived from this software without specific prior
 *     written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Created on 18 mars 2005
 *
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
