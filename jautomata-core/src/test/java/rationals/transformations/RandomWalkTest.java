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
package rationals.transformations;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import rationals.Automaton;
import rationals.converters.Expression;

import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static rationals.transformations.RandomWalk.hasLength;


public class RandomWalkTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  private RandomWalk walk = new RandomWalk();

  @Test
  public void canGenerateARandomWordMatchingAGivenLengthPredicate() throws Exception {
    Automaton a = new Expression().fromString("a(bbb)*");

    List<Object> word = walk.walk(a, hasLength(10));

    assertThat(word).hasSize(10);
  }

  @Test
  public void throwsExceptionWhenConditionIsNotMetAndNoTransitionCanBeFired() throws Exception {
    expectedException.expect(IllegalStateException.class);

    Automaton a = new Expression().fromString("ab");

    walk.walk(a, hasLength(3));
  }

}
