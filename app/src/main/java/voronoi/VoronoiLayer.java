package voronoi;

import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class VoronoiLayer {

    private Triangulation dt;                   // Delaunay triangulation
    private Map<Object, Integer> colorTable;      // Remembers colors for display
    private Triangle initialTriangle;           // Initial triangle
//    private static int initialSize = 90;     // Size of initial triangle

    /**
     * Create and initialize the DT.
     */
    public VoronoiLayer () {
//        initialTriangle = new Triangle(
//                new Pnt(-initialSize, -initialSize),
//                new Pnt( initialSize, -initialSize),
//                new Pnt(           0,  initialSize));
        initialTriangle = new Triangle(
                new Pnt(30, -100),
                new Pnt( 50, -100),
                new Pnt(           40,  -60));
        dt = new Triangulation(initialTriangle);
        colorTable = new HashMap<Object, Integer>();
    }

    /**
     * Add a new site to the DT.
     * @param point the site to add
     */
    public void addSite(Pnt point, int regionColor) {
        dt.delaunayPlace(point);
        colorTable.put(point, regionColor);
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
        else return Color.argb(50,100,100,100);

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
    public HashMap<List<Pnt>, Integer> drawAllVoronoi () {
        // Keep track of sites done; no drawing for initial triangles sites
        HashSet<Pnt> done = new HashSet<Pnt>(initialTriangle);
        HashMap<List<Pnt>, Integer> verticesTable = new HashMap<>();

        for (Triangle triangle : dt) {
            for (Pnt site : triangle) {
                if (done.contains(site)) continue;
                done.add(site);
                List<Triangle> list = dt.surroundingTriangles(site, triangle);
                List<Pnt> vertices = new ArrayList<>();
                for (Triangle tri : list)
                    vertices.add(tri.getCircumcenter());

                verticesTable.put(vertices,getColor(site));
            }
        }
         return verticesTable;

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
