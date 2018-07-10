package uic.hcilab.citymeter.voronoi;

public class Line {
    Intersection.Point p1;
    Intersection.Point p2;

    public Line(Intersection.Point pA, Intersection.Point pB) {
        p1 = pA;
        p2 = pB;
    }

    public Line(double x1, double y1, double x2, double y2) {
        p1 = new Intersection.Point(x1, y1);
        p2 = new Intersection.Point(x2, y2);
    }

    public String toString() {
        return "{(" + p1.x + ", " + p1.y + ")(" + p2.x + ", " + p2.y + ")}";
    }

    public boolean equals(Object o) {
        if (o instanceof Line) {
            return ((Line) o).p1.x == p1.x && ((Line) o).p1.y == p1.y && ((Line) o).p2.x == p2.x
                    && ((Line) o).p2.y == p2.y;
        }
        return false;
    }


}
