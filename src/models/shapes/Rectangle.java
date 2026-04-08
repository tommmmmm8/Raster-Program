package models.shapes;

import rasterizers.Rasterizer;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Rectangle extends BaseShape {
    private Point p1, p2; // Upper left corner; Bottom right corner

    public Rectangle(Point p1, Point p2, Color color, Style style, int width) {
        super(color, style, width);
        this.p1 = p1;
        this.p2 = p2;
    }

    public Rectangle(Point p1, Point p2, Color color) {
        this(p1, p2, color, Style.NORMAL, 1);
    }

    @Override
    public void draw(Rasterizer rasterizer) {
        Color defaultColor = rasterizer.getDefaultColor();

        // p1.x,p1.y -> p2.x,p1.y
        // p1.x,p1.y -> p1.x,p2.y
        // p1.x,p2.y -> p2.x,p2.y
        // p2.x, p1.y -> p2.x, p2.y

        /* Lines
        * TR,TL
        * TR,BR
        * TL,BL
        * BL, BR
         */

//        Point TR = p1.getX() > p2.getX() ? p1 : p2;
//        Point TL = p1.getX() < p2.getX() ? p1 : p2;
//        Point BR = p1.getX() > p2.getX() ? new Point(p1.getX(), )
//        Point BL;

        List<Line> rectangleLines = new ArrayList<>();
        rectangleLines.add(new Line(p1, new Point(p2.getX(), p1.getY()), color, style, width));
        rectangleLines.add(new Line(p1, new Point(p1.getX(), p2.getY()), color, style, width));
        rectangleLines.add(new Line(new Point(p1.getX(), p2.getY()), p2, color, style, width));
        rectangleLines.add(new Line(new Point(p2.getX(), p1.getY()), p2, color, style, width));

        for (Line line : rectangleLines)
            line.draw(rasterizer);

    }
}
