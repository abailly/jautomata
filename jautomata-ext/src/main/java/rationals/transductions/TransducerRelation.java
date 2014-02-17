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
package rationals.transductions;

public final class TransducerRelation {

	final Object in;

	final Object out;

	private int h;
	
	public TransducerRelation(Object in, Object out) {
		this.in = in;
		this.out = out;
		// precompute hashcode for efficiency (?)
		this.h = hash();
	}

	/**
	 * @return Returns the o1.
	 */
	public Object getIn() {
		return in;
	}

	/**
	 * @return Returns the o2.
	 */
	public Object getOut() {
		return out;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		TransducerRelation r = (TransducerRelation) obj;
		return (in == null ? r.in == null : in.equals(r.in))
				&& (out == null ? r.out == null : out.equals(r.out));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return h;
	}

	private int hash() {
		return (in == null ? 0xfefe0000 : in.hashCode() << 16)
				| (out == null ? 0x0000fefe : out.hashCode() & 0x0000ffff);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return (in == null ? "1" : in.toString()) + "/"
				+ (out == null ? "1" : out.toString());
	}
}