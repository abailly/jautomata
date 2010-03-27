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
