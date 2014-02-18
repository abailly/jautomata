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
package rationals.transductions.testing;

import java.util.Set;

import rationals.transductions.DeterministicTransducer;
import rationals.transductions.TransductionException;

/**
 * Base interface for generating testing sequences for deterministic transducers.
 * 
 * @author nono
 * @version $Id: TransducerTestGenerator.java 2 2006-08-24 14:41:48Z oqube $
 */
public interface TransducerTestGenerator {

    /**
     * This method generates a test suite for the given transducer 
     * according to some algorithm.
     * 
     * @param t a deterministic minimal transducer.
     * @return a set of input words - arrays of Object - for the transducer 
     * @throws TransductionException if something bad happens while constructing test suite.
     */
    public Set testSuite(DeterministicTransducer t) throws TransductionException;
    
}
