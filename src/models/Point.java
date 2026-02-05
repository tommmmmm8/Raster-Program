package models;

public class Point {

    private int x, y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean hasLowerX(Point that) {
        return this.getX() < that.getX();
    }

    public boolean hasLowerY(Point that) {
        return this.getY() < that.getY();
    }
}
