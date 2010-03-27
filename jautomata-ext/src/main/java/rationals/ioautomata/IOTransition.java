/*______________________________________________________________________________
 * 
 * Copyright 2005 Arnaud Bailly - NORSYS/LIFL
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * (1) Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 * (2) Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in
 *     the documentation and/or other materials provided with the
 *     distribution.
 *
 * (3) The name of the author may not be used to endorse or promote
 *     products derived from this software without specific prior
 *     written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Created on 31 mars 2005
 *
 */
package rationals.ioautomata;

import rationals.State;
import rationals.Transition;

/**
 * A transition that distinguishes input, output and internal letters. This
 * class takes care of relabelling of letters according to their type:
 * <ul>
 * <li>An input action is prefixed with '?'</li>
 * <li>An output action is prefixed with '!'</li>
 * <li>An internal action is prefixed with '^'</li>
 * </ul>
 * A helper method {@see canSynchronizeWith(IOTransition)}is provided that
 * synchronizes input/output pairs.
 * 
 * @author nono
 * @version $Id: IOTransition.java 2 2006-08-24 14:41:48Z oqube $
 */
public class IOTransition extends Transition {

	private IOAlphabetType type;

	public static class IOLetter {

		public final Object label;

		public final IOAlphabetType type;

		public IOLetter(Object lbl, IOAlphabetType t) {
			this.label = lbl;
			this.type = t;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		public boolean equals(Object obj) {
			IOLetter lt = (IOLetter) obj;
			if (lt == null)
				return false;
			return type == lt.type
					&& (label == null ? lt.label == null : label
							.equals(lt.label));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#hashCode()
		 */
		public int hashCode() {
			return label.hashCode() ^ (type.ordinal() << 8);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			StringBuffer sb = new StringBuffer();
			switch (type) {
			case INPUT:
				sb.append('?');
				break;
			case OUTPUT:
				sb.append('!');
				break;
			case INTERNAL:
				sb.append('^');
				break;
			}
			sb.append(label);
			return sb.toString();
		}
	}

	/**
	 * @param start
	 * @param label
	 * @param end
	 */
	public IOTransition(State start, Object label, State end,
			IOAlphabetType type) {
		super(start, new IOLetter(label, type), end);
		this.type = type;
	}

	/**
	 * This constructor should be used only for the purpose of completing an
	 * IOautomaton, hence its restricted access.
	 * 
	 * @param q
	 *            start state
	 * @param object
	 *            the label. Must be an IOLetter or a ClassCastException will be
	 *            thrown
	 * @param q2
	 *            end state
	 */
	IOTransition(State q, Object object, State q2) {
		this(q, ((IOLetter) object).label, q2, ((IOLetter) object).type);
	}

	/**
	 * @return Returns the type.
	 */
	public IOAlphabetType getType() {
		return type;
	}

}