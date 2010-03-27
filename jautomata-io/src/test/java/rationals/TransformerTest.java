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
 * Created on 16 avr. 2005
 *
 */
package rationals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;
import rationals.converters.Codecs;
import rationals.converters.ConverterException;
import rationals.converters.Expression;
import rationals.converters.StreamDecoder;
import rationals.converters.StreamEncoder;

/**
 * @author nono
 * @version $Id: TransformerTest.java 2 2006-08-24 14:41:48Z oqube $
 */
public class TransformerTest extends TestCase {

    private Automaton aut;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        /* output into file */
        aut = new Expression().fromString("ab*");
        StreamEncoder enc= Codecs.encoder("auto");
        enc.output(aut,new FileOutputStream("/tmp/test1.auto"));
        Automaton a = new Expression().fromString("ac*");
        enc.output(a,new FileOutputStream("/tmp/test2.auto"));
     }

    
    protected void tearDown() throws Exception {
        super.tearDown();
        /* delete files */
        File in1 = new File("/tmp/test1.auto");
        File in2 = new File("/tmp/test2.auto");
        File out = new File("/tmp/test.auto");
        in1.delete();
        in2.delete();
        out.delete();
    }
    
    /**
     * Constructor for TransformerTest.
     * @param arg0
     */
    public TransformerTest(String arg0) {
        super(arg0);
    }

    public void testUnary() throws ConverterException, IOException {
        String[] args = new String[]{"Reducer","/tmp/test1.auto"};
        /* collect output */
        ByteArrayOutputStream bos;
        PrintStream ps = new PrintStream(bos = new ByteArrayOutputStream());
        System.setOut(ps);
        Transform.main(args);
        /* decode output */
        StreamDecoder dec = Codecs.decoder("auto");
        Automaton a = dec.input(new ByteArrayInputStream(bos.toByteArray()));
        List word = Arrays.asList(new String[]{"a","b","b"});
        assertTrue(a.accept(word));
    }

    public void testUnaryFilout() throws ConverterException, IOException {
        String[] args = new String[]{"Reducer","-o","/tmp/test.auto","/tmp/test1.auto"};
        /* collect output */
        ByteArrayOutputStream bos;
        Transform.main(args);
        /* decode output */
        StreamDecoder dec = Codecs.decoder("auto");
        assertTrue(new File("/tmp/test.auto").exists());
        Automaton a = dec.input(new FileInputStream("/tmp/test.auto"));
        List word = Arrays.asList(new String[]{"a","b","b"});
        assertTrue(a.accept(word));
    }

    public void testUnaryStdin() throws ConverterException, IOException {
        String[] args = new String[]{"Reducer","-o","/tmp/test.auto"};
        System.setIn(new FileInputStream("/tmp/test1.auto"));
        /* collect output */
        ByteArrayOutputStream bos;
        Transform.main(args);
        /* decode output */
        StreamDecoder dec = Codecs.decoder("auto");
        assertTrue(new File("/tmp/test.auto").exists());
        Automaton a = dec.input(new FileInputStream("/tmp/test.auto"));
        List word = Arrays.asList(new String[]{"a","b","b"});
        assertTrue(a.accept(word));
    }
    
    public void testBinary() throws FileNotFoundException, IOException {
        String[] args = new String[]{"Mix","-o","/tmp/test.auto","/tmp/test1.auto","/tmp/test2.auto"};
        /* collect output */
        ByteArrayOutputStream bos;
        Transform.main(args);
        /* decode output */
        StreamDecoder dec = Codecs.decoder("auto");
        assertTrue(new File("/tmp/test.auto").exists());
        Automaton a = dec.input(new FileInputStream("/tmp/test.auto"));
        List word = Arrays.asList(new String[]{"a","b","b"});
        List word2 = Arrays.asList(new String[]{"a","c","c"});
        assertTrue(a.accept(word)&&a.accept(word2));        
    }
}
