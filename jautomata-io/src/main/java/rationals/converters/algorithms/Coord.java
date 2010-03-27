package rationals.converters.algorithms;

public class Coord {
	public double x, y;

	public Coord(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public double distance(Coord c) {
		return Math.sqrt((c.x - x) * (c.x - x) + (c.y - y) * (c.y - y));
	}

	public Coord vector(Coord c) {
		return new Coord(c.x - x, c.y - y);
	}

	public Coord add(Coord c) {
		x += c.x;
		y += c.y;
		return this;
	}

	public Coord sub(Coord c) {
		x -= c.x;
		y -= c.y;
		return this;
	}

	public Coord mul(double d) {
		x *= d;
		y *= d;
		return this;
	}

	public Coord div(double d) {
		x /= d;
		y /= d;
		return this;
	}

	public Coord rand(double d) {
		return new Coord(
			(d / 2) - Math.random() * d,
			(d / 2) - Math.random() * d);
	}

	public String toString() {
		return "(" + x + "," + y + ")";
	}
}
