/**
 * 
 */
package rationals.transductions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rationals.Automaton;
import rationals.NoSuchStateException;
import rationals.State;
import rationals.Transition;
import rationals.transformations.InverseMorphism;
import rationals.transformations.Mix;
import rationals.transformations.Morphism;

/**
 * This class implements a rational transduction by using 
 * Nivat's theorem instead of computing images directly.
 * Nivat's theorem states that any rational transduction <code>t: X* -> Y*</code> 
 * may be decomposed by means of:
 * <ul>
 * <li>an alphabet Z ;</li>
 * <li> two alphabetic morphisms <code>m:Z* -> X*</code> and <code>n:Z* -> Y*</code>;</li>
 * <li> a regular language <code>K \in Z*</code>.</li>
 * </ul>   
 * Then we have:
 * <code>t(u) = n(m-1(u) \inter K)</code>.
 * 
 * @author nono
 * @see J.Berstel, "Rational Transductions and Context-free languages", Teubner B.G., 1979
 */
public class TransducerNivat extends Automaton implements Transduction {

	/*
	 * input morphism 
	 */
	private Map input = new HashMap();
	
	/*
	 * output morphism
	 */
	private Map output = new HashMap();
	
    /**
     * The label of a transition in a transducer is a couple of letters,
     * possibliy null.
     * 
     * @throws ClassCastException if transition is not a TransducerTransition
     * @see rationals.transductions.TransducerRelation
     * @see rationals.Rational#addTransition(rationals.Transition)
     */
    public void addTransition(Transition transition)
            throws NoSuchStateException {
        // check transition's object is a couple
        TransducerRelation rel = (TransducerRelation)transition.label();
        /* update input and output automata */
        input.put(transition.label(),rel.getIn());
        output.put(transition.label(),rel.getOut());
        super.addTransition(transition);
    }

	/* (non-Javadoc)
	 * @see rationals.transductions.Transduction#image(rationals.Automaton)
	 */
	public Automaton image(Automaton a) {
		Morphism m1 = new Morphism(output);
		InverseMorphism m2 = new InverseMorphism(input);
		/*
		 * compute inverse morphism
		 */
		Automaton b = m2.transform(a);
		/*
		 * compute intersection with this language
		 */
		b = new Mix().transform(b,this);
		/*
		 * compute output morphism
		 */
		b = m1.transform(b);
		return b;
	}
	
	/* (non-Javadoc)
	 * @see rationals.transductions.Transduction#image(java.util.List)
	 */
	public Automaton image(List word) {
		// create an automaton from word and pass it to image(Automaton)
		Automaton a = Automaton.labelAutomaton(word);
		return image(a);
	}
	

	/* (non-Javadoc)
	 * @see rationals.transductions.Transduction#image(java.lang.Object[])
	 */
	public Automaton image(Object[] word) {
		// create an automaton from word and pass it to image(Automaton)
		Automaton a = Automaton.labelAutomaton((List)Arrays.asList(word));
		return image(a);
	}

	/* (non-Javadoc)
	 * @see rationals.transductions.Transduction#inverse(java.util.List)
	 */
	public Set inverse(List word) {
		// TODO Auto-generated method stub
		return null;
	}

}
