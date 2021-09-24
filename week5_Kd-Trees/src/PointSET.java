


import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;

import java.util.ArrayList;
import java.util.TreeSet;

public class PointSET {
    // Java中的TreeSet是用TreeMap实现的，TreeMap是用的红黑树
    private final TreeSet<Point2D> points;

    // construct an empty set of points
    public PointSET() {
        points = new TreeSet<>();
    }

    // is the set empty?
    public boolean isEmpty() {
        return points.isEmpty();
    }

    // number of points in the set
    public int size() {
        return points.size();
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null)
            throw new IllegalArgumentException();
        points.add(p);
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null)
            throw new IllegalArgumentException();
        return points.contains(p);
    }

    // draw all points to standard draw
    public void draw() {
        for (Point2D p : points) {
            p.draw();
        }
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null)
            throw new IllegalArgumentException();
        ArrayList<Point2D> a = new ArrayList<>();
        for (Point2D p : points) {
            if (rect.contains(p))
                a.add(p);
        }
        return a;
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null)
            throw new IllegalArgumentException();
        if (isEmpty()) return null;
        double minDis = Double.POSITIVE_INFINITY;
        Point2D near = null;
        for (Point2D pp : points) {
            if (p.distanceSquaredTo(pp) < minDis) {
                minDis = p.distanceSquaredTo(pp);
                near = pp;
            }
        }
        return near;
    }

    // unit testing of the methods (optional)
    public static void main(String[] args) {
        PointSET ps = new PointSET();
        ps.insert(new Point2D(1, 2));
        ps.insert(new Point2D(3, 4));
        ps.insert(new Point2D(7, 8));
        System.out.println("size  " + ps.size());
        RectHV rec = new RectHV(0, 0, 5, 5);
        System.out.println("ps.contains(new Point2D(1,2));  " + ps.contains(new Point2D(1, 2)));
        System.out.println("ps.contains(new Point2D(5,6));  " + ps.contains(new Point2D(5, 6)));
        ps.draw();
        for (Point2D p : ps.range(rec)) {
            System.out.println("range  " + p.toString());
        }
        System.out.println("ps.nearest(new Point2D(10,10));  " + ps.nearest(new Point2D(10, 10)));
    }
}
