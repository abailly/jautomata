/**
 * 
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