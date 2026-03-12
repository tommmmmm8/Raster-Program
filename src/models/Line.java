package models;

import rasterizers.Rasterizer;
import rasters.Raster;

import java.awt.*;

public class Line implements Shape {

    private Point p1, p2;
    private final boolean isDotted;

    public Line(Point p1, Point p2, boolean isDotted) {
        this.p1 = p1;
        this.p2 = p2;
        this.isDotted = isDotted;
    }

    public Line(Point p1, Point p2) {
        this(p1, p2, false);
    }

    public Point getP1() {
        return p1;
    }

    public Point getP2() {
        return p2;
    }

    public boolean isDotted() {
        return isDotted;
    }

    @Override
    public void draw(Rasterizer rasterizer) {

        Color defaultColor = rasterizer.getDefaultColor();
        Raster raster = rasterizer.getRaster();

        int increment = 1;
        if (isDotted)
            increment = 7;

        if (p1.getX() == p2.getX()) { // Vertical line
            int x = p1.getX();
            int startY = Math.min(p1.getY(), p2.getY());
            int endY = Math.max(p1.getY(), p2.getY());

            for (int y = startY; y <= endY; y += increment) {
                raster.setPixel(x, y, defaultColor.getRGB());
            }
            return;
        }

        double slope = calculateSlope();
        double intercept = calculateIntercept(slope);

        if (Math.abs(slope) <= 1) {
            if (p1.getX() > p2.getX()) {
                Point temp = p1;
                p1 = p2;
                p2 = temp;
            }

            for (int x = p1.getX(); x < p2.getX(); x += increment) {
                int y = (int) (slope * x + intercept);
                raster.setPixel(x, y, defaultColor.getRGB());
            }
        } else {
            if (p1.getY() > p2.getY()) {
                Point temp = p1;
                p1 = p2;
                p2 = temp;
            }

            for (int y = p1.getY(); y < p2.getY(); y += increment) {
                int x = (int) ((y - intercept) / slope);
                raster.setPixel(x, y, defaultColor.getRGB());
            }
        }
    }

    private double calculateSlope() {
        return (double) (p2.getY() - p1.getY()) / ( p2.getX() - p1.getX() );
    }

    private double calculateIntercept(double slope) {
        return p1.getY() - slope * p1.getX();
    }
}
