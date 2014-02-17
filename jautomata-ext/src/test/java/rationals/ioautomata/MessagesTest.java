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

import org.fest.assertions.Assertions;
import org.junit.Test;

public class MessagesTest {

    @Test
    public void canBuildAnFullInternalLetterWithToFromAndContentDefined() throws Exception {
        IOTransition.IOLetter letter = Messages.fullLetter("^a->b.m");

        Assertions.assertThat(letter.label.toString()).isEqualTo("a -> b.m");
        Assertions.assertThat(letter.type).isEqualTo(IOAlphabetType.INTERNAL);
    }

    @Test
    public void canBuildAnFullInternalLetterWithReverseArrow() throws Exception {
        IOTransition.IOLetter letter = Messages.fullLetter("^a<-b.m");

        Assertions.assertThat(letter.label.toString()).isEqualTo("b -> a.m");
        Assertions.assertThat(letter.type).isEqualTo(IOAlphabetType.INTERNAL);
    }

    @Test
    public void canBuildAnFullInternalLetterWithoutArrow() throws Exception {
        IOTransition.IOLetter letter = Messages.fullLetter("^b.m");

        Assertions.assertThat(letter.label).isEqualTo(Messages.message("^b.m"));
        Assertions.assertThat(letter.type).isEqualTo(IOAlphabetType.INTERNAL);
    }

    @Test
    public void canBuildAnFullOutputLetterWithFullContent() throws Exception {
        IOTransition.IOLetter letter = Messages.fullLetter("!a->b.m");

        Assertions.assertThat(letter.label).isEqualTo(Messages.message("a->b.m"));
        Assertions.assertThat(letter.type).isEqualTo(IOAlphabetType.OUTPUT);
    }

    @Test
    public void canBuildAnFullOutputLetterWithReversedArrow() throws Exception {
        IOTransition.IOLetter letter = Messages.fullLetter("!a<-b.m");

        Assertions.assertThat(letter.label).isEqualTo(Messages.message("b->a.m"));
        Assertions.assertThat(letter.type).isEqualTo(IOAlphabetType.OUTPUT);
    }

    @Test
    public void canBuildAnFullInputLetterWithFullContent() throws Exception {
        IOTransition.IOLetter letter = Messages.fullLetter("? a->b.m");

        Assertions.assertThat(letter.label).isEqualTo(Messages.message("a->b.m"));
        Assertions.assertThat(letter.type).isEqualTo(IOAlphabetType.INPUT);
    }

    @Test
    public void canBuildAnFullInputLetterWithReversedArrow() throws Exception {
        IOTransition.IOLetter letter = Messages.fullLetter("?a<-  b.m");

        Assertions.assertThat(letter.label).isEqualTo(Messages.message("b->a.m"));
        Assertions.assertThat(letter.type).isEqualTo(IOAlphabetType.INPUT);
    }

}
