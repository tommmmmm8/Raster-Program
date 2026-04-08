package models.shapes;

import rasterizers.Rasterizer;
import rasters.Raster;

import java.awt.*;

public class Circle extends BaseShape {
    private Point center;
    private int radius;

    public Circle(Point center, Point edge, Color color, Style style, int width)  {
        super(color, style, width);
        this.center = center;
        int dx = center.getX() - edge.getX();
        int dy = center.getY() - edge.getY();
        this.radius = (int) Math.sqrt(dx * dx + dy * dy);
    }

    @Override
    public void draw(Rasterizer rasterizer) {
        prepareRasterizer(rasterizer);
        
        int halfWidth = (width - 1) / 2;
        for (int w = -halfWidth; w <= width / 2; w++) {
            drawBresenhamCircle(rasterizer, radius + w);
        }
    }

    private void drawBresenhamCircle(Rasterizer rasterizer, int r) {
        int increment = 1;
        if (style == Style.DOTTED)
            increment = 6;

        int pixelCount = 0;

        int x = 0;
        int y = r;
        int d = 3 - (2 * r);

        while (x <= y) {
            if (x % increment == 0)
                if (style != Style.DASHED || pixelCount % 20 < 10)
                    drawCircle(rasterizer, center.getX(), center.getY(), x, y);

            if (d < 0)
                d = d + (4 * x) + 6;
            else {
                d = d + 4 * (x - y) + 10;
                y--;
            }

            pixelCount++;
            x++;
        }
    }

    private void drawCircle(Rasterizer rasterizer, int cX, int cY , int x, int y) {
        Raster raster = rasterizer.getRaster();
        int color = rasterizer.getDefaultColor().getRGB();

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
