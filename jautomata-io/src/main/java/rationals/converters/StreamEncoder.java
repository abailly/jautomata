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
 * Created on 29 mars 2005
 *
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
