// Class for definition of a 2D point
public class Point {
    private int x, y;
    // polar coordinates
    private double theta; // angle
    private double rho; // distance

    // Constructors..
    Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    Point(int x, int y, double theta) {
        this.x = x;
        this.y = y;
        this.theta = theta;
    }

    // point`s coordinate values getters
    public int get_x() 
    {return x;}
    public int get_y() 
    {return y;}
    public double get_theta()
    {return theta;}
    public void set_theta(double theta) // setter for angle
    {this.theta = theta;}
    public double get_rho()
    {return rho;}
}