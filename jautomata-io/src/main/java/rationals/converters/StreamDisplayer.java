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
