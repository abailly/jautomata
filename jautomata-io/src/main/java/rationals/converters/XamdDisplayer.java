package rationals.converters;

import java.io.FileWriter;
import java.io.IOException;

import rationals.Automaton;

public class XamdDisplayer implements Displayer {

	private Automaton automata;

	public void setAutomaton(Automaton a) throws ConverterException {
		automata = a;
	}
	public void display() throws ConverterException {
		try {
			FileWriter f = new FileWriter("/tmp/tmp.ad");
			f.write(new Xamd().toString(automata));
			f.close();
			Process q = Runtime.getRuntime().exec("ad /tmp/tmp.ad &");
			q.waitFor();
		} catch (InterruptedException ex) {
			throw new ConverterException("Xamd error");
		} catch (IOException ex) {
			throw new ConverterException("IO error");
		}
	}

	public void setAlgorithm(
		rationals.converters.algorithms.LayoutAlgorithm algo) {
	}

}