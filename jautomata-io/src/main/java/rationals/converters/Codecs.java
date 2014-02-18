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
