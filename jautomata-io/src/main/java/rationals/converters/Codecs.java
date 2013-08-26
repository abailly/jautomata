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

import java.util.HashMap;
import java.util.Map;

/**
 * Base entry point for storing/retrieving codecs for Automaton.
 * 
 * This class provides a way for coders/decoders to register themselves with a
 * format - a String - and to clients to retrieve encoders/decoders for specific
 * formats. <p/>The <code>register</code> methods allow registering of Class
 * instances associated with specific format strings. The {@see #encoder(String)}
 * and {@see #decoder(String)}return an instance of the given Class provided an
 * empty constructor has been provided.
 * 
 * @author nono
 * @version $Id: Codecs.java 2 2006-08-24 14:41:48Z oqube $
 */
public abstract class Codecs {

    private static final Map encoders = new HashMap();
    private static final Map decoders = new HashMap();

    static {
        /* default codecs */
        Class autof;
        try {
            autof = Class.forName("rationals.converters.JAutoCodec");
            registerEncoder("auto",autof);
            registerDecoder("auto",autof);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            autof = Class.forName("rationals.converters.DotCodec");
            registerEncoder("dot" +
            		"",autof);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Register a given encoder with a given format key. The format key is
     * usually a file extension.
     * 
     * @param format
     *            the format to register encoder with.
     * @param enc
     *            the StreamEncoder object for this format.
     * @throws IllegalArgumentException
     *             if <code>enc</code> does not implements StreamEncoder
     *             interface.
     */
    public static final void registerEncoder(String format,Class enc) {
        if(!StreamEncoder.class.isAssignableFrom(enc))
            throw new IllegalArgumentException("Class "+enc +" must implement "+StreamEncoder.class);
        encoders.put(format,enc);
    }
    
    /**
     * Register a given decoder with a given format key. The format key is
     * usually a file extension.
     * 
     * @param format
     *            the format to register decoder with.
     * @param dec
     *            the decoder Class instance for this format.
     * @throws IllegalArgumentException
     *             if <code>enc</code> does not implements StreamEncoder
     *             interface.
     */
    public static final void registerDecoder(String format,Class dec) {
        if(!StreamDecoder.class.isAssignableFrom(dec))
            throw new IllegalArgumentException("Class "+dec+" must implement "+StreamDecoder.class);
        decoders.put(format,dec);
    }
    
    
    /**
     * Retrieve an encoder for given format.
     * 
     * @param format
     *            the string representing the format.
     * @return a - possibly null - instance of encoder object associated with
     *         this format. A null return value may denote a problem with either
     *         the format (it is unregistered) or the encoder class (it may not
     *         be instantiated).
     */
    public static StreamEncoder encoder(String format) {
        Class cls = (Class)encoders.get(format);
        try {
            return (StreamEncoder)cls.newInstance();
        } catch (InstantiationException e) {
            return null;
        } catch (IllegalAccessException e) {
            return null;
        }
    }
    
    /**
     * Retrieve a decoder for given format.
     * 
     * @param format
     *            the string representing the format.
     * @return a - possibly null - instance of decoder object. A null return
     *         value may denote a problem with either the format (it is
     *         unregistered) or the encoder class (it may not be instantiated).
     */
    public static StreamDecoder decoder(String format) {
        Class cls = (Class)decoders.get(format);
        try {
            return (StreamDecoder)cls.newInstance();
        } catch (InstantiationException e) {
            return null;
        } catch (IllegalAccessException e) {
            return null;
        }
    }
}
