package main.gui;

import main.exceptions.AdminAccessException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.ArrayList;
import java.net.URL; // Added import for resource loading

public class LandingPanel {
    private JPanel mainPanel;
    private JPanel bgPanel;
    private JButton btnBook;

    private final List<Image> image = new ArrayList<>();
    private int currentIndex = 0;
    private int nextIndex = 1;
    private float slideOffset = 0;
    private boolean isAnimating = false;
    private final MainFrame parent;

    private Timer slideTimer;
    private Timer animationTimer;

    // --- Admin Logic Variables ---
    private boolean isAdminTriggered = false;

    public LandingPanel(MainFrame parent) {
        this.parent = parent;

        loadImages();

        SwingUtilities.invokeLater(() -> {
            setupTimers();
            setupButtonListener();
            btnStyle();
        });
    }

    // --- FIX IS HERE ---
    private void createUIComponents() {
        // 1. Initialize Main Panel (Best practice to prevent NPEs on root)
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // 2. Initialize Background Panel
        bgPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawSlideshow(g);
            }
        };
        // Use GridBagLayout to center the button easily
        bgPanel.setLayout(new GridBagLayout());
        bgPanel.setOpaque(true);

        // 3. CRITICAL FIX: Initialize the Button here!
        // If "Custom Create" is checked in the form, this must exist.
        btnBook = new JButton("Book Now");
    }

    private void loadImages() {
        try {
            // Using getClass().getResource is safer for exported JARs
            String[] paths = {"/Manila.png", "/Bohol.png", "/Cebu.png", "/Davao.png"};

            for(String path : paths){
                URL url = getClass().getResource(path);
                // Fallback if looking in specific folder structure
                if(url == null) url = getClass().getResource("/resources" + path);

                if(url != null) {
                    image.add(new ImageIcon(url).getImage());
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading images: " + e.getMessage());
        }
    }

    private void setupTimers() {
        slideTimer = new Timer(3000, e -> { // Increased to 3s for better viewing
            if (!image.isEmpty() && !isAnimating) {
                startSlideAnimation();
            }
        });
        slideTimer.start();

        animationTimer = new Timer(16, e -> { // ~60 FPS
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
            Timer holdTimer = new Timer(2000, e -> {
                isAdminTriggered = true;
                // Stop UI timers while showing the dialog to prevent lag
                stopTimers();
                String pwd = JOptionPane.showInputDialog(mainPanel, "Admin Access:\nEnter Password:");

                // Restart timers after dialog closes (optional, or handle inside try/catch)
                if(pwd == null) { setupTimers(); }

                try {
                    verifyAdminAccess(pwd);
                    stopTimers(); // Stop permanently if going to Admin
                    parent.showAdmin();
                } catch (AdminAccessException ex) {
                    // Wrong password, restart animation
                    setupTimers();
                    JOptionPane.showMessageDialog(mainPanel, ex.getMessage(), "Admin Denied", JOptionPane.ERROR_MESSAGE);
                }
            });
            holdTimer.setRepeats(false);

            btnBook.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    isAdminTriggered = false;
                    holdTimer.start();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    holdTimer.stop();
                    if (!isAdminTriggered) {
                        stopTimers();
                        parent.goToBooking();
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    holdTimer.stop();
                }
            });
        }
    }

    private void verifyAdminAccess(String pass) throws AdminAccessException {
        if(pass == null) throw new AdminAccessException("Login Cancelled.");
        if(!"admin123".equals(pass)) throw new AdminAccessException("Incorrect Password.");
    }

    private void drawSlideshow(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        int width = bgPanel.getWidth();
        int height = bgPanel.getHeight();

        if (image.isEmpty()) {
            g2d.setColor(new Color(84, 120, 125)); // Fallback color
            g2d.fillRect(0, 0, width, height);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 20));
            String msg = "Loading images...";
            FontMetrics fm = g2d.getFontMetrics();
            int x = (width - fm.stringWidth(msg)) / 2;
            int y = height / 2;
            g2d.drawString(msg, x, y);
            return;
        }

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
            // Apply custom Button UI for rounded effect
            btnBook.setBackground(new Color(244, 208, 63));
            btnBook.setForeground(Color.BLACK);
            btnBook.setFont(new Font("Arial", Font.BOLD, 16));
            btnBook.setFocusPainted(false);
            btnBook.setContentAreaFilled(false);
            btnBook.setBorderPainted(false);
            btnBook.setOpaque(false);

            // Set dimensions if needed
            btnBook.setPreferredSize(new Dimension(200, 50));

            // Custom painting for the button to make it rounded
            btnBook.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
                @Override
                public void paint(Graphics g, JComponent c) {
                    AbstractButton b = (AbstractButton) c;
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

                    // Hover effect logic handled by BasicButtonUI via model state
                    if(b.getModel().isRollover()) {
                        g2.setColor(new Color(255, 225, 100));
                    } else if (b.getModel().isPressed()) {
                        g2.setColor(new Color(200, 170, 50));
                    } else {
                        g2.setColor(b.getBackground());
                    }

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