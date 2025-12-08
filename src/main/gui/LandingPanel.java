package main.gui;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class LandingPanel {
    private JPanel mainPanel;
    private JPanel bgPanel;
    private JButton btnBook;

    private List<Image> image = new ArrayList<>();
    private int currentIndex = 0;
    private int nextIndex = 1;
    private float slideOffset = 0;
    private boolean isAnimating = false;
    private MainFrame parent;

    private Timer slideTimer;
    private Timer animationTimer;

    public LandingPanel(MainFrame parent) {
        this.parent = parent;

        loadImages();

        SwingUtilities.invokeLater(() -> {
            setupTimers();
            setupButtonListener();

            if (bgPanel != null) {
                bgPanel.addAncestorListener(new AncestorListener() {
                    @Override
                    public void ancestorAdded(AncestorEvent event) {
                        if (slideTimer != null && !slideTimer.isRunning()) {
                            slideTimer.start();
                        }
                    }

                    @Override
                    public void ancestorRemoved(AncestorEvent event) {
                        stopTimers();
                    }

                    @Override
                    public void ancestorMoved(AncestorEvent event) {
                    }
                });
            }
        });
    }

    private void createUIComponents() {
        bgPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawSlideshow(g);
            }
        };
        bgPanel.setLayout(null);
        bgPanel.setOpaque(true);

        btnBook = new JButton("Search Scheds") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2.setColor(new Color(200, 170, 50));
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(255, 225, 100));
                } else {
                    g2.setColor(new Color(244, 208, 63));
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

                g2.setColor(getForeground());
                FontMetrics fm = g2.getFontMetrics();
                Rectangle stringBounds = fm.getStringBounds(this.getText(), g2).getBounds();
                int textX = (getWidth() - stringBounds.width) / 2;
                int textY = (getHeight() - stringBounds.height) / 2 + fm.getAscent();

                g2.drawString(getText(), textX, textY);
                g2.dispose();
            }
        };
        btnBook.setFont(new Font("Arial", Font.BOLD, 16));
        btnBook.setForeground(Color.BLACK);
        btnBook.setFocusPainted(false);
        btnBook.setBorderPainted(false);
        btnBook.setContentAreaFilled(false);
        btnBook.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void loadImages() {
        try {
            image.add(new ImageIcon("resources/Manila.png").getImage());
            image.add(new ImageIcon("resources/Bohol.png").getImage());
            image.add(new ImageIcon("resources/Cebu.png").getImage());
            image.add(new ImageIcon("resources/Davao.png").getImage());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private void setupTimers() {
        slideTimer = new Timer(3000, e -> {
            if (!image.isEmpty() && !isAnimating) {
                isAnimating = true;
                slideOffset = 0;
                nextIndex = (currentIndex + 1) % image.size();
                if (animationTimer != null) animationTimer.start();
            }
        });
        slideTimer.start();

        animationTimer = new Timer(30, e -> {
            if (isAnimating) {
                slideOffset += 0.04f;

                if (slideOffset >= 1.0f) {
                    slideOffset = 0;
                    isAnimating = false;
                    currentIndex = nextIndex;
                    nextIndex = (nextIndex + 1) % image.size();
                    ((Timer)e.getSource()).stop();
                }

                if (bgPanel != null) {
                    bgPanel.repaint();
                }
            }
        });
    }

    private void setupButtonListener() {
        if (btnBook != null) {
            btnBook.addActionListener(e -> {
                stopTimers();
                parent.goToBooking();
            });
        }
    }

    private void drawSlideshow(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        if (image.isEmpty()) {
            g2d.setColor(Color.DARK_GRAY);
            g2d.fillRect(0, 0, bgPanel.getWidth(), bgPanel.getHeight());
            return;
        }

        int width = bgPanel.getWidth();
        int height = bgPanel.getHeight();

        if (isAnimating) {
            int currentX = (int) (-width * slideOffset);
            int nextX = (int) (width * (1 - slideOffset));

            g2d.drawImage(image.get(currentIndex), currentX, 0, width, height, bgPanel);
            g2d.drawImage(image.get(nextIndex), nextX, 0, width, height, bgPanel);
        } else {
            g2d.drawImage(image.get(currentIndex), 0, 0, width, height, bgPanel);
        }
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public void stopTimers() {
        if (slideTimer != null && slideTimer.isRunning()) slideTimer.stop();
        if (animationTimer != null && animationTimer.isRunning()) animationTimer.stop();
    }
}