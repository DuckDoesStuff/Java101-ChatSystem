package view;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

class RoundedButton extends JButton {
    int w;
    int h;
    int radius;
    boolean border;
    Color color;
    public RoundedButton(String label, int w, int h, int radius, boolean border, Color color) {
        super(label);
        this.w = w;
        this.h = h;
        this.radius = radius;
        this.border = border;
        this.color = color;
        Dimension size = getPreferredSize();
        size.width = size.height = Math.max(size.width,size.height);
        setPreferredSize(size);

        setContentAreaFilled(false);
    }

    protected void paintComponent(Graphics g) {
        if (getModel().isArmed()) {
            g.setColor(Color.lightGray);
        } else {
            g.setColor(getBackground());
        }
        g.fillRoundRect(0, 0, w - 3, h - 3, radius, radius);
        super.paintComponent(g);
    }

    protected void paintBorder(Graphics g) {
        if (border){
            g.setColor(color);
            g.drawRoundRect(0, 0, w - 3, h - 3, radius, radius);
        }
    }

    Shape shape;
    public boolean contains(int x, int y) {
        if (shape == null ||
                !shape.getBounds().equals(getBounds())) {
            shape = new RoundRectangle2D.Float(0, 0, w - 3, h - 3, radius, radius);
        }
        return shape.contains(x, y);
    }

}