package rasterizers;

import models.shapes.Line;
import models.shapes.Style;
import models.shapes.Point;
import rasters.Raster;

import java.awt.*;

public class TrivialRasterizer implements Rasterizer {

    private Color defaultColor = Color.RED;
    private Raster raster;

    public TrivialRasterizer(Color defaultColor, Raster raster) {
        this.defaultColor = defaultColor;
        this.raster = raster;
    }

    @Override
    public void setColor(Color color) {
        defaultColor = color;
    }

    @Override
    public void setRaster(Raster raster) {
        this.raster = raster;
    }

    @Override
    public void rasterize(Line line) {
        if (line.getP1().getX() == line.getP2().getX()) { // Vertical line
            int x = line.getP1().getX();
            int startY = Math.min(line.getP1().getY(), line.getP2().getY());
            int endY = Math.max(line.getP1().getY(), line.getP2().getY());

            for (int y = startY; y <= endY; y++) {
                raster.setPixel(x, y, defaultColor.getRGB());
            }
            return;
        }

        double slope = calculateSlope(line);
//        System.out.println("P1 Y: " + line.getP1().getY() + ", P2 Y: " + line.getP2().getY());
//        System.out.println("Slope: " + slope);
        double intercept = calculateIntercept(line.getP1(), slope);

        int increment = 1;
        if (line.getStyle() == Style.DOTTED)
            increment = 7;

        if (Math.abs(slope) <= 1) {
            if (line.getP1().getX() > line.getP2().getX())
                line = new Line(line.getP2(), line.getP1(), line.getColor(),line.getStyle(), line.getWidth());

            for (int x = line.getP1().getX(); x < line.getP2().getX(); x += increment) {
                int y = (int) (slope * x + intercept);
                raster.setPixel(x, y, defaultColor.getRGB());
            }
        } else {
            if (line.getP1().getY() > line.getP2().getY())
                line = new Line(line.getP2(), line.getP1(), line.getColor(), line.getStyle(), line.getWidth());

            for (int y = line.getP1().getY(); y < line.getP2().getY(); y += increment) {
                int x = (int) ((y - intercept) / slope);
                raster.setPixel(x, y, defaultColor.getRGB());
            }
        }
    }

    private double calculateSlope(Line line) {
        return (double) (line.getP2().getY() - line.getP1().getY()) / ( line.getP2().getX() - line.getP1().getX() );
    }

    private double calculateIntercept(Point p, double slope) {
        return p.getY() - slope * p.getX();
    }

    public Color getDefaultColor() {
        return defaultColor;
    }

    public Raster getRaster() {
        return raster;
    }
}
