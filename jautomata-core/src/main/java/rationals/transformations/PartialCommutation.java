package rationals.transformations;

import rationals.Automaton;

import java.util.Set;

public class PartialCommutation implements UnaryTransformation{
    private final Set commutingLetters;

    public PartialCommutation(Set commutingLetters) {
        this.commutingLetters = commutingLetters;
    }

    public Automaton transform(Automaton a) {
        Automaton b = new Automaton();
        return a;
    };
}
