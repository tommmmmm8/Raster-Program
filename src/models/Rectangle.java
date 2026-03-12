package models;

import rasterizers.Rasterizer;

import java.util.ArrayList;
import java.util.List;

public class Rectangle implements Shape {

    private Point p1, p2; // Upper left corner; Bottom right corner
    private boolean isDotted;

    public Rectangle(Point p1, Point p2) {
        this.p1 = p1;
        this.p2 = p2;
        this.isDotted = false;
    }

    public Rectangle(Point p1, Point p2, boolean isDotted) {
        this.p1 = p1;
        this.p2 = p2;
        this.isDotted = isDotted;
    }

    @Override
    public void draw(Rasterizer rasterizer) {

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
        rectangleLines.add(new Line(p1, new Point(p2.getX(), p1.getY()), isDotted));
        rectangleLines.add(new Line(p1, new Point(p1.getX(), p2.getY()), isDotted));
        rectangleLines.add(new Line(new Point(p1.getX(), p2.getY()), p2, isDotted));
        rectangleLines.add(new Line(new Point(p2.getX(), p1.getY()), p2, isDotted));

        for (Line line : rectangleLines)
            line.draw(rasterizer);

    }
}
