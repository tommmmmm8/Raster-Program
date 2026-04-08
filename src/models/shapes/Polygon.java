package models.shapes;

import rasterizers.Rasterizer;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Polygon implements Shape {
    private final List<Point> points = new ArrayList<>();
    private final List<Style> styles = new ArrayList<>();
    private boolean isClosed = false;

    public void addPoint(Point point, Style style) {
        points.add(point);
        styles.add(style);
    }

    public int pointCount() {
        return points.size();
    }

    public Point lastPoint() {
        return points.getLast();
    }

    public void setClosed(boolean closed) {
        isClosed = closed;
    }

    @Override
    public void draw(Rasterizer rasterizer) {
        // #TODO Change the width approach
        int width = 1;

        for (int i = 0; i < points.size() - 1; i++) {
            new Line(points.get(i), points.get(i+1), rasterizer.getDefaultColor(), styles.get(i), width).draw(rasterizer);
        }

        if (isClosed) {
            new Line(points.getLast(), points.getFirst(), rasterizer.getDefaultColor(), styles.getLast(), width).draw(rasterizer);
        }
    }

    public Polygon deepCopy() {
        Polygon copy = new Polygon();
        for (int i = 0; i < points.size(); i++) {
            copy.addPoint(new Point(points.get(i).getX(), points.get(i).getY()), styles.get(i));
        }
        copy.setClosed(isClosed);
        return copy;
    }
}
