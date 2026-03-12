package models;

import java.util.ArrayList;
import java.util.List;

public class ShapeCanvas {

    private List<Shape> shapes;

    public ShapeCanvas() {
        shapes = new ArrayList<>();
    }

    public void addShape(Shape shape) {
        shapes.add(shape);
    }

    public List<Shape> getShapes() {
        return new ArrayList<>(shapes);
    }

    public void clear() {
        shapes.clear();
    }
}
