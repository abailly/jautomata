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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import rationals.converters.Codecs;
import rationals.converters.StreamDecoder;
import rationals.converters.StreamEncoder;
import rationals.properties.BinaryTest;
import rationals.properties.UnaryTest;

/**
 * A utility application that handles textual representations of Automaton
 * objects and can apply arbitrary tests using command-line.
 * <p>
 * This application reads one or two automata description in one of the format
 * supported by {@see rationals.converters.Codecs}, either from standard input
 * or from given files. It then tests an arbitrary property, either unary or
 * binary, on the automata, then output the result on stdout as the word
 * <code>true</code> or <code>false</code> and exits with return value 0 if
 * test is succesful, 1 otherwise.
 * 
 * @author nono
 * @version $Id: Test.java 2 2006-08-24 14:41:48Z oqube $
 */
public class Test {

    private static PrintStream output = System.out;

    private static UnaryTest utest = null;

    private static BinaryTest btest = null;

    /** usage */
    static void usage() {
        System.err
                .println("Usage   :  java -cp jauto.jar rationals.Test [options] <test> [file1] [file2] ");
        System.err.println("Options :  ");
        System.err.println("  -h : help (this message)");
        System.err
                .println("test : fully qualified name of a Unary or Binary Test instance. If not fully");
        System.err
                .println("       qualified, then the name is taken to be a test in the rationals.properties package.");
    }

    public static void main(String[] argv) {
        InputStream is1 = System.in;
        InputStream is2 = System.in;

        String expr = null;
        // parse command line
        if (argv.length < 1) {
            usage();
            return;
        }
        for (int i = 0; i < argv.length; i++)
            if (argv[i].startsWith("-")) // command-line option
                switch (argv[i].charAt(1)) {
                case 'h': // algorithm class name
                    usage();
                    return;
                default:
                    System.err.println("Unknown option :" + argv[i]);
                    usage();
                    System.exit(1);
                }
            else {
                /* first non option parameter is an expression, rest are files */
                if (expr == null)
                    expr = argv[i];
                else if (is1 == System.in)
                    try {
                        is1 = new FileInputStream(argv[i]);
                    } catch (FileNotFoundException e) {
                        System.err.println("Cannot open " + argv[i]
                                + " for reading");
                        System.exit(1);
                    }
                else if (is2 == System.in)
                    try {
                        is2 = new FileInputStream(argv[i]);
                    } catch (FileNotFoundException e1) {
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
        /* parse expression */
        if (expr == null) {
            System.exit(2);
        } else {
            try {
                if (expr.indexOf('.') == -1)

                    expr = "rationals.properties." + expr;
                Class cls = Class.forName(expr);
                if (UnaryTest.class.isAssignableFrom(cls))
                    utest = (UnaryTest) cls.newInstance();
                else if (BinaryTest.class.isAssignableFrom(cls))
                    btest = (BinaryTest) cls.newInstance();
                else {
                    System.err
                            .println(expr + " is not a valid test class name");
                    System.exit(1);
                }
            } catch (ClassNotFoundException cne) {
                System.err.println("Cannot find test class " + expr);
                System.exit(1);
            } catch (InstantiationException e) {
                System.err.println("Cannot instantiate test class " + expr
                        + " : " + e.getMessage());
                System.exit(1);
            } catch (IllegalAccessException e) {
                System.err.println("Cannot instantiate test class " + expr
                        + " : " + e.getMessage());
                System.exit(1);
            }
        }
        /* OK - do tranform */
        if (btest != null) {
            doBinaryTest(btest, is1, is2);
        } else if (utest != null) {
            doUnaryTest(utest, is1);
        }
    }

    /**
     * @param utransform2
     * @param is1
     */
    private static void doUnaryTest(UnaryTest utransform2, InputStream is1) {
        /* read automaton 1 */
        Automaton a1 = null;
        boolean ret;
        /* assumes stream are encoded as jauto files */
        StreamDecoder dec = Codecs.decoder("auto");
        StreamEncoder enc = Codecs.encoder("auto");
        try {
            a1 = dec.input(is1);
        } catch (IOException e) {
            System.err.println("Error while decoding :" + e.getMessage());
            System.exit(1);
        }
        /* transform */
        ret = utransform2.test(a1);
        if (ret) {
            System.out.println("true");
            System.exit(0);
        } else {
            System.out.println("false");
            System.exit(1);
        }
    }

    /**
     * @param btransform2
     * @param is1
     * @param is2
     */
    private static void doBinaryTest(BinaryTest btransform2, InputStream is1,
            InputStream is2) {
        /* read automaton 1 */
        Automaton a1 = null;
        Automaton a2 = null;
        boolean ret;
        /* assumes stream are encoded as jauto files */
        StreamDecoder dec = Codecs.decoder("auto");
        StreamEncoder enc = Codecs.encoder("auto");
        try {
            a1 = dec.input(is1);
            a2 = dec.input(is2);
        } catch (IOException e) {
            System.err.println("Error while decoding :" + e.getMessage());
            System.exit(1);
        }
        ret = btransform2.test(a1, a2);
        if (ret) {
            System.out.println("true");
            System.exit(0);
        } else {
            System.out.println("false");
            System.exit(1);
        }

    }

}