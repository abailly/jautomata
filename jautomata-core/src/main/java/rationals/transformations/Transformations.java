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

import rationals.Automaton;
import rationals.Synchronization;


/** Provides useful transformations as static methods for convenience. */
public class Transformations {

  /**
   * Compute the <em>reduced synchronization product</em> of two automata under given {@link Synchronization} relation.
   *
   * @param  a               first automaton. Must not be null.
   * @param  b               second automaton. Must not be null.
   * @param  synchronization relation to synchronize letters on. may be null in which case it defaults to {@link
   *                         rationals.DefaultSynchronization}.
   *
   * @return a new {@link Automaton} instance.
   */
  public static Automaton miniMix(Automaton a, Automaton b, Synchronization synchronization) {
    return new Reducer().transform(new Mix(synchronization).transform(a, b));
  }
}
