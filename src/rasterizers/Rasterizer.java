package rasterizers;

import models.shapes.Line;
import rasters.Raster;

import java.awt.*;

public interface Rasterizer {

    void setColor(Color color);

    void setRaster(Raster raster);

    void rasterize(Line line);

    Raster getRaster();

    Color getDefaultColor();

}
