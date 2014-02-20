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


/**
 * This displayer sub-interface is aimed at devices generating output
 * to files, such as Postscript, SVG or graphics files.
 * 
 * @author bailly
 * @version 23072002
 */
public interface StreamDisplayer extends Displayer {

	/**
	 * Defines the output stream to use by this displayer
	 * 
	 * @param a java.io.OutputStream object
	 */
	public void setOutputStream(java.io.OutputStream os)
		throws java.io.IOException;

	/**
	 * Defines a file name to generate output to.
	 * 
	 * @param a String naming a file on the file system
	 */
	public void setOutputFileName(String fname) throws java.io.IOException;

	
}
