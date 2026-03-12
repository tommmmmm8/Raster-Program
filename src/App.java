import models.*;
import models.Point;
import models.Rectangle;
import models.Shape;
import rasterizers.ShapeCanvasRasterizer;
import rasterizers.Rasterizer;
import rasterizers.TrivialRasterizer;
import rasters.Raster;
import rasters.RasterBufferedImage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serial;

public class App {

    private final JPanel panel;
    private final Raster raster;
    private MouseAdapter mouseAdapter;
    private KeyAdapter keyAdapter;
    private Point startPoint;
    private Rasterizer rasterizer;
    private boolean dottedMode = false;
    private boolean isShiftPressed = false;
    private Point cursorPosition;

    private ShapeCanvas shapeCanvas;
    private ShapeCanvasRasterizer shapeCanvasRasterizer;
    private Modes currentMode;

    private enum Modes {
        LINESMODE,
        RECTANGLEMODE,
        SQUAREMODE,
        CIRCLEMODE,
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new App(800, 600).start());
    }

    public void clear(int color) {
        raster.setClearColor(color);
        raster.clear();
    }

    public void present(Graphics graphics) {
        raster.repaint(graphics);
    }

    public void start() {
        clear(0xaaaaaa);
        panel.repaint();
    }

    public App(int width, int height) {
        JFrame frame = new JFrame();

        frame.setLayout(new BorderLayout());

        frame.setTitle("Delta : " + this.getClass().getName());
        frame.setResizable(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        raster = new RasterBufferedImage(width, height);

        panel = new JPanel() {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                present(g);
            }
        };
        panel.setPreferredSize(new Dimension(width, height));

        frame.add(panel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);

        panel.requestFocus();
        panel.requestFocusInWindow();

        shapeCanvas = new ShapeCanvas();
        rasterizer = new TrivialRasterizer(Color.CYAN, raster);
        shapeCanvasRasterizer = new ShapeCanvasRasterizer(rasterizer);

//        currentMode = Modes.LINESMODE;
//        currentMode = Modes.RECTANGLEMODE;
//        currentMode = Modes.SQUAREMODE;
        currentMode = Modes.CIRCLEMODE;


        createAdapters();
        createKeyAdapter();

        panel.addMouseMotionListener(mouseAdapter);
        panel.addMouseListener(mouseAdapter);
        panel.addKeyListener(keyAdapter);
    }

    private boolean isInBounds(Point point) {
        return point != null &&
                point.getX() >= 0 && point.getY() >= 0 &&
                point.getX() < raster.getWidth() && point.getY() < raster.getHeight();
    }

    private void createAdapters() {
        mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                cursorPosition = new Point(e.getX(), e.getY());
                boolean isInBounds = isInBounds(cursorPosition);

                if (!isInBounds) {
                    System.out.println("Point is outside the bounds of the panel.");
                    return;
                }

                Shape shape = null;

                switch(currentMode) {
                    case LINESMODE:
                        if (isShiftPressed)
                            cursorPosition = alignTo45Degrees(startPoint, cursorPosition);

                        shape = new Line(startPoint, cursorPosition, dottedMode);
                        break;
                    case RECTANGLEMODE:
                        shape = new Rectangle(startPoint, cursorPosition, dottedMode);
                        break;
                    case SQUAREMODE:
                        Point endPoint = getFixedSquareEnd(startPoint, cursorPosition); // in-bounds clamped end point of the square
                        shape = new Square(startPoint, endPoint, dottedMode);
                        break;
                    case CIRCLEMODE:
                        shape = new Circle(startPoint, cursorPosition, dottedMode);
                        break;
                    default:
                        System.out.println("Unhandled mode: " + currentMode);
                        break;
                }

                raster.clear();
                shapeCanvasRasterizer.rasterizeCanvas(shapeCanvas);
                shape.draw(rasterizer);
                panel.repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                Point point2 = new Point(e.getX(), e.getY());
                boolean isInBounds = isInBounds(point2);

                if (!isInBounds) {
                    System.out.println("Point is outside the bounds of the panel.");
                    return;
                }

                Shape shape = null;

                switch (currentMode) {
                    case LINESMODE:
                        if (isShiftPressed)
                            point2 = alignTo45Degrees(startPoint, point2);

                        shape = new Line(startPoint, point2, dottedMode);
                        break;
                    case RECTANGLEMODE:
                        shape = new Rectangle(startPoint, cursorPosition, dottedMode);
                        break;
                    case SQUAREMODE:
                        Point endPoint = getFixedSquareEnd(startPoint, point2); // in-bounds clamped end point of the square
                        shape = new Square(startPoint, endPoint, dottedMode);
                        break;
                    case CIRCLEMODE:
                        shape = new Circle(startPoint, cursorPosition, dottedMode);
                        break;
                    default:
                        System.out.println("Unsupported mode: " + currentMode);
                }

                shapeCanvas.addShape(shape);
                shape.draw(rasterizer);
                panel.repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                startPoint = new Point(e.getX(), e.getY());
            }
        };
    }

    private Point getFixedSquareEnd(Point p1, Point p2) {
        int dx = p2.getX() - p1.getX(); // side length
        int dy = p2.getY() - p1.getY(); // side length

        int a = Math.max(Math.abs(dx), Math.abs(dy));

        int sX = (dx >= 0) ? 1 : -1;
        int sY = (dy >= 0) ? 1 : -1;

        int targetX = p1.getX() + sX * a;
        int targetY = p1.getY() + sY * a;

        int finalX = Math.max(0, Math.min(targetX, rasterizer.getRaster().getWidth() - 10));
        int finalY = Math.max(0, Math.min(targetY, rasterizer.getRaster().getHeight() - 10));
        Point clampedEnd = new Point(finalX, finalY);

        return new Point(targetX, targetY);
    }

    private Point alignTo45Degrees(Point start, Point end) {
        double dx = end.getX() - start.getX();
        double dy = end.getY() - start.getY();
        double rawAngle = Math.atan2(dy, dx);
//        System.out.println("Raw Angle: " + rawAngle);

        double angleSegment = Math.PI / 4; // 45 degrees in radians
        double snappedAngle = Math.round(rawAngle / angleSegment) * angleSegment;

        double maxDist = Math.max(Math.abs(dx), Math.abs(dy));
        int newX = start.getX() + (int)(maxDist * Math.cos(snappedAngle));
        int newY = start.getY() + (int)(maxDist * Math.sin(snappedAngle));

        return new Point(newX, newY);
    }

    private void createKeyAdapter() {
        keyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_C) {
                    shapeCanvas.clear();
                    clear(0xaaaaaa);
                    panel.repaint();
                } else if (e.getKeyCode() == KeyEvent.VK_CONTROL)
                    dottedMode = true;
                else if (e.getKeyCode() == KeyEvent.VK_SHIFT)
                    isShiftPressed = true;
                else
                    super.keyPressed(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_CONTROL)
                    dottedMode = false;
                else if (e.getKeyCode() == KeyEvent.VK_SHIFT)
                    isShiftPressed = false;
                else
                    super.keyReleased(e);
            }
        };
    }
}
