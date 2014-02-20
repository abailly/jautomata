/*
 * (C) Copyright 2002 Arnaud Bailly (arnaud.oqube@gmail.com),
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
package rationals.converters;

import java.awt.Graphics2D;
import java.util.Map;

import rationals.Automaton;
import rationals.converters.algorithms.LayoutAlgorithm;

/**
 * This class is used to display an Automaton in a graphic
 * component
 * 
 * @author bailly
 * @version Jul 23, 2002
 * 
 */
public class WindowDisplayer implements GraphicsDisplayer {

	private Automaton automata;
	
	private LayoutAlgorithm algorithm;
		
	/**
	 * @see rationals.converters.GraphicsDisplayer#redraw(Graphics2D)
	 */
	public void draw(Graphics2D gs) {
		Map m = algorithm.getState();
	}

	/**
	 * @see rationals.converters.Displayer#setAutomaton(Automaton)
	 */
	public void setAutomaton(Automaton a) throws ConverterException {
		automata = a;		
	}

	/**
	 * @see rationals.converters.Displayer#display()
	 */
	public void display() throws ConverterException {
	}

	/**
	 * Returns the automata.
	 * @return Automaton
	 */
	public Automaton getAutomata() {
		return automata;
	}

	/**
	 * Sets the automata.
	 * @param automata The automata to set
	 */
	public void setAutomata(Automaton automata) {
		this.automata = automata;
	}

	/**
	 * Returns the algorithm.
	 * @return LayoutAlgorithm
	 */
	public LayoutAlgorithm getAlgorithm() {
		return algorithm;
	}

	/**
	 * Sets the algorithm.
	 * @param algorithm The algorithm to set
	 */
	public void setAlgorithm(LayoutAlgorithm algorithm) {
		this.algorithm = algorithm;
	}

}
