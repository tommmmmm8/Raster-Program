package models.shapes;

import rasterizers.Rasterizer;
import rasters.Raster;

import java.awt.*;

public class Line extends BaseShape {
    private Point p1, p2;

    public Line(Point p1, Point p2, Color color, Style style, int width) {
        super(color, style, width);
        this.p1 = p1;
        this.p2 = p2;
    }

    public Line(Point p1, Point p2, Color color) {
        this(p1, p2, color, Style.NORMAL, 1);
    }

    public Point getP1() {
        return p1;
    }

    public Point getP2() {
        return p2;
    }

    @Override
    public void draw(Rasterizer rasterizer) {
        prepareRasterizer(rasterizer);
        Raster raster = rasterizer.getRaster();

        int increment = 1;
        if (style == Style.DOTTED)
            increment = 7;

        int pixelCount = 0;

        if (p1.getX() == p2.getX()) { // Vertical line
            int x = p1.getX();
            int startY = Math.min(p1.getY(), p2.getY());
            int endY = Math.max(p1.getY(), p2.getY());
            int startX = x - (width - 1) / 2;
            int endX = x + width / 2;

            for (int y = startY; y <= endY; y += increment) {
                if (style != Style.DASHED || pixelCount % 20 < 10) {
                    for (int currX = startX; currX <= endX; currX++) { // Width loop
                        raster.setPixel(currX, y, color.getRGB());
                    }
                }
                pixelCount++;
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
                if (style != Style.DASHED || pixelCount % 20 < 10) {
                    int startY = y - (width - 1) / 2;
                    int endY = y + width / 2;

                    for (int currY = startY; currY <= endY; currY++) {
                        raster.setPixel(x, currY, color.getRGB());
                    }
                }
                pixelCount++;
            }
        } else {
            if (p1.getY() > p2.getY()) {
                Point temp = p1;
                p1 = p2;
                p2 = temp;
            }

            for (int y = p1.getY(); y < p2.getY(); y += increment) {
                int x = (int) ((y - intercept) / slope);
                if (style != Style.DASHED || pixelCount % 20 < 10) {
                    int startX = x - (width - 1) / 2;
                    int endX = x + width / 2;

                    for (int currX = startX; currX <= endX; currX++) {
                        raster.setPixel(currX, y, color.getRGB());
                    }
                }
                pixelCount++;
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
