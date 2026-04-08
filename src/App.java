import models.*;
import models.shapes.*;
import models.shapes.Point;
import models.shapes.Polygon;
import models.shapes.Rectangle;
import models.shapes.Shape;
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
import java.util.Arrays;
import java.util.Stack;

public class App {
    private final JPanel panel;
    private Rasterizer rasterizer;
    private final Raster raster;
    private MouseAdapter mouseAdapter;
    private KeyAdapter keyAdapter;

    private Point startPoint;
    private boolean isShiftPressed = false;
    private Point cursorPosition;

    private ShapeCanvas shapeCanvas;
    private ShapeCanvasRasterizer shapeCanvasRasterizer;
    private Mode activeMode;
    private Mode[] shapeModes = {Mode.LINE, Mode.RECTANGLE, Mode.SQUARE, Mode.CIRCLE, Mode.POLYGON};
    private Style currentStyle = Style.NORMAL;
    private Color activeColor = Color.CYAN;
    private Color activeFillColor = Color.YELLOW;

    private Polygon activePolygon;

    private JComboBox<Style> stylePicker;
    private int selectedWidth = 1;

    private enum Mode {
        LINE,
        RECTANGLE,
        SQUARE,
        CIRCLE,
        POLYGON,
        FILL
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

    private void setMode(Mode mode) {
        if (activeMode == Mode.FILL)
            panel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        activeMode = mode;
        if (activeMode == Mode.FILL)
            panel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    }

    public void fill(Point initialPoint, Color targetColor, Color fillColor) {
        if (targetColor == fillColor) return;

        Stack<Point> points = new Stack<>();
        points.push(initialPoint);

        while (!points.empty()) {
            Point point = points.pop();
            int x = point.getX();
            int y = point.getY();
            if (isInBounds(point) && raster.getPixel(x, y) == targetColor.getRGB()) {
                raster.setPixel(x, y, fillColor.getRGB());

                points.push(new Point(x - 1, y)); // Left
                points.push(new Point(x + 1, y)); // Right
                points.push(new Point(x, y + 1)); // Top
                points.push(new Point(x, y - 1)); // Bottom
            }
        }
    }

    public App(int width, int height) {
        JFrame frame = new JFrame();

        frame.setLayout(new BorderLayout());

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.setTitle("Delta : " + this.getClass().getName());

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

        // -------
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT)) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(super.getPreferredSize().width, 60);
            }
        };

        // Create buttons
        JButton btnLine = createModeBtn("Line", Mode.LINE);
        JButton btnSquare = createModeBtn("Square", Mode.SQUARE);
        JButton btnRect = createModeBtn("Rectangle", Mode.RECTANGLE);
        JButton btnCircle = createModeBtn("Circle", Mode.CIRCLE);
        JButton btnPoly = createModeBtn("Polygon", Mode.POLYGON);

        addButtons(toolbar, new JButton[]{btnLine, btnSquare, btnRect, btnCircle, btnPoly});

        // Add toolbar to the top of the screen
        frame.add(toolbar, BorderLayout.NORTH);

        // Add color button
        JButton btnColor = new JButton("Color");
        btnColor.setBackground(Color.CYAN);
        btnColor.addActionListener(e -> {
            Color selectedColor = JColorChooser.showDialog(
                    frame,
                    "Choose Line Color",
                    rasterizer.getDefaultColor()
            );

            if (selectedColor != null) {
//                rasterizer.setColor(selectedColor);
                activeColor = selectedColor;
                btnColor.setBackground(selectedColor);
            }
        });
        btnColor.setFocusable(false);
        toolbar.add(btnColor);

        // Add style selection
        Style[] styles = { Style.NORMAL, Style.DOTTED, Style.DASHED };
        stylePicker = new JComboBox<>(styles);
        stylePicker.addActionListener(e -> {
            currentStyle = (Style) stylePicker.getSelectedItem();
        });
        stylePicker.setFocusable(false);
        toolbar.add(new JLabel("Style:"));
        toolbar.add(stylePicker);

        // Add bucket tool button
        JButton btnBucket = new JButton("Fill Mode");
        btnBucket.addActionListener(e -> {
            setMode(Mode.FILL);
        });
        btnBucket.setFocusable(false);
        toolbar.add(btnBucket);

        // Add fill color picker
        JButton btnFillColor = new JButton("Fill Color");
        btnFillColor.setBackground(Color.YELLOW);
        btnFillColor.addActionListener(e -> {
            Color selectedFillColor = JColorChooser.showDialog(
                    frame,
                    "Choose Fill Color",
                    activeFillColor
            );

            if (selectedFillColor != null) {
                activeFillColor = selectedFillColor;
                btnFillColor.setBackground(selectedFillColor);
            }
        });
        btnFillColor.setFocusable(false);
        toolbar.add(btnFillColor);

        JComboBox<Integer> widthPicker = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10});
        widthPicker.setSelectedItem(selectedWidth);
        widthPicker.addActionListener(e -> {
            selectedWidth = (Integer) widthPicker.getSelectedItem() != null ? (Integer)widthPicker.getSelectedItem() : 1;
            System.out.println(selectedWidth);
        });
        widthPicker.setFocusable(false);
        toolbar.add(new JLabel("Width:"));
        toolbar.add(widthPicker);

        // -------

        panel.requestFocus();
        panel.requestFocusInWindow();

        shapeCanvas = new ShapeCanvas();
        rasterizer = new TrivialRasterizer(Color.CYAN, raster);
        shapeCanvasRasterizer = new ShapeCanvasRasterizer(rasterizer);
        activePolygon = new Polygon();

        setMode(Mode.LINE);

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

    private boolean isShapeMode(Mode mode) {
        return Arrays.asList(shapeModes).contains(mode);
    }

    private void createAdapters() {
        mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (!isShapeMode(activeMode)) return;

                cursorPosition = new Point(e.getX(), e.getY());
                boolean isInBounds = isInBounds(cursorPosition);

                if (!isInBounds) {
                    System.out.println("Point is outside the bounds of the panel.");
                    return;
                } else if (activeMode == Mode.POLYGON)
                    return;

                Shape shape = null;

                switch(activeMode) {
                    case LINE:
                        if (isShiftPressed)
                            cursorPosition = alignTo45Degrees(startPoint, cursorPosition);
                        shape = new Line(startPoint, cursorPosition, activeColor, currentStyle, selectedWidth);
                        break;
                    case RECTANGLE:
                        shape = new Rectangle(startPoint, cursorPosition, activeColor, currentStyle, selectedWidth);
                        break;
                    case SQUARE:
                        Point endPoint = getFixedSquareEnd(startPoint, cursorPosition); // in-bounds clamped end point of the square
                        shape = new Square(startPoint, endPoint, activeColor, currentStyle, selectedWidth);
                        break;
                    case CIRCLE:
                        shape = new Circle(startPoint, cursorPosition, activeColor, currentStyle, selectedWidth);
                        break;
                    default:
                        System.out.println("Unhandled mode: " + activeMode);
                        break;
                }

                if (shape != null)
                    redraw(shape, true);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (!isShapeMode(activeMode)) return;

                Point point2 = new Point(e.getX(), e.getY());
                boolean isInBounds = isInBounds(point2);

                if (!isInBounds) {
                    System.out.println("Point is outside the bounds of the panel.");
                    return;
                } else if (activeMode == Mode.POLYGON)
                    return;

                Shape shape = null;

                switch (activeMode) {
                    case LINE:
                        if (isShiftPressed)
                            point2 = alignTo45Degrees(startPoint, point2);
                        shape = new Line(startPoint, point2, activeColor, currentStyle, selectedWidth);
                        break;
                    case RECTANGLE:
                        shape = new Rectangle(startPoint, cursorPosition, activeColor, currentStyle, selectedWidth);
                        break;
                    case SQUARE:
                        Point endPoint = getFixedSquareEnd(startPoint, point2); // in-bounds clamped end point of the square
                        shape = new Square(startPoint, endPoint, activeColor, currentStyle, selectedWidth);
                        break;
                    case CIRCLE:
                        shape = new Circle(startPoint, cursorPosition, activeColor, currentStyle, selectedWidth);
                        break;
                    default:
                        System.out.println("Unsupported mode: " + activeMode);
                }

                shapeCanvas.addShape(shape);
                redraw(shape, false);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (activeMode == Mode.FILL) {
                    Point point = new Point(e.getX(), e.getY());
                    int pixelColor = raster.getPixel(point.getX(), point.getY());
                    Color targetColor = new Color(pixelColor, true);

                    shapeCanvas.addShape(new Fill(point, targetColor, activeFillColor));
                    redraw(null, true);
                }
                else if (activeMode == Mode.POLYGON) {
                    if (SwingUtilities.isRightMouseButton(e) && activePolygon.pointCount() >= 3) {
                        System.out.println("Has more than 3 points");
                        // end the polygon
                        activePolygon.setClosed(true);
                        shapeCanvas.addShape(activePolygon.deepCopy());
                        activePolygon = new Polygon();

                        redraw(activePolygon, true);
                    } else {
                        Point point = new Point(e.getX(), e.getY());
                        activePolygon.addPoint(point, currentStyle);

                        if (activePolygon.pointCount() >= 2)
                            redraw(activePolygon, false);
                    }
                } else
                    startPoint = new Point(e.getX(), e.getY());
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                if (activePolygon == null)
                    return;
                if (activeMode == Mode.POLYGON && activePolygon.pointCount() > 0) {
                    Point point = new Point(e.getX(), e.getY());
                    if (isShiftPressed)
                        cursorPosition = alignTo45Degrees(activePolygon.lastPoint(), point);

                    Line line = new Line(activePolygon.lastPoint(), point, activeColor, currentStyle, selectedWidth);

                    // Draw preview line (previousPoint, point)
                    redraw(activePolygon, true);
                    line.draw(rasterizer);
                }
            }
        };
    }

    private void redraw(Shape shape, boolean clear) {
        // mouse dragged (preview) / mouse moved (in polygon mode)
        if (clear) {
            raster.clear();
            shapeCanvasRasterizer.rasterizeCanvas(shapeCanvas);
        }
        // mouse released (draw new finished shape)
        if (shape != null)
            shape.draw(rasterizer);

        panel.repaint();
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
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_C -> {
                        shapeCanvas.clear();
                        clear(0xaaaaaa);
                        panel.repaint();
                    }
                    case KeyEvent.VK_CONTROL -> {
                        stylePicker.setSelectedItem(Style.DOTTED);
                        currentStyle = Style.DOTTED;
                    }
                    case KeyEvent.VK_SHIFT -> isShiftPressed = true;
                    case KeyEvent.VK_L -> setMode(Mode.LINE);
                    case KeyEvent.VK_R -> setMode(Mode.RECTANGLE);
                    case KeyEvent.VK_S -> setMode(Mode.SQUARE);
                    case KeyEvent.VK_K -> setMode(Mode.CIRCLE);
                    case KeyEvent.VK_P -> setMode(Mode.POLYGON);
                    default -> super.keyPressed(e);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_CONTROL ->  {
                        stylePicker.setSelectedItem(Style.NORMAL);
                        currentStyle = Style.NORMAL;
                    }
                    case KeyEvent.VK_SHIFT -> isShiftPressed = false;
                    default -> super.keyReleased(e);
                }
            }
        };
    }

    private JButton createModeBtn(String label, Mode mode) {
        JButton button = new JButton(label);
        button.addActionListener(e -> setMode(mode));
        button.setFocusable(false);
        return button;
    }

    private void addButtons(JPanel toolbar, JButton[] buttons) {
        for (JButton button: buttons)
            toolbar.add(button);
    }
}