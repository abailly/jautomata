/**
 *  Copyright (C) 2007 - OQube / Arnaud Bailly
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA

 Created 1 juin 07
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
