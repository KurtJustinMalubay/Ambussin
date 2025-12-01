package com.ambussin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Main application window for the Ambussin Bus Booking System.
 * Uses CardLayout to manage the four primary screens:
 * 1. Landing_Page
 * 2. Placing_Order
 * 3. Order_Confirmation (Review)
 * 4. Order_Confirmation (Final Success)
 * * NOTE: This is the GUI framework. The actual booking logic
 * will be called from the Spring Backend.
 */
public class BusBookingApp extends JFrame {

    private static final String LANDING = "LandingPage";
    private static final String PLACING_ORDER = "PlacingOrder";
    private static final String CONFIRMATION_REVIEW = "ConfirmationReview";
    private static final String CONFIRMATION_FINAL = "ConfirmationFinal";

    private JPanel cardPanel;
    private CardLayout cardLayout;

    public BusBookingApp() {
        setTitle("AMBUSSIN Bus Booking System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600); // Standard desktop size
        setLocationRelativeTo(null); // Center the window

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);


        cardPanel.add(createLandingPage(), LANDING);
        cardPanel.add(createPlacingOrderPage(), PLACING_ORDER);
        cardPanel.add(createConfirmationReviewPage(), CONFIRMATION_REVIEW);
        cardPanel.add(createConfirmationFinalPage(), CONFIRMATION_FINAL);

        add(cardPanel);
        cardLayout.show(cardPanel, LANDING); // Show the first screen
        setVisible(true);
    }



    private JPanel createLandingPage() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(240, 240, 240));

        JLabel title = new JLabel("AMBUSSIN", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 36));
        panel.add(title, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(new Color(240, 240, 240));

        JLabel sceneryLabel = new JLabel("VEHICLE type or scenery");
        sceneryLabel.setFont(new Font("Arial", Font.ITALIC, 24));

        JButton travelNowButton = new JButton("Travel Now!");
        travelNowButton.setFont(new Font("Arial", Font.BOLD, 18));
        travelNowButton.setPreferredSize(new Dimension(150, 40));
        travelNowButton.addActionListener(e -> cardLayout.show(cardPanel, PLACING_ORDER));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 50, 10);
        content.add(sceneryLabel, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(10, 10, 10, 10);
        content.add(travelNowButton, gbc);

        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createPlacingOrderPage() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel("AMBUSSIN", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 36));
        panel.add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Placing Order"));

        // --- FORM FIELDS ---
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Label 1: Select Date
        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Select Date:"), gbc);
        gbc.gridx = 1; formPanel.add(new JTextField(15), gbc); // Placeholder for Calendar picker

        // Label 2: Origin
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Origin:"), gbc);
        gbc.gridx = 1; formPanel.add(new JTextField(15), gbc);

        // Label 3: Destination
        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Destination:"), gbc);
        gbc.gridx = 1; formPanel.add(new JTextField(15), gbc);

        // Label 4: Time
        gbc.gridx = 2; gbc.gridy = 0; formPanel.add(new JLabel("Time:"), gbc);
        gbc.gridx = 3; formPanel.add(new JTextField(8), gbc);

        // Label 5: Total
        gbc.gridx = 2; gbc.gridy = 1; formPanel.add(new JLabel("Total:"), gbc);
        gbc.gridx = 3; formPanel.add(new JTextField(8), gbc);

        // --- Buttons ---
        JButton confirmButton = new JButton("Confirm");
        confirmButton.setPreferredSize(new Dimension(100, 30));
        confirmButton.addActionListener(e -> cardLayout.show(cardPanel, CONFIRMATION_REVIEW));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(confirmButton);

        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createConfirmationReviewPage() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(230, 230, 230));

        JLabel title = new JLabel("AMBUSSIN", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 36));
        panel.add(title, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Column 1: Schedules (Mock Data)
        contentPanel.add(createSchedulePanel());

        // Column 2: Route Map (Mock Data)
        contentPanel.add(createRouteMapPanel());

        // Column 3: Available Seats (Mock Data)
        contentPanel.add(createSeatsPanel());

        panel.add(contentPanel, BorderLayout.CENTER);

        // Place Order Button
        JButton placeOrderButton = new JButton("Place Order");
        placeOrderButton.setFont(new Font("Arial", Font.BOLD, 16));
        placeOrderButton.addActionListener(e -> cardLayout.show(cardPanel, CONFIRMATION_FINAL));

        JPanel southPanel = new JPanel();
        southPanel.add(placeOrderButton);
        panel.add(southPanel, BorderLayout.SOUTH);

        return panel;
    }

    // Helper method to create the Schedules panel
    private JPanel createSchedulePanel() {
        JPanel schedulesPanel = new JPanel(new BorderLayout());
        schedulesPanel.setBorder(BorderFactory.createTitledBorder("AVAILABLE SCHEDULES"));

        String[] columns = {"TIME", "VEHICLE"};
        Object[][] data = {
                {"08:00 AM", "Deluxe"},
                {"10:30 AM", "Standard"},
                {"12:00 PM", "Deluxe"}
        };
        JTable table = new JTable(data, columns);
        JScrollPane scrollPane = new JScrollPane(table);
        schedulesPanel.add(scrollPane, BorderLayout.CENTER);

        return schedulesPanel;
    }

    // Helper method to create the Route Map panel
    private JPanel createRouteMapPanel() {
        JPanel routePanel = new JPanel(new BorderLayout());
        routePanel.setBorder(BorderFactory.createTitledBorder("ROUTE"));

        // Placeholder for the Map/Route Visualization
        JLabel mapPlaceholder = new JLabel("MAP of Stops/Route", SwingConstants.CENTER);
        mapPlaceholder.setPreferredSize(new Dimension(200, 200));
        mapPlaceholder.setBorder(BorderFactory.createLineBorder(Color.BLUE));

        JLabel estimate = new JLabel("ETA: 15 mins (if confirmed)", SwingConstants.CENTER);

        routePanel.add(mapPlaceholder, BorderLayout.CENTER);
        routePanel.add(estimate, BorderLayout.SOUTH);

        return routePanel;
    }

    // Helper method to create the Seats panel (Mockup)
    private JPanel createSeatsPanel() {
        JPanel seatsPanel = new JPanel(new BorderLayout());
        seatsPanel.setBorder(BorderFactory.createTitledBorder("AVAILABLE SEATS"));

        // Mock seat visualization (vertical rectangular block)
        JPanel seatBlock = new JPanel();
        seatBlock.setBackground(Color.LIGHT_GRAY);
        seatBlock.setPreferredSize(new Dimension(100, 300));

        JLabel seatLabel = new JLabel("Seat Layout", SwingConstants.CENTER);
        seatBlock.add(seatLabel);

        seatsPanel.add(seatBlock, BorderLayout.CENTER);
        return seatsPanel;
    }

    private JPanel createConfirmationFinalPage() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(255, 255, 255));

        JLabel title = new JLabel("AMBUSSIN", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 36));
        panel.add(title, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridBagLayout());

        JLabel thankYou = new JLabel("THANK YOU FOR BOOKING WITH US!", SwingConstants.CENTER);
        thankYou.setFont(new Font("Arial", Font.BOLD, 24));

        JLabel ticketResult = new JLabel("Ticket Issued.", SwingConstants.CENTER);
        ticketResult.setFont(new Font("Arial", Font.PLAIN, 16));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        content.add(thankYou, gbc);

        gbc.gridy = 1;
        content.add(ticketResult, gbc);

        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    public static void main(String[] args) {
        // Ensure GUI runs on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(BusBookingApp::new);
    }
}