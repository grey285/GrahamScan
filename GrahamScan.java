// Libraries necessary for plotting the results, optional. Uncomment relevant lines of code to test, IDE required

// import java.io.IOException;
// import com.github.sh0nk.matplotlib4j.Plot;
// import com.github.sh0nk.matplotlib4j.PythonExecutionException;
// import java.util.List;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.Stack;



public class GrahamScan 
{
    public static void main( String[] args ) throws Exception {
        Set<Point> test_points_1 = generatePoints(50, 150, 150); // generate set of 50 randomly distributed points in a 2D plane with dimensions 150 x 150
        printOutput(test_points_1);

        // Testing other configurations...
        Set<Point> test_points_2 = generatePoints(10, 1000, 1000); // high ranges and a few points
        printOutput(test_points_2);

        // Set<Point> test_points_3 = generatePoints(1000, 1001, 1001); // boundary case with the critical size (relative to the x-, y- ranges)
        // printOutput(test_points_3);

        Set<Point> test_points_4 = generatePoints(110, 101, 101); // will throw an exception because the size is greater than the ranges; unstable output is possible in such cases
        printOutput(test_points_4);
    }
    
    // Generate a set of random points
    public static Set<Point> generatePoints(int size, int hrange, int vrange) throws Exception { 
        /*
        * size = number of points; 
        * hrange = range where points` x-coord. are generated;
        * vrange = range where points` y-coord. are generated;
        */   
        // handling inappropriate input                              
        if (size <= 0 || hrange <= 0 || vrange <= 0) {
            throw new Exception("Invalid input in ClosestPoints.generatePoints(). The size, as well as the x- and y- ranges must be positive; negative values entered");
        }
        if (size / hrange >= 1 || size / vrange >= 1) {
            throw new Exception("The specified parameters are out of reasonable range; please ensure that the number of generated points is not greater than the horizontal or vertical range");
        }
        // Generating random points within 'hrange' and 'vrange'
        Set<Point> points = new HashSet<Point>();
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            int x = random.nextInt(hrange);
            int y = random.nextInt(vrange);
            points.add(new Point(x, y));
        }
    return points;
    }

    // Printing the output
    public static void printOutput(Set<Point> points_set) throws Exception {
        ArrayList<Point> sorted_by_angle = generateSortedArray(points_set); // sort the points by their polar angle w.r.t. the origin point
        System.out.println("\n\n\nPoints sorted by their polar angle w.r.t. the origin point (x, y, theta):\n-------------------------------------------------------------");
        for (Point point : sorted_by_angle) {
            System.out.print(point.get_x() + ", " + point.get_y() + ", " + (Math.round(point.get_theta()*10.0)/10.0) + "   |   ");
        }
        Stack<Point> hull = grahamScan(sorted_by_angle); // calculate the convex hull using Graham Scan
        System.out.println("\n\nPoints of the convex hull contour (x, y, theta):\n-------------------------------------------------------------");
        for (Point point : hull) {
            System.out.print(point.get_x() + ", " + point.get_y() + ", " + (Math.round(point.get_theta()*10.0)/10.0) + "   |   ");
        }
        System.out.println("\n\n\n");
        // plotPoints(points_set, hull); // ---> visualization for testing purposes, IDE required
    }

    // Find the leftmost lowest point p and convert all points to polar coordinates with p as origin, and sort by angle
    public static ArrayList<Point> generateSortedArray(Set<Point> points) throws Exception { 
        Point origin = points.iterator().next(); // set initial origin
        // get the leftmost lowest point
        for (Point point : points) {
            if (point.get_y() < origin.get_y() || point.get_y() == origin.get_y() && point.get_x() < origin.get_x())
                origin = point;
        }
        // calculate the angles of each point relative to the origin
        for (Point point : points) {
            if (point == origin) 
            continue; // skip the origin
            double point_x = point.get_x() - origin.get_x();
            double point_y = point.get_y() - origin.get_y();
            point.set_theta(Math.acos((point_x) / (Math.sqrt(point_x * point_x + point_y * point_y))) * 180 / Math.PI); // calculate the polar angles and convert to deg (easier to imagine)
        }
        int size = points.size() - 1;
        ArrayList<Point> points_list = new ArrayList<>();
        // Add points from the Set of points into a new ArrayList
        for (Point point : points) 
            points_list.add(point);
        sort(points_list, 0, size); // sort the points using Merge Sort
        swap(points_list.get(0), origin); // swap the leftmost lowest point with the first one
        // Create another list of points for the purpose of handling the collinear points
        ArrayList<Point> points_list_final = new ArrayList<>();
        points_list_final.add(origin);
        // handling the collinear points
        for (int i = 1; i < points_list.size(); i++) {
            while (i < points_list.size() - 1 && direction(points_list.get(0), points_list.get(i), points_list.get(i + 1)) == 0)
                i++; // skips points with the same polar angle
            points_list_final.add(points_list.get(i)); // adds only the last one to the final points list
        }
        return points_list_final;
    }

    // Derive the convex hull
    public static Stack<Point> grahamScan(ArrayList<Point> sorted_list) { // input is the list of points sorted by the angle 
        Stack<Point> stack = new Stack<>(); 
        // Push the first three elements of the 'sorted_list' into the stack
        stack.push(sorted_list.get(0));
        stack.push(sorted_list.get(1));
        stack.push(sorted_list.get(2));
        // Iterate through the 'sorted_list' and push into the stack all points which have ccw angle w.r.t. the previous two points
        // and pop a point once a cw rotation is encountered
        for (int i = 3; i < sorted_list.size(); i++) { 
            while (direction(stack.get(stack.size()-2), stack.get(stack.size()-1), sorted_list.get(i)) == -1) { // if cw rotation
                stack.pop();
            }
            stack.push(sorted_list.get(i));
        }
        stack.add(sorted_list.get(0)); // append the first element at the end of the stack for visualization purpose
        return stack;
    }

    // Calculate the cross product of two vectors (i -> j and j -> k) to find out the rotation direction
    public static int direction(Point i, Point j, Point k) {
        double cross_product = (k.get_y() - i.get_y())*(j.get_x() - i.get_x()) - (j.get_y() - i.get_y())*(k.get_x() - i.get_x()); 
        if (cross_product > 0) return 1; // ccw rotation
        if (cross_product < 0) return -1; // cw rotation
        return 0; //no rotation
    }

    // Swap two points in an array
    public static void swap(Point a, Point b) {
        Point aux = a;
        a = b;
        b = aux;
    }

    // Merge Sort
    public static void sort(ArrayList<Point> array, int p, int r) throws Exception {
        if (p < r) { // if there are at least two points..
            int q = (int)Math.floor((p + r) / 2); // round down for the middle element
            // recursively divide the array until reaching the elementary case, and sort by merging
            sort(array, p, q);
            sort(array, q+1, r);
            merge(array, p, r, q);
        }
    }

    // Sort by merging the elementary arrays 
    public static void merge(ArrayList<Point> array, int p, int r, int q) throws Exception { // coord for differentiation between x- and y- axis sorting
        int inf = Integer.MAX_VALUE;
        int n1 = q - p + 2;
        int n2 = r - q + 1;
        // initialize the left and right half arrays
        ArrayList<Point> L = new ArrayList<>();
        for (int a = 0; a < n1; a++) {
            L.add(new Point(0, 0));}
        ArrayList<Point> R = new ArrayList<>();
        for (int b = 0; b < n2; b++) {
            R.add(new Point(0, 0));}
        // set the end element as infinity and fill the rest with (sub)array values
        for (int i = 0; i <= n1 - 2; i++) {
            L.set(i, array.get(p + i)); // adding the left element to L
            L.set(n1 - 1, new Point(inf, inf, inf)); // and the right element
        }
        for (int j = 0; j <= n2 - 2; j++) {
            R.set(j, array.get(q + j + 1)); // adding the left element to R
            R.set(n2 - 1, new Point(inf, inf, inf)); // and the right element
        }
        // sort by merging
        int i = 0;
        int j = 0;
        // sort by angle
        for (int k = p; k <= r; k++) { // compare two subarrays 
            if (L.get(i).get_theta() <= R.get(j).get_theta()) { 
                array.set(k, L.get(i)); // reset with min value
                i++;
            } else {
                array.set(k, R.get(j));
                j++;
            }
        }
    }

    // ---> Plot the points and the convex hull using the matplotlib4j library. IDE required to run
//     public static void plotPoints(Set<Point> points, Stack<Point> stack) throws PythonExecutionException, IOException {
//         List<Integer> x = new ArrayList<>();
//         List<Integer> y = new ArrayList<>();
//         List<Integer> x_hull = new ArrayList<>();
//         List<Integer> y_hull = new ArrayList<>();
// //        ArrayList<Float> points_list = new ArrayList<>();
//         for (Point point : points) {
//             x.add(point.get_x());
//             y.add(point.get_y());
//         }
//         for (Point point : stack) {
//             x_hull.add(point.get_x());
//             y_hull.add(point.get_y());
//         }
//         Plot plt = Plot.create();
//         plt.subplot(1, 2, 1);
//         plt.plot().add(x, y, "o");
//         plt.subplot(1, 2, 2);
//         plt.plot().add(x_hull, y_hull, "o").linestyle("-");
//         plt.show();
//     }
}