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
package rationals.ioautomata;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Messages {

  private static final Pattern messagesPattern = Pattern.compile("(?:\\s*(\\w+)\\s*(->|<-)|\\^)\\s*(\\w+)\\.(\\S+)\\s*");

  /**
   * Builds a {@link Message} object from a string description.
   *
   * @param  message description of the message to build, of the form <code>a -> b.m</code> or <code>a <- b.m</code>.
   *                 The direction of the "arrow" distinguish whether this message is an input or an output. The left
   *                 identifier denotes the object whose viewpoint this message reflects. <code>a -> b.m</code> means
   *                 "in a, observe output of message m to object b".
   *
   * @return a {@link Message} instance
   *
   * @throws IllegalArgumentException if message doos not match expected pattern.
   */
  public static Message message(String message) {
    Matcher matcher = messagesPattern.matcher(message);
    if (matcher.matches()) {
      return new Message(matcher);
    }
    throw rejectMessage(message);
  }

  private static IllegalArgumentException rejectMessage(String message) {
    return new IllegalArgumentException(message + " does not match expected pattern " + messagesPattern);
  }

  public static IOTransition.IOLetter letter(String message) {
    Matcher matcher = messagesPattern.matcher(message);
    if (matcher.matches()) {
      Message built = new Message(matcher);

      if (matcher.group(2) == null) {
        return new IOTransition.IOLetter(built, IOAlphabetType.INTERNAL);
      }

      if (matcher.group(2).equals("->")) {
        return new IOTransition.IOLetter(built, IOAlphabetType.OUTPUT);
      } else {
        return new IOTransition.IOLetter(built, IOAlphabetType.INPUT);
      }
    }
    throw rejectMessage(message);
  }

  public static class Message {
    private final String from;
    private final String to;
    private final String content;

    Message(Matcher matcher) {
      String first = matcher.group(1);
      String second = matcher.group(3);
      String content = matcher.group(4);
      if (matcher.group(2) == null) {
        from = to = matcher.group(3);
      } else {
        if (matcher.group(2).equals("->")) {
          from = first;
          to = second;
        } else {
          from = second;
          to = first;
        }
      }
      this.content = content;
    }

    public Message(String from, String to, String content) {
      this.from = from;
      this.to = to;
      this.content = content;
    }

    public String getFrom() {
      return from;
    }

    public String getTo() {
      return to;
    }

    public String getContent() {
      return content;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o)
        return true;
      if ((o == null) || (getClass() != o.getClass()))
        return false;

      Message message = (Message) o;

      if (!content.equals(message.content))
        return false;
      if (!from.equals(message.from))
        return false;
      if (!to.equals(message.to))
        return false;

      return true;
    }

    @Override
    public int hashCode() {
      int result = from.hashCode();
      result = (31 * result) + to.hashCode();
      result = (31 * result) + content.hashCode();
      return result;
    }

    @Override
    public String toString() {
      if (!from.equals(to))
        return from + " -> " + to + '.' + content;
      else
        return from + '.' + content;
    }
  }
}
