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
 * Created on 21 avr. 2005
 *
 */
package rationals;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import rationals.converters.Codecs;
import rationals.transformations.Morphism;

/**
 * An application that reads an Automaton description and an alphabet and
 * returns the result of applying a morphism to this automaton.
 * <p>
 * The morphism is given as a set of <code>domain=image</code> couples either
 * on the command-line where they are separated by semicolons or in a file where
 * they are separated by new lines.
 * 
 * @author nono
 * @version $Id: Morph.java 2 2006-08-24 14:41:48Z oqube $
 */
public class Morph {

    /** usage */
    static void usage() {
        System.err
                .println("Usage   :  java -cp jauto.jar rationals.Project [options] [automaton] ");
        System.err.println("Options :  ");
        System.err.println("  -h : help (this message)");
        System.err
                .println("  -o <pathname>  : output of projection (-o - for stdout)");
        System.err.println("  -a : output alphabet from input automaton");
        System.err
                .println("  -m string1=string1';...;stringn=stringn'  : mapping");
        System.err
                .println("  -f <pathname>  : read projection set from file <pathname>. Elements must be separated by semicolon");
        System.exit(1);
    }

    public static void main(String[] argv) {
        InputStream is1 = System.in;
        PrintStream out = System.out;
        Automaton aut = null;
        boolean alphabetmode = false;
        Map morph = new HashMap();
        // parse command line
        if (argv.length < 1)
            usage();
        for (int i = 0; i < argv.length; i++)
            if (argv[i].startsWith("-")) // command-line option
                switch (argv[i].charAt(1)) {
                case 'o': // output file
                    String ofname = argv[++i];
                    if (!ofname.equals("-"))
                        try {
                            out = new java.io.PrintStream(
                                    new java.io.FileOutputStream(ofname));
                        } catch (java.io.IOException ioex) {
                            System.err.println("Can't output to :" + ofname
                                    + " : " + ioex);
                            System.exit(1);
                        }
                    else
                        out = System.out;
                    break;
                case 'h':
                case '?': // parameters list
                    usage();
                    System.exit(0);
                    break;
                case 'a':
                    alphabetmode = true;
                    break;
                case 'm': // morphism
                    String stringSet = argv[++i];
                    try {
                        morph = makeMap(new StringReader(stringSet), ';');
                    } catch (IOException e1) {
                        System.err
                                .println("Error while reading mappings from command-line : "
                                        + e1.getMessage());
                        System.exit(1);
                    }
                    break;
                case 'f':
                    // read from file
                    String fname = argv[++i];
                    try {
                        Reader rd = new FileReader(fname);
                        morph = makeMap(rd, '\n');
                        break;
                    } catch (IOException e) {
                        System.err.println("Error while reading mappings from "
                                + fname + " : " + e.getMessage());
                        System.exit(1);
                    }
                default:
                    System.err.println("Unknown option " + argv[i]);
                    usage();
                    System.exit(1);

                }
            else {
                /* first non option is a file */
                if (is1 == System.in)
                    try {
                        is1 = new FileInputStream(argv[i]);
                    } catch (FileNotFoundException e) {
                        System.err.println("Cannot open " + argv[i]
                                + " for reading");
                        System.exit(1);
                    }

                else {
                    System.err.println("Too many arguments");
                    usage();
                    System.exit(2);
                }

            }
        /* read automaton */
        Automaton a;
        try {
            a = Codecs.decoder("auto").input(is1);
        } catch (IOException e) {
            System.err.println("Cannot create automaton from input file : "
                    + e.getMessage());
            System.exit(1);
            return;
        }
        if (alphabetmode)
            outputAlphabet(a.alphabet());
        else {
            /* compute morphism */
            Morphism morphism = new Morphism(morph);
            try {
                a = morphism.transform(a);
                Codecs.encoder("auto").output(a, out);
            } catch (IOException e) {
                System.err.println("Error in outputting automaton :"
                        + e.getMessage());
                System.exit(1);
            }
        }
    }

    /**
     * @param rd
     * @throws IOException
     */
    private static Map makeMap(Reader rd, char sep) throws IOException {
        Map ret = new HashMap();
        String from = null, to;
        StringBuffer sb = new StringBuffer();
        int c;
        while ((c = rd.read()) != -1) {
            switch (c) {
            case '=':
                from = sb.toString().trim();
                sb = new StringBuffer();
                break;
            default:
                if (c == sep) {
                    to = sb.toString().trim();
                    if("".equals(to))
                        to = null;
                    System.err.println("Adding ("+from+","+to+")");
                    ret.put(from, to);
                    from = null;
                    to = null;
                    sb = new StringBuffer();
                } else {
                    sb.append((char) c);
                }
            }
        }
        if (from != null) {
            to = sb.toString().trim();
            if("".equals(to))
                to = null;
            System.err.println("Adding ("+from+","+to+")");
            ret.put(from, to);
        }
        return ret;
    }

    /**
     * @param set
     */
    private static void outputAlphabet(Set set) {
        for (Iterator i = set.iterator(); i.hasNext();) {
            System.out.print(i.next());
            if (i.hasNext())
                System.out.print(';');
        }

    }
}