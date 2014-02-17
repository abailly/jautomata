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

/**
 * An interface for handling input messages in StateMachines. An input handler
 * associated with a state machine is passed incoming messages for further
 * processing. Its return value will be reinjected as a further input into the
 * state machine if it is not null.
 * 
 * @author nono
 * 
 */
public interface Function<I, O> {

  /**
   * A function that does nothing.
   */
  Function VOID = new Function<Object, Object>() {

    public Object apply(Object message) throws Exception {
      return null;
    }

  };

  /**
   * A function that returns its message.
   */
  Function ID = new Function<Object, Object>() {

    public Object apply(Object message) throws Exception {
      return message;
    }

  };

  /**
   * Do something with the given message.
   * 
   * @param message
   *          a message.
   * @return result of processing message. May be null.
   * @throws Exception
   */
  O apply(I message) throws Exception;
}
