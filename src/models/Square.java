package models;

import rasterizers.Rasterizer;
import rasters.Raster;

import java.util.ArrayList;
import java.util.List;

public class Square implements Shape {

    private Point p1, p2;
    private boolean isDotted;

    public Square(Point p1, Point p2, boolean isDotted) {
        this.p1 = p1;
        this.p2 = p2;
        this.isDotted = isDotted;
    }

    public Square(Point p1, Point p2) {
        this(p1, p2, false);
    }

    @Override
    public void draw(Rasterizer rasterizer) {
        Point topEdge = new Point(p2.getX(), p1.getY());
        Point verticalEdge = new Point(p1.getX(), p2.getY());
        Point diagonal = new Point(p2.getX(), p2.getY());

        List<Line> squareLines = new ArrayList<>();
        squareLines.add(new Line(p1, topEdge, isDotted));
        squareLines.add(new Line(p1, verticalEdge, isDotted));
        squareLines.add(new Line(verticalEdge, diagonal, isDotted));
        squareLines.add(new Line(topEdge, diagonal, isDotted));

        for (Line line : squareLines)
            line.draw(rasterizer);
    }

    private boolean isInBounds(Point point, Raster raster) {
        return point.getX() >= 0 && point.getY() >= 0 &&
                point.getX() < raster.getWidth() && point.getY() < raster.getHeight();
    }
}
