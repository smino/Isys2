public class  Point {

    private int x;

    private int y;

    private double value;

    private PointType type;

    private Point[][] sector;

    Point(int x, int y, PointType type, Point[][] sector) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.sector = sector;
    }

    Point(int x, int y, double value) {
        this.x = x;
        this.y = y;
        this.value = value;
    }

    int getX() {
        return this.x;
    }

    int getY() {
        return this.y;
    }

    @Override
    public String toString () {
        return "(" + this.x + ", " + this.y + ")";
    }

    PointType getType() {
        return this.type;
    }

    public double getValue() {
        return this.value;
    }

    public Point[][] getSector() {
        return this.sector;
    }

    public void setSectorValue (int x, int y, Point toInsert) {
        this.sector[x][y] = toInsert;
    }

}
