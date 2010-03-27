package rationals.converters;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import rationals.Automaton;
import rationals.State;
import rationals.Transition;
import rationals.converters.algorithms.Coord;
import rationals.converters.algorithms.LayoutAlgorithm;
import rationals.converters.algorithms.SimulatedAnnealing;

/**
 * This Displayer subclass produces a postscript file from an
 * automaton. <p>
 * It uses a LayoutAlgorithm object to produce its output<p>.
 *
 * @author Arnaud Bailly
 * @version 22032002
 */
public class PSDisplayer implements StreamDisplayer {

	private double ratio;

	private double maxHeight = ((29.7 /2.54) * 72) ;

	private double maxWidth = ((21.0 /2.54) * 72);

	private static String header =
		"%!PS-Adobe-2.0 EPSF-2.0 \n"
			+ "%%BoundingBox:\n"
			+ "%%Creator:autops\n"
			+ "%%Title:\n"
			+ "%%CreationDate:\n"
			+ "%%EndComments\n"
			+ "\n"
			+ "/autodict \n"
			+ "%%dictionnaire\n"
			+ "dict def \n"
			+ "autodict begin\n"
			+ "\n"
			+ "% transforme des coordonnees alg en polaire\n"
			+ "% relativement a x0 y0\n"
			+ "% x0 y0 x1 y1 polaire norme cos(v) sin(v)\n"
			+ "/polaire { \n"
			+ "exch 4 -1 roll 4 copy              % y0 y1 x1 x0 y0 y1 x1 x0\n"
			+ "sub dup mul                      % y0 y1 (x1 - x0)2\n"
			+ "3 -2 roll exch\n"
			+ "sub dup mul                      % xv yv\n"
			+ "add sqrt                         % || v ||\n"
			+ "5 1 roll                         % |v| yo y1 x1 x0\n"
			+ "sub 3 index div                  % |v| yo y1 cos(v)\n"
			+ "4 1 roll exch                    % cos(v) |v| y1 y0\n"
			+ "sub 1 index  div         % cos(v) |v| sin(v)\n"
			+ "exch 3 1 roll} bind def   \n"
			+ "\n"
			+ "% calcule une nouvelle matrice de transformation a partir\n"
			+ "% des coordonnees polaires relatives a deux etats \n"
			+ "% cos(v) sin(v) x0 y0 transformCTM matrice\n"
			+ "/transformCTM {\n"
			+ "3 index 3 index neg exch        % cos sin x y -sin cos\n"
			+ "4 2 roll               % cos sin -sin cos x y\n"
			+ "6 array astore } bind def\n"
			+ "\n"
			+ "% affiche une double fleche de sortie\n"
			+ "% sur l'etat xi yi a l'angle cos sin\n"
			+ "% cos sin x y sortie -\n"
			+ "/sortie {\n"
			+ "gsave \n"
			+ "transformCTM concat \n"
			+ "newpath\n"
			+ "20 3 moveto 6 0 rlineto \n"
			+ "3 -3 rlineto -3 -3 rlineto -6 0 rlineto \n"
			+ "stroke\n"
			+ "grestore } bind def\n"
			+ "\n"
			+ "% affiche un etat \n"
			+ "% (nom-etat) x y etat -\n"
			+ "/etat {\n"
			+ "1 index 1 index\n"
			+ "newpath 20 0 360 arc stroke\n"
			+ "exch 5 sub exch\n"
			+ "moveto show } bind def \n"
			+ "\n"
			+ "% affiche un etat initial \n"
			+ "% (nom-etat) x y etat_init -\n"
			+ "/etat_init {\n"
			+ "gsave\n"
			+ "1 index 1 index\n"
			+ "newpath 20 0 360 arc fill\n"
			+ "exch 5 sub exch\n"
			+ "moveto 1 setgray show \n"
			+ "grestore} bind def \n"
			+ "\n"
			+ "% affiche un etat final \n"
			+ "% (nom-etat) x y etat_final -\n"
			+ "/etat_final {\n"
			+ "1 index 1 index\n"
			+ "newpath 20 0 360 arc stroke\n"
			+ "% trace de la sortie a 45 degres\n"
			+ "1 index 1 index                % x y x y\n"
			+ "1 index 20 add 1 index 20 add  % x y x y x+20 y+20\n"
			+ "polaire 3 -1 roll pop          % x y cos sin\n"
			+ "3 index 3 index                % x y cos sin x y\n"
			+ "sortie\n"
			+ "exch 5 sub exch\n"
			+ "moveto show } bind def \n"
			+ "\n"
			+ "% affiche un etat final et initial\n"
			+ "% (nom-etat) x y etat_init_final -\n"
			+ "/etat_init_final {\n"
			+ "gsave\n"
			+ "1 index 1 index\n"
			+ "newpath 20 0 360 arc fill\n"
			+ "% trace de la sortie a 45 degres\n"
			+ "1 index 1 index                % x y x y\n"
			+ "1 index 20 add 1 index 20 add  % x y x y x+20 y+20\n"
			+ "polaire 3 -1 roll pop          % x y cos sin\n"
			+ "3 index 3 index                % x y cos sin x y\n"
			+ "sortie\n"
			+ "exch 5 sub exch\n"
			+ "moveto 1 setgray show grestore} bind def \n"
			+ "\n"
			+ "\n"
			+ "% trace une pointe de fleche a l'extremite du segment \n"
			+ "% passe en parametre \n"
			+ "% xa ya xb yb fleche -\n"
			+ "/fleche {\n"
			+ "3 index 3 index 4 -2 roll     % xa ya xa ya xb yb\n"
			+ "gsave\n"
			+ "polaire                       % xa ya |v| cosv sinv\n"
			+ "5 -2 roll transformCTM concat % |v|\n"
			+ "newpath\n"
			+ "0 moveto -6 2 rlineto 2 -2 rlineto -2 -2 rlineto closepath fill\n"
			+ "grestore } bind def \n"
			+ "\n"
			+ "% trace une transition entre qi et qj \n"
			+ "% sous la forme d'un arc aplati avec une fleche a son extremite\n"
			+ "% la lettre representant la transition est affichee au sommet de l'arc\n"
			+ "% string-lt string-i xi yi string-j xj yj  transition -\n"
			+ "/transition_courbe {\n"
			+ "6 -1 roll pop\n"
			+ "3 -1 roll pop             % lt xi yi xj yj\n"
			+ "3 index 3 index 7 2 roll  % xi yi lt xi yi xj yj\n"
			+ "gsave                     % sauvegarde de l'etat graphique\n"
			+ "polaire                   % xi yi lt |v| cos sin\n"
			+ "6 -2 roll                 % lt |v| cos sin xi yi\n"
			+ "transformCTM concat       % lt |v| le systeme de coordonnees est modifie\n"
			+ "newpath\n"
			+ "0 0 moveto\n"
			+ "dup 2 div dup 10 exch 10 % lt |v| |v|/3 20 2|v|/3 20\n"
			+ "4 index 20 sub 0              % lt |v| x1 y1 x2 y2 x3 y3 \n"
			+ "20 0 rmoveto curveto           % lt |v| \n"
			+ "currentpoint stroke              % la transition est tracee\n"
			+ "2 index  2 div 10 4 2 roll    % lt |v| xm ym xa ya\n"
			+ "fleche                        % trace la fleche a l'arrivee\n"
			+ "2 div 12 moveto               % le milieu du texte est positionne\n"
			+ "dup stringwidth               % calcule la taille de lt\n"
			+ "pop 2 div neg 0 rmoveto \n"
			+ "show \n"
			+ "grestore } bind def  \n"
			+ "\n"
			+ "% trace une transition entre qi et qj \n"
			+ "% sous la forme d'un segment avec une fleche a son extremite\n"
			+ "% la lettre representant la transition est affichee au dessus du segment\n"
			+ "% string-lt string-i xi yi string-j xj yj  transition -\n"
			+ "/transition_droite {\n"
			+ "6 -1 roll pop\n"
			+ "3 -1 roll pop             % lt xi yi xj yj\n"
			+ "3 index 3 index 7 2 roll  % xi yi lt xi yi xj yj\n"
			+ "gsave                     % sauvegarde de l'etat graphique\n"
			+ "polaire                   % xi yi lt |v| cos sin\n"
			+ "6 -2 roll                 % lt |v| cos sin xi yi\n"
			+ "transformCTM concat       % lt |v| le systeme de coordonnees est modifie\n"
			+ "newpath\n"
			+ "0 0 moveto\n"
			+ "20 0 rmoveto dup 20 sub 0 lineto\n"
			+ "stroke\n"
			+ "20 0 2 index 20 sub 0 fleche\n"
			+ "2 div 5 moveto \n"
			+ "dup stringwidth pop 2 div neg 0 rmoveto\n"
			+ "show \n"
			+ "grestore } bind def  \n"
			+ "\n"
			+ "% affiche une transition d'un etat sur lui_meme\n"
			+ "% perpendiculairement a un segment\n"
			+ "% string-lt string-i xi yi string-j xj yj transition -\n"
			+ "/auto_transition {\n"
			+ "6 -1 roll pop\n"
			+ "3 -1 roll pop             % lt xi yi xj yj\n"
			+ "3 index 3 index 7 2 roll  % xi yi lt xi yi xj yj\n"
			+ "gsave                     % sauvegarde de l'etat graphique\n"
			+ "polaire                   % xi yi lt |v| cos sin\n"
			+ "6 -2 roll                 % lt |v| cos sin xi yi\n"
			+ "transformCTM concat       % lt |v| le systeme de coordonnees est modifie\n"
			+ "newpath\n"
			+ "0 20 moveto\n"
			+ "-15 40 15 40 currentpoint curveto stroke\n"
			+ "10 40 0 20 fleche\n"
			+ "0 40 moveto pop show\n"
			+ "grestore } bind def\n"
			+ "\n"
			+ "%%defetat\n"
			+ "\n"
			+ "end\n"
			+ "%%EndProlog\n"
			+ "autodict begin\n"
			+ "\n"
			+ "%%translation\n"
			+ "translate \n"
			+ "%%scaling\n"
			+ "scale \n"
			+ "/Helvetica findfont 10 scalefont setfont   \n"
			+ "0.5 setlinewidth\n"
			+ "\n"
			+ "%%etats\n"
			+ "\n"
			+ "%%transitions\n"
			+ "\n"
			+ "end\n"
			+ "showpage\n"
			+ "%%Trailer\n";

	private static Map tokenMap = new java.util.HashMap();

	static {
		try {
			tokenMap.put(
				"%%BoundingBox:",
				PSDisplayer.class.getMethod("outputBBox", null));
			tokenMap.put(
				"%%dictionnaire",
				PSDisplayer.class.getMethod("outputDict", null));
			tokenMap.put(
				"%%translation",
				PSDisplayer.class.getMethod("outputTranslation", null));
			tokenMap.put(
				"%%scaling",
				PSDisplayer.class.getMethod("outputScaling", null));
			tokenMap.put(
				"%%defetat",
				PSDisplayer.class.getMethod("outputDefetat", null));
			tokenMap.put(
				"%%etats",
				PSDisplayer.class.getMethod("outputEtats", null));
			tokenMap.put(
				"%%transitions",
				PSDisplayer.class.getMethod("outputTransitions", null));
		} catch (Exception ex) {
			System.err.println(
				"Error in output functions initialization :" + ex);
		}
	}

	/** width of display */
	private double width = 200;

	/** height of display */
	private double height = 200;

	private double border = 40;

	private long minx = Integer.MAX_VALUE;
	private long maxx = Integer.MIN_VALUE;
	private long miny = Integer.MAX_VALUE;
	private long maxy = Integer.MIN_VALUE;

	/** display algorithm to use */
	LayoutAlgorithm algo;

	/** print stream to use */
	PrintStream ps;

	/** map of states to coord */
	Map statesCoord;

	Automaton automata;

	/////////////////////////////////////////////////:
	// CONSTRUCTORS
	/////////////////////////////////////////////////

	/**
	 * Constructs a default PSDisplayer. 
	 */
	public PSDisplayer() {
		algo = new SimulatedAnnealing();
		ps = System.out;
	}

	/////////////////////////////////////////////////////:
	// PUBLIC METHODS FOR OUTPUT
	/////////////////////////////////////////////////////

	public void outputBBox() {
		Iterator it = statesCoord.entrySet().iterator();
		minx = Integer.MAX_VALUE;
		maxx = Integer.MIN_VALUE;
		miny = Integer.MAX_VALUE;
		maxy = Integer.MIN_VALUE;
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			State s = (State) entry.getKey();
			Coord c = (Coord) entry.getValue();
			long ix = Math.round(c.x);
			long iy = Math.round(c.y);
			if (ix < minx)
				minx = ix;
			if (ix > maxx)
				maxx = ix;
			if (iy < miny)
				miny = iy;
			if (iy > maxy)
				maxy = iy;
		}
		width = maxx - minx + 2 * border;
		height = maxy - miny + 2 * border;
		double xratio = maxWidth/width;
		double yratio = maxHeight/height;
		ratio = xratio < yratio ? xratio : yratio;
		ratio = ratio > 1 ? 1 : ratio;
		ps.println(
			"%%BoundingBox: 0  0 "
				+ Math.round(width*ratio)
				+ " "
				+ Math.round(height*ratio));
	}

	public void outputDict() {
		ps.println("%%dictionnaire");
		ps.println(10 + statesCoord.size()); // ?????
	}

	public void outputTranslation() {
		ps.println("%%translation");
		ps.println("0 " + Math.round(height*ratio)); // ?????
	}

	public void outputScaling() {
		ps.println("%%scaling");
		/* rescale only if greater than requested - keep aspect ratio */
		ps.println(ratio  +"  " + ratio); // ?????		
	}
	
	public void outputDefetat() {
		ps.println("%%defetat");
		Iterator it = statesCoord.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			State s = (State) entry.getKey();
			Coord c = (Coord) entry.getValue();
			ps.println(
				"/q"
					+ s
					+ " { (q"
					+ s
					+ ") "
					+ Math.round(c.x - minx + border)
					+ " "
					+ (Math.round(c.y - maxy - border))
					+ " } bind def");
		}
	}

	public void outputEtats() {
		ps.println("%%defetat");
		Iterator it = statesCoord.keySet().iterator();
		while (it.hasNext()) {
			State s = (State) it.next();
			if (s.isInitial() && s.isTerminal())
				ps.println("q" + s + " etat_init_final ");
			else if (s.isTerminal())
				ps.println("q" + s + " etat_final ");
			else if (s.isInitial())
				ps.println("q" + s + " etat_init ");
			else
				ps.println("q" + s + " etat ");
		}
	}

	public void outputTransitions() {
		ps.println("%%transitions");
		Iterator it = statesCoord.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			State s = (State) entry.getKey();
			Coord cs = (Coord) entry.getValue();
			Iterator it2 = automata.delta(s).iterator();
			all_trans : while (it2.hasNext()) {
				Transition t = (Transition) it2.next();
				State s2 = t.end();
				Coord cs2 = (Coord) statesCoord.get(s2);
				Object lbl = t.label();
				if (s2.equals(s)) {
					ps.println(
						"("
							+ lbl
							+ ") q"
							+ s
							+ " (a) "
							+ Math.round(minx - cs.x + border)
							+ " "
							+ Math.round(cs.y - maxy + border)
							+ " auto_transition");
				} else {
					Iterator it3 = automata.delta(s2).iterator();
					while (it3.hasNext()) {
						Transition t2 = (Transition) it3.next();
						State s3 = (t2.end());
						if (s3.equals(s)) {
							ps.println(
								"("
									+ t.label()
									+ ") q"
									+ s
									+ " q"
									+ s2
									+ " transition_courbe");
							continue all_trans;
						}
					}
					ps.println(
						"("
							+ t.label()
							+ ") q"
							+ s
							+ " q"
							+ s2
							+ " transition_droite");
				}
			}
		}
	}

	/**
	 * output data
	 */
	private void output() {
		StringTokenizer st = new StringTokenizer(header, "\n\r");
		while (st.hasMoreTokens()) {
			String tok = st.nextToken();
			java.lang.reflect.Method m =
				(java.lang.reflect.Method) tokenMap.get(tok);
			if (m != null)
				try {
					m.invoke(this, null);
				} catch (java.lang.reflect.InvocationTargetException ex) {
					System.err.println(
						"Caught exception : " + ex.getTargetException());
					ex.printStackTrace();
				} catch (IllegalAccessException aex) {
					System.err.println("Caught exception : " + aex);
				} else
				ps.println(tok);
		}
	}

	///////////////////////////////////////////////////////
	// IMPLEMENTATION OF OUTDISPLAYER
	///////////////////////////////////////////////////////

	public void display() throws ConverterException {
		try {
			algo.layout(automata);
			while (!algo.done())
				algo.work();
			statesCoord = algo.getState();
			output();
			ps.close();
		} catch (Exception ex) {
			throw new ConverterException("Error :" + ex);
		}
	}

	public void setAutomaton(Automaton a) {
		automata = a;
	}

	public void setAlgorithm(
		rationals.converters.algorithms.LayoutAlgorithm algo) {
		this.algo = algo;
	}

	public void setOutputStream(java.io.OutputStream os)
		throws java.io.IOException {
		ps = new java.io.PrintStream(os);
	}

	public void setOutputFileName(String fname) throws java.io.IOException {
		ps = new java.io.PrintStream(new java.io.FileOutputStream(fname));
	}

	/**
	 * Sets the output size in 1/72 inches
	 * @param width
	 * @param height
	 */
	public void setOutputFormat(int width,int height){
		this.maxWidth = width;
		this.maxHeight = height;
	}
} 
