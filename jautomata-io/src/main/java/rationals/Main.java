package rationals;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import rationals.converters.ConverterException;
import rationals.converters.Displayer;
import rationals.converters.Expression;
import rationals.converters.PSDisplayer;
import rationals.converters.StreamDisplayer;
import rationals.converters.algorithms.LayoutAlgorithm;
import rationals.converters.algorithms.TypeHelper;
import rationals.transformations.BinaryTransformation;
import rationals.transformations.UnaryTransformation;

public class Main {

	/** displayer to use */
	static Displayer display;

	/** algorithm to use */
	static LayoutAlgorithm algorithm;

	/** output stream */
	static OutputStream output = System.out;

	/** tweaking parameters */
	static java.util.Map params = new java.util.HashMap();

	/** transformation list */
	static java.util.List transformations = new java.util.LinkedList();

	/** binary transformation */
	static BinaryTransformation btrans;

	/** automata to output */
	static Automaton aut, aut2;

	/** params flag */
	static boolean paramlist;

	/** output parameters */
	static void outputTweak(LayoutAlgorithm algo) {
		java.util.Iterator it = algo.allParameters().entrySet().iterator();
		System.err.println("Properties for " + algo.getClass().getName());
		while (it.hasNext()) {
			java.util.Map.Entry entry = (java.util.Map.Entry) it.next();
			System.err.println(
				"\t" + entry.getKey() + " -> " + entry.getValue());
		}
		System.exit(1);
	}

	/** usage */
	static void usage() {
		System.err.println(
			"Usage   :  java -cp jauto.jar[:openjgraph.jar] rationals.Main [options] <regular expr1>");
		System.err.println("Options :  ");
		System.err.println("  -h : help (this message)");
		System.err.println("  -d <classname> : name of displayer to use");
		System.err.println("  -a <classname> : displayer algorithm to use");
		System.err.println(
			"  -o <pathname>  : output of display (-o - for stdout)");
		System.err.println(
			"  -t classname(,classname)*   : list of transformations to apply");
		System.err.println(
			"  -p name=value(,name=value)* : parameter for display algorithm");
		System.err.println(
			"  -b classname expression : apply binary transformation between expr1 and expr2");
		System.err.println(
			"  -P             : list parameters for display algorithm (requires -a )");
		System.exit(1);
	}

	/** instanciate displayer */
	static boolean createDisplayer(String clname) {
		try {
			Class cls = Class.forName(clname);
			display = (Displayer) cls.newInstance();
		} catch (Exception ex) {
			System.err.println(
				"Can't instanciate displayer " + clname + " : " + ex);
			return false;
		}
		return true;
	}

	/** instanciate algorithm */
	static boolean createAlgorithm(String clname) {
		try {
			Class cls;
			if (clname.indexOf('.') != -1) // absolute reference
				cls = Class.forName(clname);
			else
				cls =
					Class.forName("rationals.converters.algorithms." + clname);
			algorithm = (LayoutAlgorithm) cls.newInstance();
		} catch (Exception ex) {
			System.err.println(
				"Can't instanciate display algorithm " + clname + " : " + ex);
			return false;
		}
		return true;
	}

	/** create parameters list */
	static boolean createParameters(String plist) {
		StringTokenizer st = new StringTokenizer(plist, ",");
		while (st.hasMoreTokens()) {
			try {
				String tok = st.nextToken();
				String pname = tok.substring(0, tok.indexOf("="));
				String pval = tok.substring(tok.indexOf("=") + 1);
				params.put(pname, pval);
			} catch (Exception ex) {
				System.err.println("Bad parameter list :" + ex);
				return false;
			}
		}
		return true;
	}

	/** create transformations list */
	static boolean createTransformations(String tlist) {
		StringTokenizer st = new StringTokenizer(tlist, ",");
		while (st.hasMoreTokens()) {
			try {
				String tname = st.nextToken();
				Class cls;
				if (tname.indexOf('.') != -1) // absolute reference
					cls = Class.forName(tname);
				else
					cls = Class.forName("rationals.transformations." + tname);
				UnaryTransformation trans =
					(UnaryTransformation) cls.newInstance();
				transformations.add(trans);
			} catch (Exception ex) {
				System.err.println("Bad transformation list :" + ex);
				return false;
			}
		}
		return true;
	}

	/** apply parameters to algorithm */
	static void applyParameters(LayoutAlgorithm algo, Map params) {
		Iterator it = params.entrySet().iterator();
		Map ptypes = algo.allParameters();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String pname = (String) entry.getKey();
			Class cls = (Class) ptypes.get(pname);
			Object val = TypeHelper.convert(cls, (String) entry.getValue());
			if (val == null)
				continue;
			algo.tweak(pname, val);
		}
	}

	public static void main(String argv[]) throws ConverterException {
		// parse command line
		if (argv.length < 1)
			usage();
		for (int i = 0; i < argv.length; i++)
			if (argv[i].startsWith("-")) // command-line option
				switch (argv[i].charAt(1)) {
					case 'd' : // displayer class name
						if (!createDisplayer(argv[++i]))
							System.exit(1);
						break;
					case 'a' : // algorithm class name
						if (!createAlgorithm(argv[++i]))
							System.exit(1);
						break;
					case 'o' : // output file
						String ofname = argv[++i];
						if (!ofname.equals("-"))
							try {
								output =
									new java.io.PrintStream(
										new java.io.FileOutputStream(ofname));
							} catch (java.io.IOException ioex) {
								System.err.println(
									"Can't output to :"
										+ ofname
										+ " : "
										+ ioex);
								System.exit(1);
							} else
							output = System.out;
						break;
					case 'p' : // parameters list
						if (!createParameters(argv[++i]))
							System.exit(1);
						break;
					case 't' : // transformation list
						if (!createTransformations(argv[++i]))
							System.exit(1);
						break;
					case 'h' : // parameters list
						usage();
						break;
					case 'b' : // binary transformation
						String clname = argv[++i];
						String expr = argv[++i];
						try {
							Class cls;
							if (clname.indexOf('.') != -1)
								// absolute reference
								cls = Class.forName(clname);
							else
								cls =
									Class.forName(
										"rationals.transformations." + clname);
							btrans = (BinaryTransformation) (cls.newInstance());
							aut2 = new Expression().fromString(expr);
						} catch (Exception ex) {
							System.err.println(
								"Error in binary transformation : " + ex);
							System.exit(1);
						}
						break;
					case 'P' : // list of parameters for algorithm
						paramlist = true;
						break;
					default :
						System.err.println("Unknown option :" + argv[i]);
						usage();
				} else
				aut = new Expression().fromString(argv[i]);
		// apply binary transform
		if (aut2 != null && btrans != null)
			aut = btrans.transform(aut, aut2);
		// list parameters for algorithm
		if (paramlist && algorithm != null)
			outputTweak(algorithm);
		// calculate transformations
		java.util.Iterator it = transformations.iterator();
		while (it.hasNext()) {
			UnaryTransformation trans = (UnaryTransformation) it.next();
			aut = trans.transform(aut);
		}
		// apply parameters to algorithm
		if (algorithm != null)
			try {
				applyParameters(algorithm, params);
			} catch (Exception ex) {
				System.err.println(
					"Error in tweaking algorithm "
						+ algorithm.getClass().getName()
						+ " : "
						+ ex);
				System.exit(1);
			}
		// calculate display
		if (display == null)
			display = new PSDisplayer(); // default displayer
		if (display instanceof StreamDisplayer)
			try {
				((StreamDisplayer) display).setOutputStream(output);
				if (algorithm != null)
					 ((StreamDisplayer) display).setAlgorithm(algorithm);
			} catch (Exception ex) {
				System.err.println("Error in setting displayer output : " + ex);
				System.exit(1);
			}
		// done
		display.setAutomaton(aut);
		display.display();
	}

}
