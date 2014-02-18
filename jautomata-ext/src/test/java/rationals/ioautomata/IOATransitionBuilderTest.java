/*
 * (C) Copyright 2013 Arnaud Bailly (arnaud.oqube@gmail.com),
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
package rationals.ioautomata;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;


public class IOATransitionBuilderTest {

  private IOAutomaton<IOTransition, IOATransitionBuilder> ioa;

  @Before
  public void setup() {
    IOAutomaton<IOTransition, IOATransitionBuilder> ioa1 = IOATransitionBuilder.automaton("b");
    ioa1.state("init").setInitial(true);
    ioa1.state("fini").setTerminal(true);
    ioa = ioa1;
  }

  @Test
  public void buildsIntermediaryStateWhenSendAndReceiveMessagesAreChained() throws Exception {
    ioa.from("init").send("a.m").receive("a.m'").go("fini");

    assertThat(ioa.states()).hasSize(3);
    assertThat(ioa.accept(list(Messages.letter("b->a.m"), Messages.letter("b<-a.m'")))).isTrue();
  }

  @Test
  public void buildsIntermediaryStateWhenInternalActionIsChainedWithSend() throws Exception {
    ioa.from("init").send("a.m").on("m'").go("fini");

    assertThat(ioa.states()).hasSize(3);
    assertThat(ioa.accept(list(Messages.letter("b->a.m"), Messages.letter("^b.m'")))).isTrue();
  }

  @Test
  public void doesNotBuildIntermediaryStateWhenLooping() throws Exception {
    ioa.from("init").on("m'").loop().send("a.m").go("fini");

    assertThat(ioa.states()).hasSize(2);
    assertThat(ioa.accept(list(Messages.letter("^b.m'"), Messages.letter("^b.m'"), Messages.letter("b->a.m")))).isTrue();
  }

  @Test
  public void changeCurrentStateOnEnd() throws Exception {
    ioa.from("init").send("a.m").go("fini").to("init")
            .receive("a.m'").go("fini");

    assertThat(ioa.states()).hasSize(2);
    assertThat(ioa.accept(list(Messages.letter("b<-a.m'")))).isTrue();
  }

  private static List<Object> list(Object... ts) {
    return Arrays.asList(ts);
  }

}
