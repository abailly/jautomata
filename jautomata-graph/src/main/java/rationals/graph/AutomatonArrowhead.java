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
package rationals.graph;

import salvo.jesus.graph.visual.Arrowhead;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.Serializable;

/**
 * An implementation of Arrowhead for Automaton
 * 
 * This Arrowhead is shaped like traditional arrowheads.
 * 
 * @author Arnaud Bailly
 * @version 31082002
 */
public class AutomatonArrowhead implements Serializable, Arrowhead {
  Line2D stem;

  Point head;

  Point base1, base2, arrowmedian;

  final double arrowsize = 8.0;

  private Polygon shape = null;

  public Shape getShape(Line2D line, Point intersection) {
    //System.out.println("[AutomatonArrowhead] getShape");
    if (shape != null)
      return shape;
    // create polygon and add head point
    stem = line;
    head = intersection;
    shape = new Polygon();
    shape.addPoint(head.x, head.y);

    Point2D.Double edgeto;
    double dy, dx, distance;

    dy = stem.getY2() - stem.getY1();
    dx = stem.getX2() - stem.getX1();

    edgeto = new Point2D.Double(stem.getX2(), stem.getY2());
    distance = edgeto.distance(stem.getX1(), stem.getY1());
    distance = distance == 0 ? 1 : distance;

    arrowmedian = new Point((int) (head.getX() - dx * arrowsize / distance),
        (int) (head.getY() - dy * arrowsize / distance));

    base1 = new Point((int) (arrowmedian.getX() - dy * (arrowsize / 2)
        / distance), (int) (arrowmedian.getY() + dx * (arrowsize / 2)
        / distance));
    shape.addPoint(base1.x, base1.y);

    shape.addPoint((int) (head.getX() - dx * arrowsize / distance / 2),
        (int) (head.getY() - dy * arrowsize / distance / 2));

    base2 = new Point((int) (arrowmedian.getX() + dy * (arrowsize / 2)
        / distance), (int) (arrowmedian.getY() - dx * (arrowsize / 2)
        / distance));
    shape.addPoint(base2.x, base2.y);
    return shape;
  }
}

