package main.gui;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class LandingPanel {
    private JPanel mainPanel;
    private JPanel bgPanel;
    private JButton btnBook;
    private JPanel buttonPannel;

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
            btnStyle();
        });
    }

    private void createUIComponents() {
        mainPanel = new JPanel();
        buttonPannel = new JPanel();
        btnBook = new JButton();

        bgPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawSlideshow(g);
            }
        };
        bgPanel.setLayout(null);
        bgPanel.setOpaque(true);
    }

    private void loadImages() {
        try {
            image.add(new ImageIcon("resources/Manila.png").getImage());
            image.add(new ImageIcon("resources/Bohol.png").getImage());
            image.add(new ImageIcon("resources/Cebu.png").getImage());
            image.add(new ImageIcon("resources/Davao.png").getImage());
        } catch (Exception e) {
            System.err.println("Error loading images: " + e.getMessage());
        }
    }

    private void setupTimers() {
        slideTimer = new Timer(2000, e -> {
            if (!image.isEmpty() && !isAnimating) {
                startSlideAnimation();
            }
        });
        slideTimer.start();

        animationTimer = new Timer(15, e -> {
            if (isAnimating) {
                slideOffset += 0.04f;

                if (slideOffset >= 1.0f) {
                    slideOffset = 0;
                    isAnimating = false;
                    currentIndex = nextIndex;
                    nextIndex = (nextIndex + 1) % image.size();
                }

                if (bgPanel != null) {
                    bgPanel.repaint();
                }
            }
        });
        animationTimer.start();
    }

    private void startSlideAnimation() {
        isAnimating = true;
        slideOffset = 0;
        nextIndex = (currentIndex + 1) % image.size();
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
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 20));
            String msg = "Loading images...";
            FontMetrics fm = g2d.getFontMetrics();
            int x = (bgPanel.getWidth() - fm.stringWidth(msg)) / 2;
            int y = bgPanel.getHeight() / 2;
            g2d.drawString(msg, x, y);
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
        if (slideTimer != null) slideTimer.stop();
        if (animationTimer != null) animationTimer.stop();
    }

    public void btnStyle(){
        if (btnBook != null) {
            btnBook.setBackground(new Color(244, 208, 63));
            btnBook.setForeground(Color.BLACK);
            btnBook.setFont(new Font("Arial", Font.BOLD, 16));
            btnBook.setFocusPainted(false);
            btnBook.setContentAreaFilled(false);
            btnBook.setBorderPainted(false);
            btnBook.setOpaque(false);

            btnBook.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    btnBook.setBackground(new Color(247, 220, 111));
                    btnBook.repaint();
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    btnBook.setBackground(new Color(244, 208, 63));
                    btnBook.repaint();
                }
            });

            btnBook.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
                @Override
                public void paint(Graphics g, JComponent c) {
                    AbstractButton b = (AbstractButton) c;
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

                    g2.setColor(b.getBackground());
                    g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 25, 25);


                    g2.setColor(b.getForeground());
                    g2.setFont(b.getFont());
                    FontMetrics fm = g2.getFontMetrics();
                    String text = b.getText();
                    int x = (c.getWidth() - fm.stringWidth(text)) / 2;
                    int y = (c.getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                    g2.drawString(text, x, y);

                    g2.dispose();
                }
            });
        }
    }
}