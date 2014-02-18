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
package rationals.converters;

import rationals.Automaton;
import rationals.converters.algorithms.LayoutAlgorithm;

/**
 * This is the base inteface for all devices used to display automata.
 * This interface is used to display a graph representation of an 
 * Automaton object, not an encoding of its structure. It should 
 * not be confused with the interface {@see rationals.converters.StreamEncoder}
 * which used to serialize the semantics of the Automaton.
 * 
 * @author bailly
 * @version 23072002
 */ 
public interface Displayer {

	/**
	 * Defines the Automaton to display with this displayer
	 * 
	 * @param an Automaton object
	 */
	public void setAutomaton(Automaton a) throws ConverterException;

	/**
	 * Defines the algorithm to use for layout
	 * 
	 * @param an LyaoutAlgorithm object. May not be null.
	 */
	public void setAlgorithm(
		LayoutAlgorithm algo);

	/**
	 * Asks the displayer to display the automaton 
	 * 
	 */
	public void display() throws ConverterException;

}
