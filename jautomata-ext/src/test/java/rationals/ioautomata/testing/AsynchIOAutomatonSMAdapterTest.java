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
package rationals.ioautomata.testing;

import junit.framework.TestCase;
import org.hamcrest.CoreMatchers;
import rationals.State;
import rationals.ioautomata.IOAlphabetType;
import rationals.ioautomata.IOAutomaton;
import rationals.ioautomata.IOSynchronization;
import rationals.ioautomata.IOTransition;

import java.util.concurrent.LinkedBlockingQueue;


/**
 * @author  nono
 * @version $Id: AsynchIOAutomatonSMAdapterTest.java 13 2007-06-01 16:11:03Z oqube $
 */
public class AsynchIOAutomatonSMAdapterTest extends TestCase {

    private IOAutomaton impl;

    private IOSynchronization synch;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors 
    //~ ----------------------------------------------------------------------------------------------------------------

    /**
     * Constructor for SynchIOAutomatonSMAdapterTest.
     *
     * @param arg0
     */
    public AsynchIOAutomatonSMAdapterTest(String arg0) {
        super(arg0);
    }

    public void test1() throws InterruptedException {
        AsynchIOAutomatonSMAdapter ior = new AsynchIOAutomatonSMAdapter(impl);
        ior.setInQueue(new LinkedBlockingQueue());
        ior.setOutQueue(new LinkedBlockingQueue());
        Thread th = new Thread(ior);
        th.setDaemon(true);
        th.start();
        for (int i = 0; i < 10; i++) {
            ior.input("a");
            while (ior.availableOutput() == 0)
                Thread.sleep(500);
            Object out = ior.output();
            assertEquals("b", out);
            System.err.print('.');
        }
        ior.stop();
        System.err.println('\n');
        assertTrue(!ior.isError());
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        impl = new IOAutomaton();
        State s1 = impl.addState(true, true);
        State s2 = impl.addState(false, false);
        impl.addTransition(new IOTransition(s1, CoreMatchers.equalTo("a"), s2, IOAlphabetType.INPUT));
        impl.addTransition(new IOTransition(s2, "b", s1, IOAlphabetType.OUTPUT));
        synch = new IOSynchronization();
    }
}
