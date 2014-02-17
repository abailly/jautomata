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
package rationals.ioautomata;

import rationals.State;
import rationals.Transition;


/**
A transition that distinguishes input, output and internal letters. This class takes care of relabelling of letters
according to their type:

<ul>
  <li>An input action is prefixed with '?'</li>
  <li>An output action is prefixed with '!'</li>
  <li>An internal action is prefixed with '^'</li>
</ul>

A helper method {@see canSynchronizeWith(IOTransition)}is provided that synchronizes input/output pairs.

@author  nono
@version $Id: IOTransition.java 2 2006-08-24 14:41:48Z oqube $
 */
public class IOTransition extends Transition {

  private IOAlphabetType type;

  public static class IOLetter {

    public final Object label;

    public final IOAlphabetType type;

    public IOLetter(Object lbl, IOAlphabetType t) {
      this.label = lbl;
      this.type = t;
    }

    public static IOLetter send(Object message) {
      return new IOLetter(message, IOAlphabetType.OUTPUT);
    }

    public static IOLetter receive(Object message) {
      return new IOLetter(message, IOAlphabetType.INPUT);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
      IOLetter lt = (IOLetter) obj;
      if (lt == null)
        return false;
      return (type == lt.type) && ((label == null) ? (lt.label == null) : label.equals(lt.label));
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
      return label.hashCode() ^ (type.ordinal() << 8);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    public String toString() {
      StringBuffer sb = new StringBuffer();
      switch (type) {

      case INPUT:
        sb.append('?');
        break;

      case OUTPUT:
        sb.append('!');
        break;

      case INTERNAL:
        sb.append('^');
        break;
      }
      sb.append(label);
      return sb.toString();
    }
  }

  /**
   * @param start
   * @param label
   * @param end
   */
  public IOTransition(State start, Object label, State end, IOAlphabetType type) {
    super(start, new IOLetter(label, type), end);
    this.type = type;
  }

  /**
   * This constructor should be used only for the purpose of completing an IOautomaton, hence its restricted access.
   *
   * @param q      start state
   * @param object the label. Must be an IOLetter or a ClassCastException will be thrown
   * @param q2     end state
   */
  public IOTransition(State q, Object object, State q2) {
    this(q, ((IOLetter) object).label, q2, ((IOLetter) object).type);
  }

  /** @return Returns the type. */
  public IOAlphabetType getType() {
    return type;
  }

}
