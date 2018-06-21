package voronoi;

import android.graphics.Color;

import java.lang.reflect.GenericArrayType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class VoronoiLayer {

    public static int voronoiColor = Color.MAGENTA;
    public static int delaunayColor = Color.GREEN;
    public static int pointRadius = 3;

    private Triangulation dt;                   // Delaunay triangulation
    private Map<Object, Integer> colorTable;      // Remembers colors for display
    private Triangle initialTriangle;           // Initial triangle
    private static int initialSize = 10000;     // Size of initial triangle
    private Random random = new Random();       // Source of random numbers

    /**
     * Create and initialize the DT.
     */
    public VoronoiLayer () {
        initialTriangle = new Triangle(
                new Pnt(-initialSize, -initialSize),
                new Pnt( initialSize, -initialSize),
                new Pnt(           0,  initialSize));
        dt = new Triangulation(initialTriangle);
        colorTable = new HashMap<Object, Integer>();
    }

    /**
     * Add a new site to the DT.
     * @param point the site to add
     */
    public void addSite(Pnt point) {
        dt.delaunayPlace(point);
    }
//
//    /**
//     * Re-initialize the DT.
//     */
//    public void clear() {
//        dt = new Triangulation(initialTriangle);
//    }

    /**
     * Get the color for the spcified item; generate a new color if necessary.
     * @param item we want the color for this item
     * @return item's color
     */
    private int getColor (Object item) {
        if (colorTable.containsKey(item)) return colorTable.get(item);
        int color = Color.argb(100,random.nextInt(256),random.nextInt(256),random.nextInt(256));
        colorTable.put(item, color);
        return color;
    }

//    /**
//     * Draw a polygon.
//     * @param polygon an array of polygon vertices
//     * @param fillColor null implies no fill
//     */
//    public void draw (Pnt[] polygon, int fillColor) {
//        int[] x = new int[polygon.length];
//        int[] y = new int[polygon.length];
//        for (int i = 0; i < polygon.length; i++) {
//            x[i] = (int) polygon[i].coord(0);
//            y[i] = (int) polygon[i].coord(1);
//        }
//        if (fillColor != 0) {
//            Color temp = g.getColor();
//            g.setColor(fillColor);
//            g.fillPolygon(x, y, polygon.length);
//            g.setColor(temp);
//        }
//        g.drawPolygon(x, y, polygon.length);
//    }

    /* Higher Level Drawing Methods */


    /**
     * Draw all the Voronoi cells.
     */
    public List<List<Pnt>> drawAllVoronoi () {
        // Keep track of sites done; no drawing for initial triangles sites
        HashSet<Pnt> done = new HashSet<Pnt>(initialTriangle);
        List<List<Pnt>> verticesList = new ArrayList<>();
        for (Triangle triangle : dt)
            for (Pnt site: triangle) {
                if (done.contains(site)) continue;
                done.add(site);
                List<Triangle> list = dt.surroundingTriangles(site, triangle);
                List<Pnt> vertices = new ArrayList<>();
                for (Triangle tri: list)
                    vertices.add(tri.getCircumcenter());

                verticesList.add(vertices);
                //draw(vertices, getColor(site));
                //if (withSites) draw(site);
            }
         return verticesList;

    }

//    /**
//     * Draw all the empty circles (one for each triangle) of the DT.
//     */
//    public void drawAllCircles () {
//        // Loop through all triangles of the DT
//        for (Triangle triangle: dt) {
//            // Skip circles involving the initial-triangle vertices
//            if (triangle.containsAny(initialTriangle)) continue;
//            Pnt c = triangle.getCircumcenter();
//            double radius = c.subtract(triangle.get(0)).magnitude();
//            draw(c, radius, null);
//        }
//    }

}
