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


/**
 * This is the base interface for displaying automatons in 
 * windows. 
 * 
 * @author bailly
 * @version 23 July 2002
 */
public interface GraphicsDisplayer extends Displayer {

	/**
	 * Asks the Displayer to redraw the Automaton on the given Graphics2D object
	 * @param a Graphics2D object
	 */
	public void draw(java.awt.Graphics2D gs);

}
