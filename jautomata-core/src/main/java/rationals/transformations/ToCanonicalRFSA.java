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
package rationals.transformations;


import rationals.Automaton;
import rationals.Builder;
import rationals.Transition;

public class ToCanonicalRFSA<L, Tr extends Transition<L>, T extends Builder<L, Tr, T>> implements UnaryTransformation<L, Tr, T> {

  public Automaton<L, Tr, T> transform(Automaton<L, Tr, T> a) {
    Reverser<L, Tr, T> r = new Reverser<>();
    ToC<L, Tr, T> c = new ToC<>() ;
    return c.transform(r.transform(c.transform(r.transform(a)))) ;
  }
}
