package rationals.ioautomata;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import junit.framework.TestCase;

public class IOAutomatonSMAdapterTest extends TestCase {

  private IOAutomatonSMAdapter sm;

  private IOAutomaton<IOTransitionBuilder> a;

  protected void setUp() {

    a = new IOAutomaton<IOTransitionBuilder>(new IOTransitionBuilder());
    a.state("s1").setInitial(true);
    a.from("s1").receive(1).go("s2").from("s2").send(2).go("s3").send(3).go(
        "s4");
    sm = new IOAutomatonSMAdapter(a);
  }

  public void testAutomatonAcceptsInputAndChangeState() {
    sm.input(1);
    assertTrue("not in expected state", sm.getState().contains(a.state("s2")));
    assertTrue("not in expected state", !sm.getState().contains(a.state("s1")));
  }

  public void testNonInputEnabledAutomatonDoesNotAcceptIncorrectInput() {
    try {
      sm.input(2);
      fail("ISException expected");
    } catch (IllegalStateException e) {
      // ok
    }
  }

  public void testInputEnabledAutomatonAcceptsAnyInput() {
    sm.setInputEnabled(true);
    sm.input(2);
    assertTrue("not in expected state", !sm.getState().contains(a.state("s2")));
    assertTrue("not in expected state", sm.getState().contains(a.state("s1")));
  }

  public void testInputEnabledAutomatonAcceptsAnyInputButDoesNotFireInternalWithNoMatch() {
    Function<Integer, String> fun = new Function<Integer, String>() {

      public String apply(Integer message) throws Exception {
        fail("Should not be called");
        return null;
      }

    };
    a.from("s1").on(fun).go("s5");
    sm.setInputEnabled(true);
    sm.input(2);
    assertTrue("not in expected state", !sm.getState().contains(a.state("s5")));
    assertTrue("not in expected state", sm.getState().contains(a.state("s1")));
  }

  public void testAutomatonProducesSomeAvailableOutput() {
    sm.input(1);
    Object out = sm.output();
    assertTrue(out.equals(2) || out.equals(3));
  }

  public void testAutomatonRaisesExceptionIfNoOutputAvailable() {
    try {
      Object out = sm.output();
      fail("expected illegalStateException");
    } catch (IllegalStateException e) {
      // OK
    }
  }

  public void testAutomatonProducesSomeAvailableOutputInArray() {
    sm.input(1);
    Object[] outs = new Object[1];
    sm.output(outs, 0, 1);
    assertTrue(outs[0].equals(2) || outs[0].equals(3));
  }

  public void testAutomatonProducesSomeAvailableOutputInArrayAtGivenPosition() {
    sm.input(1);
    Object[] outs = new Object[3];
    sm.output(outs, 1, 1);
    assertTrue(outs[1].equals(2) || outs[1].equals(3));
  }

  public void testAutomatonOutputNothingInArrayIfLengthIs0() {
    sm.input(1);
    Object[] outs = new Object[3];
    assertEquals(0, sm.output(outs, 1, 0));
  }

  public void testAutomatonCallInternalTransitionWithFunction() {
    Function<Integer, String> fun = new Function<Integer, String>() {

      public String apply(Integer message) throws Exception {
        assertTrue(message == 2 || message == 3);
        return "toto";
      }

    };
    a.from("s3").on(fun).go("s5").from("s4").on(fun).go("s5").from("s5")
        .receive("toto").go("s1");
    sm.input(1);
    sm.output();
    assertEquals("not in expected state", Collections.singleton(a.state("s1")),
        sm.getState());

  }

  int i = 0;

  public void testAutomatonSaturateInternalTransitions() {
    Function<Integer, String> fun = new Function<Integer, String>() {

      public String apply(Integer message) throws Exception {
        i++;
        return null;
      }

    };
    a.from("s3").on(fun).go("s5").from("s4").on(fun).go("s5").from("s5")
        .on(fun).go("s1");
    sm.input(1);
    sm.output();
    assertEquals("not in expected state", Collections.singleton(a.state("s1")),
        sm.getState());
    assertEquals("number of function calls incorrect", 2, i);
  }

  public void testOutputLetterAsFunctionIsCalledWithThis() {
    Function<IOAutomaton<IOTransitionBuilder>, Object> fun = new Function<IOAutomaton<IOTransitionBuilder>, Object>() {

      public Object apply(IOAutomaton<IOTransitionBuilder> message)
          throws Exception {
        return message.getId();
      }

    };
    a.setId("myautomaton");
    a.from("s3").send(fun).go("s5").from("s4").send(fun).go("s5");
    sm.input(1);
    sm.output();
    Object o = sm.output();
    assertEquals("wrong id", "myautomaton", o);
  }

  public void testResetToInitials() {
    sm.input(1);
    assertEquals("not in expected state", Collections.singleton(a.state("s2")),
        sm.getState());
    sm.reset();
    assertEquals("not in expected state", Collections.singleton(a.state("s1")),
        sm.getState());
  }

  public void testAutomatonOutputLastInput() {
    a.from("s3").send(sm.lastInput).go("s5").from("s4").send(sm.lastInput).go(
        "s5");
    sm.input(1);
    sm.output();
    Object o = sm.output();
    assertEquals(1, o);
  }

  public void testInputEnabledAutomatonReturnsNullOnInvalidOutput() {
    sm.setInputEnabled(true);
    assertNull(sm.output());
  }

  public void testNonInputEnabledAutomatonThrowsExceptionOnInvalidOutput() {
    try {
      sm.output();
      fail("expect exception");
    } catch (IllegalStateException e) {
      // OK
    }
  }
}
