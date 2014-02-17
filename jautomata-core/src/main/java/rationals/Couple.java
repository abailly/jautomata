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

public class Couple {

    private final int hash;
    
  public final State from;

  public final State to;

  public Couple(State from, State to) {
    this.from = from;
    this.to = to;
    this.hash = (from.hashCode() << 16) ^ to.hashCode();
  }

  public State getFrom() {
    return from;
  }

  public State getTo() {
    return to;
  }

  public boolean equals(Object o) {
    if ((o != null) && (o instanceof Couple)) {
      Couple c = (Couple) o;
      return from.equals(c.from) && to.equals(c.to);
    }
    return false;
  }

  public int hashCode() {
    return hash;
  }
}

/*
 * $Log: Couple.java,v $
 * Revision 1.2  2004/09/21 11:50:28  bailly
 * added interface BinaryTest
 * added class for testing automaton equivalence (isomorphism of normalized automata)
 * added computation of RE from Automaton
 * Revision 1.1 2004/07/19 06:39:02 bailly made Automaton,
 * State and Transition subclasses of Graph API modified StateFactory API
 *  
 */