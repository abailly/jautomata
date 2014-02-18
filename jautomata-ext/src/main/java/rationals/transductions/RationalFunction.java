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
package rationals.transductions;

import java.util.List;

/**
 * A rational function is a rational transduction where there is
 * at most one image for each input word.
 * 
 * @author nono
 * @version $Id: RationalFunction.java 2 2006-08-24 14:41:48Z oqube $
 */
public interface RationalFunction extends Transduction {

    /**
     * Compute the - only - output word image from a given input word.
     * 
     * @param word a List of  Object from input alphabet.
     * @return an array of Object from output alphabet.
     */
    Object[] imageWord(List word);
    
    /**
     * Compute the - only - output word image from a given input word.
     * 
     * @param word an array of  Object from input alphabet.
     * @return an array of Object from output alphabet.
     */
    Object[] imageWord(Object[] word);
    
}
