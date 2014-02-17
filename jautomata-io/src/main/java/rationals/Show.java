/*
 * (C) Copyright $YEAR Arnaud Bailly (arnaud.oqube@gmail.com),
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
