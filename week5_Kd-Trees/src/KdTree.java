

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

import java.util.ArrayList;

public class KdTree {

    // 将Node设置为static，因为它没有必要用到任何外部类的实例变量和实例方法。
    // （但是可以使用外部类的静态方法和静态变量）
    // 如果Node需要设置泛型，则不能设置为static，因为内部类需要访问外部类声明的泛型
    private static class Node {
        private final Point2D p;      // the point
        private final RectHV rect;    // the axis-aligned rectangle corresponding to this node
        private Node lb;        // the left/bottom subtree
        private Node rt;        // the right/top subtree

        public Node(Point2D pp, RectHV r) {
            p = pp;
            rect = r;
            lb = null;
            rt = null;
        }
    }

    private Node root;
    private int size;
    private static final boolean VERTICAL = true;
    private double minDis;
    private Point2D nearest;

    // construct an empty set of points
    public KdTree() {
        root = null;
        size = 0;
    }

    // is the set empty?
    public boolean isEmpty() {
        return size == 0;
    }

    // number of points in the set
    public int size() {
        return size;
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null)
            throw new IllegalArgumentException();
        insert(p, root, VERTICAL);
    }

    private void insert(Point2D p, Node n, boolean orientation) {
        // 如果root为空，则先设置为root点
        if (n == null) {
            root = new Node(p, new RectHV(0, 0, 1, 1));
            size++;
            return;
        }
        // 如果遇到重复的点，就不操作
        if (n.p.equals(p)) {
            return;
        }
        // 如果这一层的点，将该区域垂直分割成两部分，则比较该层点的横坐标和p的横坐标
        if (orientation == VERTICAL) {
            // 如果p点在n节点的左边，去左子树
            if (p.x() < n.p.x()) {
                if (n.lb == null) {
                    // 节点对应的矩形，即是其父节点将它自己的矩形区域分割成两部分，左右子树节点的矩形区域分别对应这两部分
                    n.lb = new Node(p, new RectHV(n.rect.xmin(), n.rect.ymin(), n.p.x(),
                            n.rect.ymax()));
                    size++;
                }
                else
                    insert(p, n.lb, !orientation);
            }
            else {
                if (n.rt == null) {
                    n.rt = new Node(p, new RectHV(n.p.x(), n.rect.ymin(), n.rect.xmax(),
                            n.rect.ymax()));
                    size++;
                }
                else
                    insert(p, n.rt, !orientation);
            }
        }
        // 如果这一层的点，将该区域水平分割成两部分，则比较该层点的纵坐标和p的纵坐标
        else {
            if (p.y() < n.p.y()) {
                if (n.lb == null) {
                    n.lb = new Node(p, new RectHV(n.rect.xmin(), n.rect.ymin(), n.rect.xmax(),
                            n.p.y()));
                    size++;
                }
                else
                    insert(p, n.lb, !orientation);
            }
            else {
                if (n.rt == null) {
                    n.rt = new Node(p, new RectHV(n.rect.xmin(), n.p.y(), n.rect.xmax(),
                            n.rect.ymax()));
                    size++;
                }
                else
                    insert(p, n.rt, !orientation);
            }
        }
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null)
            throw new IllegalArgumentException();
        return contains(p, root, VERTICAL);
    }

    private boolean contains(Point2D p, Node n, boolean orientation) {
        if (n == null)
            return false;
        if (n.p.equals(p))
            return true;
        if (orientation == VERTICAL) {
            if (p.x() < n.p.x())
                return contains(p, n.lb, !orientation);
            else
                return contains(p, n.rt, !orientation);
        }
        else {
            if (p.y() < n.p.y())
                return contains(p, n.lb, !orientation);
            else
                return contains(p, n.rt, !orientation);
        }
    }

    // draw all points to standard draw
    public void draw() {
        draw(root, VERTICAL);
    }

    private void draw(Node n, boolean orientation) {
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        n.p.draw();
        StdDraw.setPenRadius();
        if (orientation == VERTICAL) {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.line(n.p.x(), n.rect.ymin(), n.p.x(), n.rect.ymax());
        }
        else {
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.line(n.rect.xmin(), n.p.y(), n.rect.xmax(), n.p.y());
        }
        if (n.lb != null)
            draw(n.lb, !orientation);
        if (n.rt != null)
            draw(n.rt, !orientation);
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null)
            throw new IllegalArgumentException();
        ArrayList<Point2D> a = new ArrayList<>();
        range(rect, root, a);
        return a;
    }

    private void range(RectHV rect, Node n, ArrayList<Point2D> a) {
        if (n == null)
            return;
        if (rect.contains(n.p))
            a.add(n.p);
        if (n.lb != null && n.lb.rect.intersects(rect))
            range(rect, n.lb, a);
        if (n.rt != null && n.rt.rect.intersects(rect))
            range(rect, n.rt, a);
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null)
            throw new IllegalArgumentException();
        if (isEmpty()) return null;
        minDis = Double.POSITIVE_INFINITY;
        nearest = root.p;
        return nearest(p, root);
    }

    private Point2D nearest(Point2D p, Node n) {
        if (n == null)
            return nearest;
        if (n.p.distanceSquaredTo(p) < minDis) {
            minDis = n.p.distanceSquaredTo(p);
            nearest = n.p;
        }
        // 这两个boolean主要是防止重复访问子树。尤其是当p点正好在当前节点n的分割线上时，因为两边子树都contains该点p，
        // 所以无法仅通过是否contains该点来判断是否已经访问过该节点。
        boolean seenLB = false;
        boolean seenRT = false;
        // nearest的访问顺序也需要特别注意，在测试中有测试到该问题
        // 访问顺序需要遵从：如果点p在当前节点n的某一子树的矩形区域内部，则先访问该子树；
        // 如果点p距离当前节点的某一子树的矩形区域比另一子树的矩形区域更近，则先访问更近的子树；
        // 此外，再次回到该节点，判断是否需要访问另一边子树时，若当前最短距离大于等于p点到另一边子树矩形的距离，则需要访问
        if ((n.lb != null && n.lb.rect.contains(p))
                || (n.lb != null && n.rt != null &&
                n.lb.rect.distanceSquaredTo(p) < n.rt.rect.distanceSquaredTo(p))) {
            nearest(p, n.lb);
            seenLB = true;
        }
        else if ((n.rt != null && n.rt.rect.contains(p))
                || (n.lb != null && n.rt != null &&
                n.rt.rect.distanceSquaredTo(p) < n.lb.rect.distanceSquaredTo(p))) {
            nearest(p, n.rt);
            seenRT = true;
        }
        if (n.lb != null && !seenLB && minDis >= n.lb.rect.distanceSquaredTo(p))
            nearest(p, n.lb);
        if (n.rt != null && !seenRT && minDis >= n.rt.rect.distanceSquaredTo(p))
            nearest(p, n.rt);
        return nearest;
    }

    // unit testing of the methods (optional)
    public static void main(String[] args) {
        KdTree kd = new KdTree();
        System.out.println("empty  " + kd.isEmpty());
        kd.insert(new Point2D(0.1, 0.2));
        kd.insert(new Point2D(0.3, 0.4));
        kd.insert(new Point2D(0.5, 0.6));
        kd.insert(new Point2D(0.05, 0.03));
        kd.insert(new Point2D(0.06, 0.04));
        System.out.println("size  " + kd.size());
        kd.draw();
        System.out.println("(0.1, 0.2)?  " + kd.contains(new Point2D(0.1, 0.2)));
        System.out.println("(0.5, 0.6)?  " + kd.contains(new Point2D(0.5, 0.6)));
        System.out.println("(0.05, 0.03)?  " + kd.contains(new Point2D(0.05, 0.03)));
        System.out.println("(0.06, 0.04)?  " + kd.contains(new Point2D(0.06, 0.04)));
        System.out.println("(0.1, 0.3)?  " + kd.contains(new Point2D(0.1, 0.3)));
    }
}

