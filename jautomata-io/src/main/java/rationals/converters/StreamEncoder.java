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
import java.io.OutputStream;

import rationals.Automaton;

/**
 * An interface for encoding an automaton a given stream.
 * This interface is the base interface for converters that serializes an 
 * Automaton to an output stream using a coding format, such as XML or another 
 * format. Normally, an encoder comes with a decoder that should be able to 
 * deserialize the automaton.
 * 
 * @author nono
 * @version $Id: StreamEncoder.java 2 2006-08-24 14:41:48Z oqube $
 * @see StreamEncoder
 */
public interface StreamEncoder {

    /**
     * Output the given automaton to the given stream.
     * The encoding is specific to the underlying format but a basic 
     * assumption is that encoder/decoder pairs should be compatible.
     * 
     * @param a the Automaton object to encode. May not be null.
     * @param stream the stream to write to. Closing of the stream is
     * the responsibility of the caller. 
     * @throws IOException if something bad happens on the underlying stream.
     * @see StreamDecoder(InputStream)
     */
    public void output(Automaton a, OutputStream stream) throws IOException;
    
}
