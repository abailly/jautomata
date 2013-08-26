/**
 *  Copyright Murex S.A.S., 2003-2013. All Rights Reserved.
 * 
 *  This software program is proprietary and confidential to Murex S.A.S and its affiliates ("Murex") and, without limiting the generality of the foregoing reservation of rights, shall not be accessed, used, reproduced or distributed without the
 *  express prior written consent of Murex and subject to the applicable Murex licensing terms. Any modification or removal of this copyright notice is expressly prohibited.
 */
package rationals;

import junit.framework.TestCase;
import rationals.converters.ConverterException;
import rationals.converters.DotCodec;
import rationals.converters.Expression;
import rationals.converters.JAutoCodec;
import rationals.transformations.Mix;
import rationals.transformations.Morphism;
import rationals.transformations.Pruner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * @author  nono
 * @version $Id: JAutoCodecTest.java 2 2006-08-24 14:41:48Z oqube $
 */
public class JAutoCodecTest extends TestCase {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors 
    //~ ----------------------------------------------------------------------------------------------------------------

    /**
     * Constructor for JAutoCodecTest.
     *
     * @param arg0
     */
    public JAutoCodecTest(String arg0) {
        super(arg0);
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    public void testJauto() throws ConverterException, IOException {
        /* first encode automaton */
        Automaton b = new Pruner().transform(new Expression().fromString("a(bbb)*e"));
        String out = b.toString();
        ByteArrayInputStream bis = new ByteArrayInputStream(out.getBytes());
        JAutoCodec codec = new JAutoCodec();
        Automaton c = codec.input(bis);
        System.err.println(c);
    }

    public void testMix2Threads() throws Exception {
        Automaton a = new Pruner().transform(new Expression().fromString("abc"));
        Automaton b = new Pruner().transform(new Expression().fromString("def"));

        Automaton mixed = new Pruner().transform(new Mix().transform(a, b));

        Map map = new HashMap();
        map.put("a", "r1 <- x");
        map.put("b", "r1 != 0");
        map.put("c", "y <- 42");
        map.put("d", "r2 <-y");
        map.put("e", "r2 != 0");
        map.put("f", "x <- 42");

        Automaton morphed = new Morphism(map).transform(mixed);

        System.err.println(morphed.enumerate(6));
//
        new JAutoCodec().output(morphed, System.out);
//
        try {
            new DotCodec().output(morphed, System.out);
        }catch(Throwable t) {
            t.printStackTrace();
        }
        
        assertTrue(true);
    }
}
