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
package rationals.ioautomata.testing;

import junit.framework.TestCase;
import org.hamcrest.CoreMatchers;
import rationals.State;
import rationals.ioautomata.IOAlphabetType;
import rationals.ioautomata.IOAutomaton;
import rationals.ioautomata.IOSynchronization;
import rationals.ioautomata.IOTransition;

/**
 * @author nono
 * @version $Id: SynchIOAutomatonSMAdapterTest.java 12 2007-06-01 07:03:41Z oqube $
 */
public class SynchIOAutomatonSMAdapterTest extends TestCase {

  private IOAutomaton impl;

  private IOSynchronization synch;

  private IOAutomaton ndet;

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

    ndet = new IOAutomaton();
    s1 = ndet.addState(true, true);
    s2 = ndet.addState(false, false);
    ndet.addTransition(new IOTransition(s1, CoreMatchers.equalTo("a"), s2,
        IOAlphabetType.INPUT));
    ndet.addTransition(new IOTransition(s2, "b", s1, IOAlphabetType.OUTPUT));
    ndet.addTransition(new IOTransition(s2, "c", s1, IOAlphabetType.OUTPUT));
    synch = new IOSynchronization();
  }

  /**
   * Constructor for SynchIOAutomatonSMAdapterTest.
   * 
   * @param arg0
   */
  public SynchIOAutomatonSMAdapterTest(String arg0) {
    super(arg0);
  }

  public void testBasic() {
    SynchIOAutomatonSMAdapter ior = new SynchIOAutomatonSMAdapter(impl);
    Thread th = new Thread(ior);
    th.setDaemon(true);
    th.start();
    for (int i = 0; i < 10; i++) {
      ior.input("a");
      Object out = ior.output();
      assertEquals("b", out);
    }
    ior.stop();
    assertTrue(!ior.isError());
  }

  public void testNondeterministic() {
    SynchIOAutomatonSMAdapter ior = new SynchIOAutomatonSMAdapter(ndet);
    IOTransition.IOLetter outb = new IOTransition.IOLetter("b",
        IOAlphabetType.INPUT);
    IOTransition.IOLetter outc = new IOTransition.IOLetter("c",
        IOAlphabetType.INPUT);
    Thread th = new Thread(ior);
    th.setDaemon(true);
    th.start();
    for (int i = 0; i < 10; i++) {
      ior.input("a");
      Object out = ior.output();
      assertTrue("b".equals(out) || "c".equals(out));
    }
    ior.stop();
    assertTrue(!ior.isError());
  }

  public void testNondeterministicError() {
    SynchIOAutomatonSMAdapter ior = new SynchIOAutomatonSMAdapter(ndet);
    IOTransition.IOLetter outb = new IOTransition.IOLetter("b",
        IOAlphabetType.INPUT);
    Thread th = new Thread(ior);
    th.setDaemon(true);
    th.start();
    for (int i = 0; i < 10; i++) {
      ior.input(new IOTransition.IOLetter("a", IOAlphabetType.OUTPUT));
      Object out = ior.output();
    }
    ior.stop();
    assertTrue(ior.isError());
  }
}
