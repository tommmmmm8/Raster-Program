import models.Point;
import models.Line;
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
    private Point point;
    private Rasterizer rasterizer;
    private boolean isCtrlPressed = false;
    private boolean isShiftPressed = false;

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

        rasterizer = new TrivialRasterizer(Color.CYAN, raster);

        createAdapters();
        createKeyAdapter();

        panel.addMouseMotionListener(mouseAdapter);
        panel.addMouseListener(mouseAdapter);
        panel.addKeyListener(keyAdapter);
    }

    private void createKeyAdapter() {
        keyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_C) {
                    clear(0xaaaaaa);
                    panel.repaint();
                } else if (e.getKeyCode() == KeyEvent.VK_CONTROL)
                    isCtrlPressed = true;
                else if (e.getKeyCode() == KeyEvent.VK_SHIFT)
                    isShiftPressed = true;
                else
                    super.keyPressed(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_CONTROL)
                    isCtrlPressed = false;
                else if (e.getKeyCode() == KeyEvent.VK_SHIFT)
                    isShiftPressed = false;
                else
                    super.keyReleased(e);
            }
        };
    }


    private void createAdapters() {
        mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                Point point2 = new Point(e.getX(), e.getY());
                boolean isInBounds = isInBounds(point2);

                if (isInBounds) {
                    if (isShiftPressed)
                        point2 = alignTo45Degrees(point, point2);

                    Line line = new Line(point, point2);

                    ((TrivialRasterizer) rasterizer).setCtrlPressed(isCtrlPressed);
                    rasterizer.rasterize(line);
                    panel.repaint();
                } else
                    System.out.println("Point is outside the bounds of the panel.");
            }

            @Override
            public void mousePressed(MouseEvent e) {
                point = new Point(e.getX(), e.getY());
            }
        };
    }

    private boolean isInBounds(Point point) {
        return point.getX() >= 0 && point.getY() >= 0 &&
                point.getX() < panel.getWidth() && point.getY() < panel.getHeight();
    }

    private Point alignTo45Degrees(Point start, Point end) {
        double dx = end.getX() - start.getX();
        double dy = end.getY() - start.getY();
        double rawAngle = Math.atan2(dy, dx);
        System.out.println("Raw Angle: " + rawAngle);

        double angleSegment = Math.PI / 4; // 45 degrees in radians
        double snappedAngle = Math.round(rawAngle / angleSegment) * angleSegment;

        double maxDist = Math.max(Math.abs(dx), Math.abs(dy));
        int newX = start.getX() + (int)(maxDist * Math.cos(snappedAngle));
        int newY = start.getY() + (int)(maxDist * Math.sin(snappedAngle));

        return new Point(newX, newY);
    }


}
