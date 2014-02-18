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

    private static final File tmp = new File(TransformerTest.class.getProtectionDomain().getCodeSource().getLocation().getFile());

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
