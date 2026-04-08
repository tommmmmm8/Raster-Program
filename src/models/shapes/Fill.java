package models.shapes;

import rasterizers.Rasterizer;

import java.awt.*;
import java.util.Stack;

import static utils.BoundsChecker.isInBounds;

public class Fill implements Shape {
    private Point initialPoint;
    private Color targetColor;
    private Color fillColor;

    public Fill(Point initialPoint, Color targetColor, Color fillColor) {
        this.initialPoint = initialPoint;
        this.targetColor = targetColor;
        this.fillColor = fillColor;
    }

    @Override
    public void draw(Rasterizer r) {
        if (targetColor == fillColor) return;

        Stack<Point> points = new Stack<>();
        points.push(initialPoint);

        while (!points.empty()) {
            Point point = points.pop();
            int x = point.getX();
            int y = point.getY();

            if (isInBounds(r.getRaster(), point) && r.getRaster().getPixel(x, y) == targetColor.getRGB()) {
                r.getRaster().setPixel(x, y, fillColor.getRGB());

                points.push(new Point(x - 1, y)); // Left
                points.push(new Point(x + 1, y)); // Right
                points.push(new Point(x, y + 1)); // Top
                points.push(new Point(x, y - 1)); // Bottom
            }
        }
    }
}
