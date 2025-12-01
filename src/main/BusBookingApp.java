package com.ambussin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Main application window for the Ambussin Bus Booking System.
 * Uses CardLayout to manage the four primary screens.
 */
public class BusBookingApp extends JFrame {

    // --- COLOR PALETTE ---
    private static final Color PRIMARY_COLOR = new Color(203, 171, 84); //Yellow Button
    private static final Color BACKGROUND_COLOR = new Color(94, 17, 37); //Maroon
    private static final Color CARD_BACKGROUND = Color.WHITE;
    private static final Color ACCENT_COLOR = new Color(255, 100, 100); // Red for highlights
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 36);
    private static final Font LABEL_FONT = new Font("Arial", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 16);

    private static final String LANDING = "LandingPage";
    private static final String PLACING_ORDER = "PlacingOrder";
    private static final String CONFIRMATION_REVIEW = "ConfirmationReview";
    // Renamed the two final screens for clearer flow management
    private static final String ORDER_DETAILS = "OrderDetails";
    private static final String CONFIRMATION_FINAL = "ConfirmationFinal";

    private JPanel cardPanel;
    private CardLayout cardLayout;

    // --- DATA FIELDS FOR USER INPUT / CONFIRMATION ---
    private JComboBox<String> dateDropdown;
    private JTextField originField;
    private JTextField destinationField;

    // UI elements to update on the Confirmation page
    private JTable scheduleTable;
    private JLabel routeEstimateLabel;
    private JLabel totalFareLabel;

    // New fields to hold selected booking data for the Order Details screen
    private JLabel detailsRouteLabel;
    private JLabel detailsSeatLabel;
    private JLabel detailsFareLabel;


    public BusBookingApp() {
        setTitle("AMBUSSIN Bus Booking System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // --- Initialize fields before creating panels ---
        originField = new JTextField(15);
        destinationField = new JTextField(15);
        dateDropdown = createDateDropdown();

        // Initialize dynamic labels/table
        scheduleTable = new JTable();
        routeEstimateLabel = new JLabel("ETA: ---", SwingConstants.CENTER);
        totalFareLabel = new JLabel("Fare: ---", SwingConstants.CENTER);

        detailsRouteLabel = new JLabel("Route: N/A");
        detailsSeatLabel = new JLabel("Seat: N/A");
        detailsFareLabel = new JLabel("Fare: N/A");


        // --- Create the 5 main screens (Panels) ---
        cardPanel.add(createLandingPage(), LANDING);
        cardPanel.add(createPlacingOrderPage(), PLACING_ORDER);
        cardPanel.add(createConfirmationReviewPage(), CONFIRMATION_REVIEW);
        cardPanel.add(createOrderDetailsPage(), ORDER_DETAILS); // NEW SCREEN
        cardPanel.add(createConfirmationFinalPage(), CONFIRMATION_FINAL);

        add(cardPanel);
        cardLayout.show(cardPanel, LANDING);
        setVisible(true);
    }

    // --- HELPER METHODS FOR DROPDOWNS ---

    private JComboBox<String> createDateDropdown() {
        List<String> dates = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, MMM d");

        // Populate with today and the next 6 days
        for (int i = 0; i < 7; i++) {
            LocalDate date = LocalDate.now().plusDays(i);
            dates.add(date.format(formatter));
        }
        return new JComboBox<>(dates.toArray(new String[0]));
    }



    // --- SCREEN IMPLEMENTATIONS ---

    private JPanel createLandingPage() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);

        JLabel title = new JLabel("AMBUSSIN", SwingConstants.CENTER);
        title.setFont(TITLE_FONT);
        title.setForeground(new Color(0xcbac54));
        title.setBorder(new EmptyBorder(30, 0, 50, 0));
        panel.add(title, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(BACKGROUND_COLOR);

        JLabel sceneryLabel = new JLabel("Your Next Journey Starts Here!", SwingConstants.CENTER);
        sceneryLabel.setFont(new Font("Arial", Font.ITALIC, 28));
        sceneryLabel.setForeground(new Color(0xcbac54));

        JButton travelNowButton = new JButton("Travel Now!");
        travelNowButton.setFont(BUTTON_FONT);
        travelNowButton.setBackground(PRIMARY_COLOR);
        travelNowButton.setForeground(new Color(0x511c0a));
        travelNowButton.setFocusPainted(false);
        travelNowButton.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
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
        panel.setBackground(CARD_BACKGROUND);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Book Your Trip", SwingConstants.CENTER);
        title.setFont(TITLE_FONT);
        title.setBorder(new EmptyBorder(0, 0, 20, 0));
        panel.add(title, BorderLayout.NORTH);

        JPanel formPanelContainer = new JPanel(new GridBagLayout());
        formPanelContainer.setBackground(CARD_BACKGROUND);

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 15, 15));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BACKGROUND_COLOR, 1),
                new EmptyBorder(30, 30, 30, 30)
        ));

        // --- FORM FIELDS ---

        // Label 1: Select Date (NOW A DROPDOWN)
        formPanel.add(new JLabel("Select Date:", JLabel.RIGHT));
        formPanel.add(dateDropdown);


        // Label 3: Origin
        formPanel.add(new JLabel("Origin:", JLabel.RIGHT));
        formPanel.add(originField);

        // Label 4: Destination
        formPanel.add(new JLabel("Destination:", JLabel.RIGHT));
        formPanel.add(destinationField);

        formPanelContainer.add(formPanel);
        panel.add(formPanelContainer, BorderLayout.CENTER);

        // --- Buttons ---
        JButton confirmButton = new JButton("Search Schedules");
        confirmButton.setFont(BUTTON_FONT);
        confirmButton.setBackground(PRIMARY_COLOR);
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setPreferredSize(new Dimension(200, 45));
        confirmButton.setFocusPainted(false);

        confirmButton.addActionListener(e -> searchAndShowSchedules());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        buttonPanel.add(confirmButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Placeholder method to simulate fetching schedules and ETA from the Spring Backend.
     */
    private void searchAndShowSchedules() {
        String origin = originField.getText().trim();
        String destination = destinationField.getText().trim();
        String date = (String) dateDropdown.getSelectedItem();

        if (origin.isEmpty() || destination.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both Origin and Destination.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // --- Simulate Backend Call for Schedules and Routing ---

        // 1. Get Schedules (Mock data)
        Object[][] newScheduleData = {
                {"Deluxe (R001)", date},
                {"1 hour later", "Standard (R002)", date},
                {"2 hours later", "Deluxe (R001)", date}
        };

        // 2. Get ETA & Fare (Mock data)
        String mockEta = "45 minutes";
        String mockFare = "PHP 150.00";

        // Update the Confirmation Review Page

        // Update Table Model
        scheduleTable.setModel(new DefaultTableModel(
                newScheduleData,
                new String [] {"TIME", "VEHICLE", "DATE"}
        ));

        // Update Route Estimate and Total Fare
        routeEstimateLabel.setText("Estimated Travel Time: " + mockEta);
        totalFareLabel.setText("Calculated Fare: " + mockFare);

        // Now switch the card
        cardLayout.show(cardPanel, CONFIRMATION_REVIEW);
    }

    // --- CONFIRMATION REVIEW PAGE ---

    private JPanel createConfirmationReviewPage() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Select Schedule & Seat", SwingConstants.CENTER); // Updated title
        title.setFont(TITLE_FONT);
        panel.add(title, BorderLayout.NORTH);

        // Content Panel uses GridLayout for the three columns
        JPanel contentPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        contentPanel.setBackground(BACKGROUND_COLOR);

        // Column 1: Schedules
        contentPanel.add(createSchedulePanel());

        // Column 2: Route Map
        contentPanel.add(createRouteMapPanel());

        // Column 3: Available Seats
        contentPanel.add(createSeatsPanel());

        panel.add(contentPanel, BorderLayout.CENTER);

        // Place Order Button (South Panel) -> NOW GOES TO ORDER DETAILS
        JButton placeOrderButton = new JButton("Proceed to Order Summary");
        placeOrderButton.setFont(BUTTON_FONT);
        placeOrderButton.setBackground(PRIMARY_COLOR); // Changed to primary color
        placeOrderButton.setForeground(Color.WHITE);
        placeOrderButton.setPreferredSize(new Dimension(250, 50));

        placeOrderButton.addActionListener(e -> {
            // Simulate collecting selected data (Schedule row and selected seat)
            int selectedRow = scheduleTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select an available schedule.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Mock selected seat data
            String selectedTime = scheduleTable.getValueAt(selectedRow, 0).toString();
            String selectedVehicle = scheduleTable.getValueAt(selectedRow, 1).toString();
            String selectedSeat = "14A"; // Assume seat 14A was clicked on the previous screen
            String finalFare = totalFareLabel.getText().replace("Calculated Fare: ", "");

            // Update the labels on the Order Details page
            String routeDetails = originField.getText() + " -> " + destinationField.getText() + " (" + selectedTime + " on " + selectedVehicle + ")";
            detailsRouteLabel.setText("Route: " + routeDetails);
            detailsSeatLabel.setText("Selected Seat: " + selectedSeat);
            detailsFareLabel.setText("Total Due: " + finalFare);

            // Navigate to the Order Details page
            cardLayout.show(cardPanel, ORDER_DETAILS);
        });

        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        southPanel.add(placeOrderButton);
        panel.add(southPanel, BorderLayout.SOUTH);

        return panel;
    }

    // --- NEW SCREEN: ORDER DETAILS (The confirmation page) ---

    private JPanel createOrderDetailsPage() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(50, 100, 50, 100));

        JLabel title = new JLabel("Order Details & Confirmation", SwingConstants.CENTER);
        title.setFont(TITLE_FONT);
        panel.add(title, BorderLayout.NORTH);

        JPanel detailsPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        detailsPanel.setBackground(CARD_BACKGROUND);
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        // Add detailed information labels
        JLabel summaryLabel = new JLabel("Summary:", SwingConstants.LEFT); // Separated JLabel creation
        summaryLabel.setFont(new Font("Arial", Font.BOLD, 18)); // Set font separately
        detailsPanel.add(summaryLabel);

        detailsPanel.add(detailsRouteLabel);
        detailsPanel.add(detailsSeatLabel);
        detailsPanel.add(detailsFareLabel);

        detailsRouteLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        detailsSeatLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        detailsFareLabel.setFont(new Font("Arial", Font.BOLD, 18));
        detailsFareLabel.setForeground(ACCENT_COLOR.darker());

        panel.add(detailsPanel, BorderLayout.CENTER);

        // Buttons: Edit and Confirm
        JButton editButton = new JButton("Edit Order");
        editButton.setFont(BUTTON_FONT);
        editButton.setBackground(Color.GRAY);
        editButton.setForeground(Color.WHITE);
        editButton.setPreferredSize(new Dimension(150, 45));
        editButton.addActionListener(e -> cardLayout.show(cardPanel, CONFIRMATION_REVIEW)); // Go back to review

        JButton confirmBookingButton = new JButton("Confirm Booking & Pay");
        confirmBookingButton.setFont(BUTTON_FONT);
        confirmBookingButton.setBackground(ACCENT_COLOR);
        confirmBookingButton.setForeground(Color.WHITE);
        confirmBookingButton.setPreferredSize(new Dimension(250, 45));
        confirmBookingButton.addActionListener(e -> showTicketDialog()); // Trigger the final ticket dialog

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.add(editButton);
        buttonPanel.add(confirmBookingButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    // --- FINAL TICKET DIALOG ---

    private void showTicketDialog() {
        // Mock Ticket Generation based on selected data
        String route = detailsRouteLabel.getText().replace("Route: ", "");
        String seat = detailsSeatLabel.getText().replace("Selected Seat: ", "");
        String fare = detailsFareLabel.getText().replace("Total Due: ", "");
        String bookingId = "AMB-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd")) + (int)(Math.random() * 1000);

        String ticketContent = String.format(
                "<html><body style='width: 300px; padding: 10px; font-family: Arial;'>" +
                        "<h2 style='color: #3296c8; text-align: center; border-bottom: 2px solid #ddd;'>AMBUSSIN TICKET</h2>" +
                        "<p><b>Booking ID:</b> %s</p>" +
                        "<p><b>Route:</b> %s</p>" +
                        "<p><b>Departure:</b> %s</p>" +
                        "<p><b>Seat No:</b> %s</p>" +
                        "<p><b>Status:</b> CONFIRMED</p>" +
                        "<h3 style='color: #ff6464; text-align: center;'>Total Paid: %s</h3>" +
                        "<p style='font-size: 10px; text-align: center;'>Thank you for choosing Ambussin!</p>" +
                        "</body></html>",
                bookingId, route + " (" + dateDropdown.getSelectedItem() + ")", seat, fare
        );

        // Display the ticket in a custom JDialog
        JDialog ticketDialog = new JDialog(this, "Your Confirmed Ticket", true);
        ticketDialog.setLayout(new BorderLayout());
        ticketDialog.setSize(400, 350);
        ticketDialog.setLocationRelativeTo(this);

        JLabel ticketLabel = new JLabel(ticketContent, SwingConstants.CENTER);
        ticketDialog.add(ticketLabel, BorderLayout.CENTER);

        JButton closeButton = new JButton("Close & Finish");
        closeButton.setFont(BUTTON_FONT);
        closeButton.setBackground(PRIMARY_COLOR);
        closeButton.setForeground(Color.WHITE);
        closeButton.addActionListener(e -> {
            ticketDialog.dispose();
            cardLayout.show(cardPanel, CONFIRMATION_FINAL); // Move to final success screen after dialog closes
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        ticketDialog.add(buttonPanel, BorderLayout.SOUTH);

        ticketDialog.setVisible(true);
    }

    // --- REST OF HELPER METHODS (Unchanged except for internal updates) ---

    private JPanel createSchedulePanel() {
        JPanel schedulesPanel = new JPanel(new BorderLayout(10, 10));
        schedulesPanel.setBackground(CARD_BACKGROUND);
        schedulesPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(PRIMARY_COLOR), "AVAILABLE SCHEDULES",
                        0, 0, new Font("Arial", Font.BOLD, 16), PRIMARY_COLOR),
                new EmptyBorder(10, 10, 10, 10)
        ));

        String[] columns = {"TIME", "VEHICLE", "DATE"};
        Object[][] data = {{"--", "--", "--"}};

        scheduleTable = new JTable(data, columns);
        scheduleTable.setFillsViewportHeight(true);
        scheduleTable.setRowHeight(25);
        scheduleTable.setFont(LABEL_FONT);

        // Selection listener to update fare/seats when a schedule is picked
        scheduleTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && scheduleTable.getSelectedRow() != -1) {
                // Simulate updating fare/seats based on selected schedule
                totalFareLabel.setText("Calculated Fare: PHP 180.00 (Deluxe)");
            }
        });

        JScrollPane scrollPane = new JScrollPane(scheduleTable);
        schedulesPanel.add(scrollPane, BorderLayout.CENTER);

        schedulesPanel.add(totalFareLabel, BorderLayout.SOUTH); // Display fare here
        totalFareLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalFareLabel.setForeground(PRIMARY_COLOR.darker());

        return schedulesPanel;
    }

    private JPanel createRouteMapPanel() {
        JPanel routePanel = new JPanel(new BorderLayout(10, 10));
        routePanel.setBackground(CARD_BACKGROUND);
        routePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(PRIMARY_COLOR), "ROUTE DETAILS",
                        0, 0, new Font("Arial", Font.BOLD, 16), PRIMARY_COLOR),
                new EmptyBorder(10, 10, 10, 10)
        ));

        // Placeholder for the Map/Route Visualization
        JPanel mapPlaceholder = new JPanel(new GridBagLayout());
        mapPlaceholder.setBackground(BACKGROUND_COLOR.brighter());
        mapPlaceholder.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        mapPlaceholder.add(new JLabel("ROUTE MAP VISUALIZATION", SwingConstants.CENTER));

        routeEstimateLabel.setFont(new Font("Arial", Font.BOLD, 16));
        routeEstimateLabel.setForeground(ACCENT_COLOR);

        routePanel.add(mapPlaceholder, BorderLayout.CENTER);
        routePanel.add(routeEstimateLabel, BorderLayout.SOUTH);

        return routePanel;
    }

    private JPanel createSeatsPanel() {
        JPanel seatsPanel = new JPanel(new BorderLayout(10, 10));
        seatsPanel.setBackground(CARD_BACKGROUND);
        seatsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(PRIMARY_COLOR), "SEAT SELECTION",
                        0, 0, new Font("Arial", Font.BOLD, 16), PRIMARY_COLOR),
                new EmptyBorder(10, 10, 10, 10)
        ));

        // Seat Visualization (Grid layout for seats)
        JPanel seatBlock = new JPanel(new GridLayout(10, 4, 10, 10)); // 10 rows, 4 seats wide
        seatBlock.setBackground(CARD_BACKGROUND);

        for (int i = 1; i <= 40; i++) {
            JButton seatButton = new JButton(String.valueOf(i));
            seatButton.setFont(new Font("Arial", Font.BOLD, 10));
            seatButton.setBackground(i % 5 == 0 ? new Color(255, 192, 192) : new Color(153, 255, 153)); // Red=Booked, Green=Available
            seatButton.setForeground(Color.BLACK);
            seatButton.setFocusPainted(false);
            seatButton.setBorder(BorderFactory.createLineBorder(Color.GRAY));

            // Seat selection interaction
            seatButton.addActionListener(e -> {
                if (seatButton.getBackground().equals(new Color(255, 192, 192))) {
                    JOptionPane.showMessageDialog(this, "Seat " + seatButton.getText() + " is already booked.", "Unavailable", JOptionPane.WARNING_MESSAGE);
                } else {
                    // Logic to select a seat
                    JOptionPane.showMessageDialog(this, "Seat " + seatButton.getText() + " selected. You can now Place Order.", "Selected", JOptionPane.INFORMATION_MESSAGE);
                }
            });
            seatBlock.add(seatButton);
        }

        JScrollPane scrollPane = new JScrollPane(seatBlock);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        seatsPanel.add(scrollPane, BorderLayout.CENTER);

        // Add legend for seats
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        legendPanel.setBackground(CARD_BACKGROUND);
        legendPanel.add(createLegendItem(new Color(153, 255, 153), "Available"));
        legendPanel.add(createLegendItem(new Color(255, 192, 192), "Booked"));

        seatsPanel.add(legendPanel, BorderLayout.SOUTH);

        return seatsPanel;
    }

    private JPanel createLegendItem(Color color, String text) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        item.setBackground(CARD_BACKGROUND);
        JLabel square = new JLabel("  ");
        square.setOpaque(true);
        square.setBackground(color);
        square.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        JLabel label = new JLabel(text, JLabel.LEFT);
        item.add(square);
        item.add(label);
        return item;
    }

    private JPanel createConfirmationFinalPage() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);

        JLabel title = new JLabel("AMBUSSIN", SwingConstants.CENTER);
        title.setFont(TITLE_FONT);
        title.setBorder(new EmptyBorder(30, 0, 50, 0));
        panel.add(title, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(BACKGROUND_COLOR);

        JLabel thankYou = new JLabel("THANK YOU FOR BOOKING WITH US!", SwingConstants.CENTER);
        thankYou.setFont(new Font("Arial", Font.BOLD, 30));
        thankYou.setForeground(PRIMARY_COLOR.darker());

        JLabel ticketResult = new JLabel("Your ticket has been issued and sent to your device.", SwingConstants.CENTER);
        ticketResult.setFont(new Font("Arial", Font.PLAIN, 18));

        JButton homeButton = new JButton("Start New Search");
        homeButton.setFont(BUTTON_FONT);
        homeButton.setBackground(PRIMARY_COLOR);
        homeButton.setForeground(Color.WHITE);
        homeButton.setPreferredSize(new Dimension(200, 40));
        homeButton.addActionListener(e -> cardLayout.show(cardPanel, LANDING));


        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 20, 10);
        content.add(thankYou, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(10, 10, 40, 10);
        content.add(ticketResult, gbc);

        gbc.gridy = 2;
        content.add(homeButton, gbc);

        panel.add(content, BorderLayout.CENTER);
        return panel;
    }
}