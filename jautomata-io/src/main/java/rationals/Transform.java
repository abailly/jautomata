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
import java.io.PrintStream;

import rationals.converters.Codecs;
import rationals.converters.StreamDecoder;
import rationals.converters.StreamEncoder;
import rationals.transformations.BinaryTransformation;
import rationals.transformations.Identity;
import rationals.transformations.UnaryTransformation;

/**
 * A utility application that handles textual representations of Automaton
 * objects and can apply arbitrary transformations using command-line.
 * <p>
 * This application reads one or two automata description in on of 
 * the format supported by {@see rationals.converters.Codecs}, either 
 * from standard input or from given files, and applies an arbitrary 
 * transformation, either unary or binary, on the automata, then
 * output thr result on standard out or in a file.
 * 
 * @author nono
 * @version $Id: Transform.java 2 2006-08-24 14:41:48Z oqube $
 */
public class Transform {

    private static PrintStream output = System.out;

    private static UnaryTransformation utransform = null;
    
    private static BinaryTransformation btransform = null;
    
    /** usage */
    static void usage() {
        System.err
                .println("Usage   :  java -cp jauto.jar rationals.Transformer [options] <transform> [file1] [file2] ");
        System.err.println("Options :  ");
        System.err.println("  -h : help (this message)");
        System.err
                .println("  -o (- | file) : send output to stdout or save it into file (default = stdout)");
        System.err
                .println("transform : fully qualified name of a Unary or Binary Transformation instance");
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
                /* first non option parameter is an expression, rest are files */
                if (expr == null)
                    expr = argv[i];
                else if(is1 == System.in)
                    try {
                        is1 = new FileInputStream(argv[i]);
                    } catch (FileNotFoundException e) {
                        System.err.println("Cannot open "+argv[i]+" for reading");
                        System.exit(1);
                    }
                else if(is2 == System.in)
                    try {
                        is2 = new FileInputStream(argv[i]);
                    } catch (FileNotFoundException e1) {
                        System.err.println("Cannot open "+argv[i]+" for reading");
                        System.exit(1);
                    }
                else {
                    System.err.println("Too many arguments");                
                    usage();
                    System.exit(2);
                }    
            }
        /* parse expression */
        if(expr == null) {
            utransform = new Identity();
        } else {
            /* if expr does not contains dot, assumes this is 
             * a standard transformation from rationals.transformations
             * package
             */
            try {
                if(expr.indexOf('.') == -1)
            
                expr = "rationals.transformations."+expr;
            Class cls = Class.forName(expr);
            if(UnaryTransformation.class.isAssignableFrom(cls)) 
                utransform = (UnaryTransformation)cls.newInstance();
            else if (BinaryTransformation.class.isAssignableFrom(cls))
                btransform = (BinaryTransformation)cls.newInstance();
            else {
                System.err.println(expr + " is not a valid transformation class name");
                System.exit(1);
            }}
            catch(ClassNotFoundException cne) {
               System.err.println("Cannot find transformation class "+expr);
                System.exit(1);
            } catch (InstantiationException e) {
                System.err.println("Cannot instantiate transformationi class "+expr +" : "+e.getMessage());
                System.exit(1);
            } catch (IllegalAccessException e) {
                System.err.println("Cannot instantiate transformationi class "+expr +" : "+e.getMessage());
                System.exit(1);
            }
        }
        /* OK - do tranform */
        if(btransform != null) {
            doBinaryTransform(btransform,is1,is2); 
        } else if(utransform != null) {
            doUnaryTransform(utransform,is1);
        }
    }

    /**
     * @param utransform2
     * @param is1
     */
    private static void doUnaryTransform(UnaryTransformation utransform2, InputStream is1) {
        /* read automaton 1 */
        Automaton a1 = null;
        Automaton a2 = null;
        /* assumes stream are encoded as jauto files */
        StreamDecoder dec = Codecs.decoder("auto");
        StreamEncoder enc=  Codecs.encoder("auto");
        try {
            a1= dec.input(is1);
        } catch (IOException e) {
            System.err.println("Error while decoding :"+e.getMessage());
            System.exit(1);
        }
        /* transform */
        a2 = utransform2.transform(a1);
        try {
            /* output */
            enc.output(a2,output);
        } catch (IOException e1) {
            System.err.println("Error while encoding output from transformation :"+e1.getMessage());
            System.exit(1);
        }
    }

    /**
     * @param btransform2
     * @param is1
     * @param is2
     */
    private static void doBinaryTransform(BinaryTransformation btransform2, InputStream is1, InputStream is2) {
        /* read automaton 1 */
        Automaton a1 = null;
        Automaton a2 = null;
        /* assumes stream are encoded as jauto files */
        StreamDecoder dec = Codecs.decoder("auto");
        StreamEncoder enc=  Codecs.encoder("auto");
        try {
            a1= dec.input(is1);
            a2= dec.input(is2);
        } catch (IOException e) {
            System.err.println("Error while decoding :"+e.getMessage());
            System.exit(1);
        }
        try {
            /* output */
            enc.output(btransform2.transform(a1,a2),output);
        } catch (IOException e1) {
            System.err.println("Error while encoding output from transformation :"+e1.getMessage());
            System.exit(1);
        }
        
    }

}