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