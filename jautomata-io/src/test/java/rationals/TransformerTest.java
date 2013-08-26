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

import junit.framework.TestCase;
import rationals.converters.*;

import java.io.*;
import java.util.Arrays;
import java.util.List;

/**
 * @author nono
 * @version $Id: TransformerTest.java 2 2006-08-24 14:41:48Z oqube $
 */
public class TransformerTest extends TestCase {

    private Automaton aut;

    private static final File tmp = new File(System.getProperty("java.io.tmpdir"));
    private File test1;
    private File test2;
    private File out;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        /* output into file */
        test1 = new File(tmp, "test1.auto");
        test2 = new File(tmp, "test2.auto");
        out = new File(tmp, "test.auto");
        
        aut = new Expression().fromString("ab*");
        StreamEncoder enc= Codecs.encoder("auto");
        enc.output(aut,new FileOutputStream(test1));
        Automaton a = new Expression().fromString("ac*");
        enc.output(a,new FileOutputStream(test2));
     }

    
    protected void tearDown() throws Exception {
        super.tearDown();
        /* delete files */
        test1.delete();
        test2.delete();
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
        String[] args = new String[]{"Reducer",test1.getAbsolutePath()};
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
        String[] args = new String[]{"Reducer","-o",out.getAbsolutePath(),test1.getAbsolutePath()};
        /* collect output */
        ByteArrayOutputStream bos;
        Transform.main(args);
        /* decode output */
        StreamDecoder dec = Codecs.decoder("auto");
        assertTrue(out.exists());
        Automaton a = dec.input(new FileInputStream(out));
        List word = Arrays.asList(new String[]{"a","b","b"});
        assertTrue(a.accept(word));
    }

    public void testUnaryStdin() throws ConverterException, IOException {
        String[] args = new String[]{"Reducer","-o",out.getAbsolutePath()};
        System.setIn(new FileInputStream(test1));
        /* collect output */
        ByteArrayOutputStream bos;
        Transform.main(args);
        /* decode output */
        StreamDecoder dec = Codecs.decoder("auto");
        assertTrue(out.exists());
        Automaton a = dec.input(new FileInputStream(out));
        List word = Arrays.asList(new String[]{"a","b","b"});
        assertTrue(a.accept(word));
    }
    
    public void testBinary() throws FileNotFoundException, IOException {
        String[] args = new String[]{"Mix","-o",out.getAbsolutePath(),test1.getAbsolutePath(),test2.getAbsolutePath()};
        /* collect output */
        ByteArrayOutputStream bos;
        Transform.main(args);
        /* decode output */
        StreamDecoder dec = Codecs.decoder("auto");
        assertTrue(out.exists());
        Automaton a = dec.input(new FileInputStream(out));
        List word = Arrays.asList(new String[]{"a","b","b"});
        List word2 = Arrays.asList(new String[]{"a","c","c"});
        assertTrue(a.accept(word)&&a.accept(word2));        
    }
}
