
package com.ninetwozero.assignment4.datatypes;

import java.util.List;

import android.graphics.Point;

/**
 * Polygon class derived from http://stackoverflow.com/a/7045002/860212
 * 
 * @author theisenp
 * @author Karl Lindmark
 */

public class Polygon {

    // Polygon coodinates.
    private List<Point> points;

    /**
     * Default constructor.
     * 
     * @param List<Point> X/Y container
     */
    public Polygon(List<Point> p) {
        points = p;
    }

    /**
     * Checks if the Polygon contains a point.
     * 
     * @see "http://alienryderflex.com/polygon/"
     * @param Point The point to locate in the Polygon
     * @return boolean True/false regarding if the point is in the polygon or
     *         not
     */
    public boolean contains(Point target) {
        boolean oddTransitions = false;
        for (int i = 0, max = points.size(), j = max - 1; i < max; j = i++) {
            if ((points.get(i).y < target.y && points.get(j).y >= target.y)
                    || (points.get(j).y < target.y && points.get(i).y >= target.y)) {
                if (points.get(i).x + (target.y - points.get(i).y)
                        / (points.get(j).y - points.get(i).y) * (points.get(j).x - points.get(i).x) < target.x) {
                    oddTransitions = !oddTransitions;
                }
            }
        }
        return oddTransitions;
    }
}
