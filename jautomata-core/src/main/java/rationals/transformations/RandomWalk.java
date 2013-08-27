/*****************************************************************************
 * Copyright 2013 (C) Codehaus.org                                                *
 * ------------------------------------------------------------------------- *
 * Licensed under the Apache License, Version 2.0 (the "License");           *
 * you may not use this file except in compliance with the License.          *
 * You may obtain a copy of the License at                                   *
 *                                                                           *
 * http://www.apache.org/licenses/LICENSE-2.0                                *
 *                                                                           *
 * Unless required by applicable law or agreed to in writing, software       *
 * distributed under the License is distributed on an "AS IS" BASIS,         *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *
 * See the License for the specific language governing permissions and       *
 * limitations under the License.                                            *
 *****************************************************************************/
package rationals.transformations;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import rationals.Automaton;
import rationals.Transition;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;


/**
Randomly walk an {@link rationals.Automaton} until some condition is met.

<p>A random walk start from the initial(s) state of the given automaton and repeatedly builds a word that, firing a
transition, moving to given state, until the accumulated word matches some predicate. The condition is given as a
parameter of the random walk.</p>
 */
public class RandomWalk {

  private final Random random = new Random();

  public List<Object> walk(Automaton a, Matcher<List<Object>> condition) {
    Set states = a.initials();
    List<Object> word = new ArrayList<Object>();

    while (true) {
      Object l = selectALetter(a, states);
      word.add(l);
      states = a.step(states, l);
      if (condition.matches(word)) {
        break;
      }
    }

    return word;
  }

  private Object selectALetter(Automaton a, Set states) {
    Set<Transition> delta = a.delta(states);

    if (delta.isEmpty()) {
      throw new IllegalStateException("cannot find a transition");
    }

    Object selected = null;
    int ln = random.nextInt(delta.size());
    for (Transition transition : delta) {
      if (ln-- == 0) {
        selected = transition.label();
        break;
      }
    }
    return selected;
  }

  public static Matcher<List<Object>> hasLength(final int length) {
    return new TypeSafeMatcher<List<Object>>() {
      @Override
      protected boolean matchesSafely(List<Object> objects) {
        return objects.size() == length;
      }

      @Override
      public void describeTo(Description description) {
        description.appendText("a word of length " + length);
      }
    };
  }
}
