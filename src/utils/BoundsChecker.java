package utils;

import models.shapes.Point;
import rasters.Raster;

import java.awt.image.BufferedImage;

public class BoundsChecker {

    public static boolean isInBounds(Raster raster, Point point) {
        return point.getX() >= 0 && point.getY() >= 0 &&
                point.getX() < raster.getWidth() && point.getY() < raster.getHeight();
    }

    public static boolean isInBounds(BufferedImage img, Point point) {
        return point.getX() >= 0 && point.getY() >= 0 &&
                point.getX() < img.getWidth() && point.getY() < img.getHeight();
    }
}
