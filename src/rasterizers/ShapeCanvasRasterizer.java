package rasterizers;

import models.shapes.Shape;
import models.ShapeCanvas;

public class ShapeCanvasRasterizer {

    private Rasterizer rasterizer;

    public ShapeCanvasRasterizer(Rasterizer rasterizer) {
        this.rasterizer = rasterizer;
    }

    public void rasterizeCanvas(ShapeCanvas shapeCanvas) {
        for (Shape shape : shapeCanvas.getShapes()) {
//            rasterizer.rasterize(line);
            shape.draw(rasterizer);
        }
    }
}
