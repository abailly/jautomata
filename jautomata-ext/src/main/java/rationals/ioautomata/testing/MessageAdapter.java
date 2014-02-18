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
package rationals.ioautomata.testing;

import rationals.ioautomata.Function;
import rationals.ioautomata.IOStateMachine;

/**
 * A class for dispatching events processed by a handler
 * 
 * @author nono
 * 
 */
public class MessageAdapter {

  private IOStateMachine machine;

  private Function fun;

  public MessageAdapter(Function inputHandler, IOStateMachine machine) {
    this.fun = inputHandler;
    this.machine = machine;
  }

  /**
   * @param in
   */
  public void handle(Object in) {
    if (fun != null) {
      Object ret = null;
      try {
        ret = fun.apply(in);
      } catch (Exception e) {
        ret = e;
      }
      if (ret != null)
        machine.input(ret);
    }
  }

}
