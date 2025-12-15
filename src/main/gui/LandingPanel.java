package main.gui;

import main.exceptions.AdminAccessException; // Make sure you have this class, or change to Exception

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.ArrayList;

public class LandingPanel {
    private JPanel mainPanel;
    private JPanel bgPanel;
    private JButton btnBook;

    private Timer slideTimer;
    private Timer animationTimer;

    private MainFrame controller;

    private List<Image> image = new ArrayList<>();
    private int currentIndex = 0;
    private int nextIndex = 1;
    private float slideOffset = 0;
    private boolean isAnimating = false;
    private boolean isAdminTriggered = false;

    public LandingPanel(MainFrame controller) {
        this.controller = controller;

        loadImages();
        adminAccess();

        SwingUtilities.invokeLater(() -> {
            setupTimers();
        });
    }

    public void startTimers() {
        if (slideTimer != null && !slideTimer.isRunning()) {
            slideTimer.start();
        }
        if (animationTimer != null && !animationTimer.isRunning()) {
            animationTimer.start();
        }
    }
    // --- LOGIC FROM YOUR OLD CODE ---
    private void adminAccess() {
        // 1. The Timer that waits 2 seconds
        Timer holdtimer = new Timer(2000, event -> {
            isAdminTriggered = true;

            String pwd = JOptionPane.showInputDialog(mainPanel, "Admin Access:\nEnter password:");
            try {
                verifyAdminAccess(pwd);
                controller.showAdmin();
            } catch (AdminAccessException ex) {
                JOptionPane.showMessageDialog(mainPanel, ex.getMessage(), "Admin Denied", JOptionPane.ERROR_MESSAGE);
                // Restart slideshow if failed
                if (slideTimer != null) slideTimer.start();
            } catch (Exception ex) {
                // Fallback for general errors
                JOptionPane.showMessageDialog(mainPanel, "Access Denied", "Admin Denied", JOptionPane.ERROR_MESSAGE);
            }
        });
        holdtimer.setRepeats(false);

        // 2. The Mouse Listener
        btnBook.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                isAdminTriggered = false;
                holdtimer.start();
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                holdtimer.stop();
                if (!isAdminTriggered) {
                    stopTimers();
                    controller.goToBooking();
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                holdtimer.stop();
            }
        });
    }

    private void verifyAdminAccess(String pwd) throws AdminAccessException {
        if (pwd == null) throw new AdminAccessException("Login Cancelled.");
        if (!"admin123".equals(pwd)) throw new AdminAccessException("Incorrect password.");
    }
    // -------------------------------

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

    public void stopTimers() {
        if (slideTimer != null) slideTimer.stop();
        if (animationTimer != null) animationTimer.stop();
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        btnBook = new main.gui.components.RoundedButton("Book Now").setNormalColor(new Color(244, 208, 63))
                .setHoverColor(new Color(255, 225, 100))
                .setPressedColor(new Color(200, 170, 50))
                .setBorderColor(Color.BLACK);

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

    public JPanel getMainPanel() {return mainPanel;}
}