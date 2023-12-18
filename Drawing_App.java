import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Path2D;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Drawing_App extends JFrame {

    private DrawingPanel drawingPanel;
    private JButton colorButton, clearButton, saveButton;
    private JComboBox<String> thicknessComboBox;

    private List<ShapeInfo> shapes = new ArrayList<>();

    public Drawing_App() {
        setTitle("Drawing App");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        drawingPanel = new DrawingPanel();
        add(drawingPanel, BorderLayout.CENTER);

        JPanel controlsPanel = new JPanel();
        colorButton = new JButton("Choose Color");
        colorButton.addActionListener(e -> chooseColor());
        clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> clearDrawing());
        saveButton = new JButton("Save");
        saveButton.addActionListener(e -> saveDrawing());
        thicknessComboBox = new JComboBox<>(new String[]{"1", "2", "3", "4", "5"});
        thicknessComboBox.addActionListener(e -> setLineThickness());

        controlsPanel.add(colorButton);
        controlsPanel.add(clearButton);
        controlsPanel.add(saveButton);
        controlsPanel.add(new JLabel("Pen Size:"));
        controlsPanel.add(thicknessComboBox);

        add(controlsPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void chooseColor() {
        Color newColor = JColorChooser.showDialog(this, "Choose Color", drawingPanel.getCurrentColor());
        if (newColor != null) {
            drawingPanel.setCurrentColor(newColor);
        }
    }

    private void clearDrawing() {
        shapes.clear();
        drawingPanel.repaint();
    }

    private void saveDrawing() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileChooser.getSelectedFile()))) {
                oos.writeObject(shapes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setLineThickness() {
        int thickness = Integer.parseInt((String) thicknessComboBox.getSelectedItem());
        drawingPanel.setCurrentThickness(thickness);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Drawing_App());
    }

    private class DrawingPanel extends JPanel {

        private Color currentColor = Color.BLACK;
        private int currentThickness = 2;
        private Path2D currentPath;

        public DrawingPanel() {
            setBackground(Color.WHITE);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    currentPath = new Path2D.Double();
                    currentPath.moveTo(e.getX(), e.getY());
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    shapes.add(new ShapeInfo(currentPath, currentColor, currentThickness));
                    currentPath = null;
                    repaint();
                }
            });

            addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    currentPath.lineTo(e.getX(), e.getY());
                    repaint();
                }
            });
        }

        public void setCurrentColor(Color color) {
            currentColor = color;
        }

        public Color getCurrentColor() {
            return currentColor;
        }

        public void setCurrentThickness(int thickness) {
            currentThickness = thickness;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            for (ShapeInfo shape : shapes) {
                g2d.setColor(shape.getColor());
                g2d.setStroke(new BasicStroke(shape.getThickness()));
                g2d.draw(shape.getShape());
            }

            if (currentPath != null) {
                g2d.setColor(currentColor);
                g2d.setStroke(new BasicStroke(currentThickness));
                g2d.draw(currentPath);
            }
        }
    }

    private static class ShapeInfo implements Serializable {
        private final Shape shape;
        private final Color color;
        private final int thickness;

        public ShapeInfo(Shape shape, Color color, int thickness) {
            this.shape = shape;
            this.color = color;
            this.thickness = thickness;
        }

        public Shape getShape() {
            return shape;
        }

        public Color getColor() {
            return color;
        }

        public int getThickness() {
            return thickness;
        }
    }
}

