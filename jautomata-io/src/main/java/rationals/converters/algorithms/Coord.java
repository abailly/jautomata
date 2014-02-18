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
