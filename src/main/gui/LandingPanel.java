package main.gui;

import main.exceptions.AdminAccessException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

public class LandingPanel {
    private JPanel mainPanel;
    private JPanel imagePanel;
    private JButton btnBook;
    private JPanel bgPanel;
    private MainFrame controller;
    private boolean isAdminTriggered = false;

    public LandingPanel(MainFrame controller) {
        this.controller = controller;

        imagePanel.add(createImageLabel("/promo1.jpeg"));
        imagePanel.add(createImageLabel("/promo2.jpeg"));
        imagePanel.add(createImageLabel("/promo3.jpeg"));

        // Admin Panel Access (2s Hold)
        Timer holdTimer = new Timer(2000, e -> {
            isAdminTriggered = true;
            String pwd = JOptionPane.showInputDialog(mainPanel, "Admin Access:\nEnter Password:");
            try{
                verifyAdminAccess(pwd);
                controller.showAdmin();
            } catch (AdminAccessException ex) {
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
                if (!isAdminTriggered) controller.goToBooking();
            }
            @Override
            public void mouseExited(MouseEvent e) { holdTimer.stop(); }
        });
    }

    private void verifyAdminAccess(String pass) throws AdminAccessException {
        if(pass == null) throw new AdminAccessException("Login Cancelled.");
        if(!"admin123".equals(pass)) throw new AdminAccessException("Incorrect Password.");
    }

    private JLabel createImageLabel(String path) {
        try {
            URL url = getClass().getResource(path);
            if (url != null) {
                ImageIcon icon = new ImageIcon(url);
                Image img = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                return new JLabel(new ImageIcon(img));
            }
        } catch (Exception e) {}
        JLabel err = new JLabel("IMG", SwingConstants.CENTER);
        err.setPreferredSize(new Dimension(150, 150));
        err.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        err.setOpaque(true);
        err.setBackground(Color.LIGHT_GRAY);
        return err;
    }

    public JPanel getMainPanel() { return mainPanel; }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        btnBook = new main.gui.components.RoundedButton("Book Now").setNormalColor(new Color(244, 208, 63))
                .setHoverColor(new Color(255, 225, 100))
                .setPressedColor(new Color(200, 170, 50));

        bgPanel = new main.gui.components.ImagePanel("/Cool_bg.png");
        bgPanel.setLayout(new BorderLayout());
    }
}