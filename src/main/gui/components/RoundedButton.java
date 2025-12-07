package main.gui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RoundedButton extends JButton {
    private Color normalColor = Color.WHITE;
    private Color hoverColor = Color.LIGHT_GRAY;
    private Color pressedColor = Color.GRAY;
    private Color borderColor = Color.GRAY;
    private int radius = 20;

    public RoundedButton(String text) {
        super(text);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { setBackground(hoverColor); }
            @Override
            public void mouseExited(MouseEvent e) { setBackground(normalColor); }
            @Override
            public void mousePressed(MouseEvent e) { setBackground(pressedColor); }
            @Override
            public void mouseReleased(MouseEvent e) { setBackground(normalColor); }
        });
    }

    public RoundedButton setNormalColor(Color normalColor) {this.normalColor = normalColor;return this;}
    public RoundedButton setHoverColor(Color hoverColor) {this.hoverColor = hoverColor;return this;}
    public RoundedButton setPressedColor(Color pressedColor) {this.pressedColor = pressedColor;return this;}
    public RoundedButton setRadius(int radius) {this.radius = radius;return this;}
    public RoundedButton setBorderColor(Color borderColor) {this.borderColor = borderColor;return this;}

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (getModel().isPressed()) g2.setColor(pressedColor);
        else if (getModel().isRollover()) g2.setColor(hoverColor);
        else g2.setColor(normalColor);

        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        g2.setColor(borderColor);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);

        super.paintComponent(g);
        g2.dispose();
    }
}