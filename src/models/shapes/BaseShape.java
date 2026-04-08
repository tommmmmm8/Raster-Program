package models.shapes;

import rasterizers.Rasterizer;

import java.awt.*;

public abstract class BaseShape implements Shape {
    protected Color color;
    protected Style style;
    protected int width;

    public BaseShape(Color color, Style style, int width) {
        this.color = color;
        this.style = style;
        this.width = Math.max(width, 1);
    }

    public Color getColor() {
        return color;
    }

    public Style getStyle() {
        return style;
    }

    public int getWidth() {
        return width;
    }

    protected void prepareRasterizer(Rasterizer r) {
        r.setColor(this.color);
    }
}
