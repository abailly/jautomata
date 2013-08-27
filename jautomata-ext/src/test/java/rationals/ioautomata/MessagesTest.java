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
