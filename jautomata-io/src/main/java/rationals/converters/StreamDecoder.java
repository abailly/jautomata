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
package rationals.converters;

import java.io.IOException;
import java.io.InputStream;

import rationals.Automaton;

/**
 * An interface for constructing an Automaton from a given stream.
 * This interface is the counterpart of the decoder. It reads data from 
 * a given stream encoding an automaton in a certain format (eg. XML) and 
 * then returns an Automaton object constructed from this data. 
 * 
 * @author nono
 * @version $Id: StreamDecoder.java 2 2006-08-24 14:41:48Z oqube $
 * @see StreamEncoder
 */
public interface StreamDecoder {
    
    /**
     * Construct an Automaton from the given stream.
     * The encoding is implementation specific and the basic contract is
     * that a certain decoder must be able to understand data from a 
     * compatible encoder.
     * 
     * @param is the stream to read data from. The caller is responsible for
     * closing the stream.
     * @return a new Automaton object.
     * @throws IOException if something bad happens in the underlying stream.
     * @see StreamEncoder.output(rationals.Automaton, java.io.OutputStream)
     */
    public Automaton input(InputStream is) throws IOException;

}
