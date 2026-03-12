package models;

import rasterizers.Rasterizer;
import rasters.Raster;

import java.awt.*;

public class Circle implements Shape {

    private Point center;
    private int radius;
    private boolean isDotted;

    public Circle(Point center, Point edge, boolean isDotted) {
        this.center = center;
        this.isDotted = isDotted;

        int dx = center.getX() - edge.getX();
        int dy = center.getY() - edge.getY();
        this.radius = (int) Math.sqrt(dx * dx + dy * dy);
    }

    @Override
    public void draw(Rasterizer rasterizer) {
        int increment = 1;
        if (isDotted)
            increment = 6;

        int x = 0;
        int y = radius;
        int d = 3 - (2 * radius);

        while (x <= y) {
            if (x % increment == 0)
                drawCircle(rasterizer, center.getX(), center.getY(), x, y);

            if (d < 0)
                d = d + (4 * x) + 6;
            else {
                d = d + 4 * (x - y) + 10;
                y--;
            }

            x++;
        }
    }

    private void drawCircle(Rasterizer rasterizer, int cX, int cY , int x, int y) {
        Raster raster = rasterizer.getRaster();
        int color = rasterizer.getDefaultColor().hashCode();

        raster.setPixel(cX + x, cY + y, color);
        raster.setPixel(cX + y, cY + x, color);

        raster.setPixel(cX + y, cY - x, color);
        raster.setPixel(cX + x, cY - y, color);

        raster.setPixel(cX - x, cY - y, color);
        raster.setPixel(cX - y, cY - x, color);

        raster.setPixel(cX - y, cY + x, color);
        raster.setPixel(cX - x, cY + y, color);
    }
}
