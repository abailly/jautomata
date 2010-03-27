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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import rationals.converters.Codecs;
import rationals.converters.ConverterException;
import rationals.converters.SwingDisplayer;
import rationals.graph.AutomatonVisualFactory;

/**
 * An application that reads the description of an automaton and displays it,
 * either in a window or to a file.
 * <p>
 * 
 * 
 * @author nono
 * @version $Id: Show.java 2 2006-08-24 14:41:48Z oqube $
 */
public class Show {

    private static OutputStream output = System.out;
    private static boolean window;
    
    /** usage */
    static void usage() {
        System.err
                .println("Usage   :  java -cp jauto.jar rationals.Show [options] [inputfile] ");
        System.err.println("Options :  ");
        System.err.println("  -h : help (this message)");
        System.err
        .println("  -o (- | file) : send output to stdout or save it into file (default = stdout).");
        System.err
        .println("                  In stdout mode, automaton is shown in eps format.");
        System.err
        .println("  -w : show automaton into a window. Compatible with -o");
    }

    public static void main(String[] argv) {
        InputStream is1 = System.in;       
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
                case 'w': // algorithm class name
                    window = true;
                    break;
                case 'o': // output file
                    String ofname = argv[++i];
                    if (!ofname.equals("-"))
                        try {
                            output = new java.io.PrintStream(
                                    new java.io.FileOutputStream(ofname));
                        } catch (java.io.IOException ioex) {
                            System.err.println("Can't output to :" + ofname
                                    + " : " + ioex);
                            System.exit(1);
                        }
                    else
                        output = System.out;
                    break;
                default:
                    System.err.println("Unknown option :" + argv[i]);
                    usage();
                    System.exit(1);
                }
            else {
                /* first non option is a file */
                if(is1 == System.in)
                    try {
                        is1 = new FileInputStream(argv[i]);
                    } catch (FileNotFoundException e) {
                        System.err.println("Cannot open "+argv[i]+" for reading");
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
            System.err.println("Cannot create automaton from input file : "+e.getMessage());
            System.exit(1);
            return;
        }
        /* output */
        if(window)
            show(a);
        else
            doOutput(a);            
    }

    /**
     * @param a
     */
    private static void show(Automaton a) {
        SwingDisplayer d = new SwingDisplayer();
        try {
            d.setAutomaton(a);
        } catch (ConverterException e) {
        }
        d.setVisible(true);
    }

    /**
     * @param a
     */
    private static void doOutput(Automaton a) {
        try {
            AutomatonVisualFactory.epsOutput(a,output,null);
        } catch (IOException e) {
            System.err.println("Error output automaton : "+e.getLocalizedMessage());
        }
    }
}
