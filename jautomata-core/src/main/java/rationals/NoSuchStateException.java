/*
 * (C) Copyright 2001 Arnaud Bailly (arnaud.oqube@gmail.com),
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

/** Instances of this class are thrown by the method
 * <tt>addTransition</tt> in class <tt>Automaton</tt>
 * when an attempt is made to use a state which does
 * not belong to te right automaton.
 * @author yroos@lifl.fr
 * @version 1.0
 * @see Automaton
*/
public class NoSuchStateException extends Exception {
    /**
   * 
   */
  public NoSuchStateException() {
    super();
    // TODO Auto-generated constructor stub
  }
  /**
   * @param message
   */
  public NoSuchStateException(String message) {
    super(message);
    // TODO Auto-generated constructor stub
  }
  /**
   * @param message
   * @param cause
   */
  public NoSuchStateException(String message, Throwable cause) {
    super(message, cause);
    // TODO Auto-generated constructor stub
  }
  /**
   * @param cause
   */
  public NoSuchStateException(Throwable cause) {
    super(cause);
    // TODO Auto-generated constructor stub
  }
}
