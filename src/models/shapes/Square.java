package models.shapes;

import rasterizers.Rasterizer;
import rasters.Raster;

import java.util.ArrayList;
import java.util.List;

import java.awt.*;

public class Square extends BaseShape {
    private Point p1, p2;

    public Square(Point p1, Point p2, Color color, Style style, int width) {
        super(color, style, width);
        this.p1 = p1;
        this.p2 = p2;
    }

    public Square(Point p1, Point p2, Color color, int width) {
        this(p1, p2, color, Style.NORMAL, width);
    }

    @Override
    public void draw(Rasterizer rasterizer) {
        prepareRasterizer(rasterizer);
        Point topEdge = new Point(p2.getX(), p1.getY());
        Point verticalEdge = new Point(p1.getX(), p2.getY());
        Point diagonal = new Point(p2.getX(), p2.getY());

        List<Line> squareLines = new ArrayList<>();
        squareLines.add(new Line(p1, topEdge, color, style, width));
        squareLines.add(new Line(p1, verticalEdge, color, style, width));
        squareLines.add(new Line(verticalEdge, diagonal, color, style, width));
        squareLines.add(new Line(topEdge, diagonal, color, style, width));

        for (Line line : squareLines)
            line.draw(rasterizer);
    }
}
