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

import java.io.IOException;
import java.io.PrintStream;

import rationals.converters.Codecs;
import rationals.converters.ConverterException;
import rationals.converters.Expression;

/**
 * This application reads a rational expression and output the corresponding
 * Automaton.
 * 
 * @author nono
 * @version $Id: JAuto.java 2 2006-08-24 14:41:48Z oqube $
 */
public class JAuto {

    /** usage */
    static void usage() {
        System.err
                .println("Usage   :  java -cp jauto.jar rationals.JAuto [options] <regular expr1>");
        System.err.println("Options :  ");
        System.err.println("  -h : help (this message)");
        System.err
                .println("  -o <pathname>  : output of display (-o - for stdout)");
        System.exit(1);
    }

    public static void main(String[] argv) {
        PrintStream out = System.out;
        Automaton aut = null;
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
                default:
                    System.err.println("Unknown option " + argv[i]);
                    usage();
                    System.exit(1);

                }
            else
                try {
                    aut = new Expression().fromString(argv[i]);
                } catch (ConverterException e) {
                    System.err.println("Error in reading rational expression :"
                            + e.getMessage());
                    System.exit(1);
                }
        if (aut != null) {
            try {
                Codecs.encoder("auto").output(aut, out);
            } catch (IOException e) {
                System.err.println("Error in outputting automaton :"
                        + e.getMessage());
                System.exit(1);
            }
        }
    }
}
