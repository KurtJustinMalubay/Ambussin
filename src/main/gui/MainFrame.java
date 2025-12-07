package main.gui;

import main.managers.DataManager;
import main.models.Vehicle;

import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;
import java.util.List;

public class MainFrame extends JFrame {
    private JPanel mainPanel;
    private CardLayout cards;

    // Wrapper Classes for Forms
    private LandingPanel landing;
    private BookingPanel booking;
    private SelectionPanel selection;
    private AdminPanel admin;

    public MainFrame() {
        setTitle("Ambussin Kiosk");
        setSize(1024, 768);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cards = new CardLayout();
        mainPanel = new JPanel(cards);

        // Load Data safely
        List<Vehicle> data;
        try {
            data = DataManager.getInstance().loadVehicles();
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Error loading database: " + e.getMessage());
            data = List.of(); // Empty list fallback
        }

        // Initialize Forms
        landing = new LandingPanel(this);
        booking = new BookingPanel(this, data);
        selection = new SelectionPanel(this, data);
        admin = new AdminPanel(this);

        // Add Panels via .getMainPanel() getter
        mainPanel.add(landing.getMainPanel(), "LANDING");
        mainPanel.add(booking.getMainPanel(), "BOOKING");
        mainPanel.add(selection.getMainPanel(), "SELECTION");
        mainPanel.add(admin.getMainPanel(), "ADMIN");

        add(mainPanel);
        goToLanding();
    }

    // Navigation Methods
    public void goToLanding() { cards.show(mainPanel, "LANDING"); }

    public void goToBooking() { cards.show(mainPanel, "BOOKING"); }

    public void goToSelection(String dest, String busType, String pType) {
        selection.loadResults(dest, busType, pType);
        cards.show(mainPanel, "SELECTION");
    }

    public void showAdmin() {
        admin.refresh();
        cards.show(mainPanel, "ADMIN");
    }
}