package main;

import main.models.*; // IMPORTING THE BACKEND MODELS

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BusBookingApp extends JFrame {

    // --- THEME CONSTANTS ---
    private static final Color COL_PRIMARY = new Color(203, 171, 84);
    private static final Color COL_PRIMARY_DARK = new Color(163, 131, 50);
    private static final Color COL_BACKGROUND = new Color(94, 17, 37);
    private static final Color COL_LIGHT_BG = new Color(245, 245, 245);
    private static final Color COL_TEXT_DARK = new Color(50, 50, 50);
    private static final Color COL_ACCENT = new Color(220, 53, 69);

    // --- FONTS ---
    private static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 28);
    private static final Font FONT_SUBHEADER = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_BODY_BOLD = new Font("Segoe UI", Font.BOLD, 14);

    // --- SCREEN NAMES ---
    private static final String LANDING = "LandingPage";
    private static final String PLACING_ORDER = "PlacingOrder";
    private static final String CONFIRMATION_REVIEW = "ConfirmationReview";
    private static final String ORDER_DETAILS = "OrderDetails";
    private static final String CONFIRMATION_FINAL = "ConfirmationFinal";

    private JPanel cardPanel;
    private CardLayout cardLayout;

    // --- FRONTEND DATA FIELDS ---
    private JComboBox<String> dateDropdown;
    private JTextField nameField;
    private JTextField originField;
    private JTextField destinationField;
    // Added a dropdown to utilize the Passenger Backend Logic (Discounts)
    private JComboBox<String> typeDropdown;

    // --- DYNAMIC UI ELEMENTS ---
    private JTable scheduleTable;
    private JLabel routeEstimateLabel;
    private JLabel totalFareLabel;
    private JLabel routeMapLabel;
    private JPanel busSeatPanel; // Made this a class field to update it dynamically

    // --- DETAILS SCREEN LABELS ---
    private JLabel detailsNameLabel;
    private JLabel detailsRouteLabel;
    private JLabel detailsSeatLabel;
    private JLabel detailsFareLabel;

    // --- BACKEND CONNECTION VARIABLES ---
    // This list acts as our "Database"
    private List<Route> allRoutes = new ArrayList<>();

    // Variables to hold the state of the current transaction
    private Route currentRoute;
    private Seat currentSeat;
    private Booking currentBooking;

    public BusBookingApp() {
        setTitle("AMBUSSIN Bus Booking System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 750);
        setLocationRelativeTo(null);
        setBackground(Color.WHITE);

        // --- 1. INITIALIZE BACKEND DATA ---
        // We generate the routes and vehicles before building the UI
        initializeBackendData();

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Initialize Input Fields
        nameField = createStyledTextField();
        originField = createStyledTextField();
        destinationField = createStyledTextField();
        dateDropdown = createDateDropdown();

        // Passenger Type for Backend Discount Logic
        typeDropdown = new JComboBox<>(new String[]{"REGULAR", "STUDENT", "SENIOR", "PWD"});
        typeDropdown.setBackground(Color.WHITE);

        // Initialize Labels
        scheduleTable = new JTable();
        styleTable(scheduleTable);

        routeEstimateLabel = new JLabel("ETA: ---", SwingConstants.CENTER);
        routeEstimateLabel.setFont(FONT_BODY_BOLD);

        totalFareLabel = new JLabel("Select a schedule", SwingConstants.CENTER);
        totalFareLabel.setFont(FONT_SUBHEADER);
        totalFareLabel.setForeground(COL_BACKGROUND);

        detailsNameLabel = new JLabel();
        detailsRouteLabel = new JLabel();
        detailsSeatLabel = new JLabel();
        detailsFareLabel = new JLabel();

        // Add Screens
        cardPanel.add(createLandingPage(), LANDING);
        cardPanel.add(createSearchPage(), PLACING_ORDER);
        cardPanel.add(createSelectionPage(), CONFIRMATION_REVIEW);
        cardPanel.add(createSummaryPage(), ORDER_DETAILS);
        cardPanel.add(createSuccessPage(), CONFIRMATION_FINAL);

        add(cardPanel);
        cardLayout.show(cardPanel, LANDING);
        setVisible(true);
    }

    // =================================================================================
    //  BACKEND INITIALIZATION (The "Database")
    // =================================================================================
    private void initializeBackendData() {
        // Create Vehicles with different capacities and types
        Vehicle v1 = new Vehicle("BUS-101", "Aircon", 45);
        Vehicle v2 = new Vehicle("BUS-102", "Non-Aircon", 50);
        Vehicle v3 = new Vehicle("BUS-103", "Deluxe Sleeper", 30);

        // Pre-book some seats in the backend to show it works
        // v1.findSeat("3").reserve(); // Example of booking seat 3 in the code

        // Create Routes linking to Vehicles
        // Route(ID, Origin, Dest, BasePrice, Vehicle)
        allRoutes.add(new Route("R01", "Cebu", "Manila", 1500.0, v1));
        allRoutes.add(new Route("R02", "Cebu", "Davao", 1200.0, v2));
        allRoutes.add(new Route("R03", "Manila", "Baguio", 800.0, v3));
        allRoutes.add(new Route("R04", "Cebu", "Manila", 1800.0, v3)); // Different bus, same route
    }

    // =================================================================================
    // 1. LANDING PAGE
    // =================================================================================
    private JPanel createLandingPage() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COL_BACKGROUND);

        JPanel content = new JPanel(new GridLayout(3, 1, 10, 20));
        content.setOpaque(false);

        JLabel logo = new JLabel("AMBUSSIN", SwingConstants.CENTER);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 64));
        logo.setForeground(COL_PRIMARY);

        JLabel slogan = new JLabel("Luxury Travel. Affordable Prices.", SwingConstants.CENTER);
        slogan.setFont(new Font("Segoe UI", Font.ITALIC, 20));
        slogan.setForeground(Color.WHITE);

        RoundedButton startBtn = new RoundedButton("BOOK A TICKET");
        startBtn.setPreferredSize(new Dimension(250, 60));
        startBtn.setFont(new Font("Segoe UI", Font.BOLD, 20));
        startBtn.addActionListener(e -> cardLayout.show(cardPanel, PLACING_ORDER));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(logo, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 40, 0);
        panel.add(slogan, gbc);

        gbc.gridy = 2;
        panel.add(startBtn, gbc);

        JLabel footer = new JLabel("© 2025 Ambussin Transport Inc.", SwingConstants.CENTER);
        footer.setForeground(new Color(255, 255, 255, 100));
        gbc.gridy = 3;
        gbc.insets = new Insets(100, 0, 0, 0);
        panel.add(footer, gbc);

        return panel;
    }

    // =================================================================================
    // 2. SEARCH PAGE
    // =================================================================================
    private JPanel createSearchPage() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(createTopBar("Start Your Journey"), BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(COL_LIGHT_BG);

        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(40, 60, 40, 60)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.gridx = 0; gbc.gridy = 0;

        formCard.add(createLabel("Passenger Name"), gbc);
        gbc.gridy++;
        formCard.add(nameField, gbc);

        gbc.gridy++;
        formCard.add(createLabel("Passenger Type"), gbc); // Added for Discount Logic
        gbc.gridy++;
        formCard.add(typeDropdown, gbc);

        gbc.gridy++;
        formCard.add(createLabel("Travel Date"), gbc);
        gbc.gridy++;
        formCard.add(dateDropdown, gbc);

        gbc.gridy++;
        formCard.add(createLabel("Origin (e.g., Cebu)"), gbc);
        gbc.gridy++;
        formCard.add(originField, gbc);

        gbc.gridy++;
        formCard.add(createLabel("Destination (e.g., Manila)"), gbc);
        gbc.gridy++;
        formCard.add(destinationField, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(30, 0, 0, 0);
        RoundedButton searchBtn = new RoundedButton("SEARCH SCHEDULES");
        searchBtn.addActionListener(e -> performSearch());
        formCard.add(searchBtn, gbc);

        centerPanel.add(formCard);
        panel.add(centerPanel, BorderLayout.CENTER);
        return panel;
    }

    private void performSearch() {
        if(nameField.getText().isEmpty() || originField.getText().isEmpty() || destinationField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Missing Info", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // --- BACKEND CONNECTION: Filter Routes ---
        String inputOrigin = originField.getText().trim();
        String inputDest = destinationField.getText().trim();

        // Java Streams to filter the list of routes based on user input (Case Insensitive)
        List<Route> foundRoutes = allRoutes.stream()
                .filter(r -> r.getOrigin().equalsIgnoreCase(inputOrigin) &&
                        r.getDestination().equalsIgnoreCase(inputDest))
                .collect(Collectors.toList());

        if (foundRoutes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No routes found for " + inputOrigin + " to " + inputDest, "No Schedules", JOptionPane.INFORMATION_MESSAGE);
            // Optional: for testing, if input is empty, maybe show all routes?
            // For now, we return.
            return;
        }

        // Reset Selection
        currentRoute = null;
        currentSeat = null;
        currentBooking = null;

        // Populate Table with Found Routes
        // We store the Route ID in the table so we can retrieve the object later
        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "TIME", "BUS INFO", "PRICE"}, 0);

        for (Route r : foundRoutes) {
            model.addRow(new Object[]{
                    r.getRouteInfo().split("\\[")[1].split("\\]")[0], // Extract ID nicely
                    "08:00 AM", // Mock time (Backend Route doesn't have time yet, simple fix)
                    r.getVehicle(),
                    String.format("%.2f", r.getBaseFare())
            });
        }

        scheduleTable.setModel(model);

        // Update Map Text
        routeMapLabel.setText("<html><div style='text-align:center;'>" +
                "<h2 style='color:#5e1125'>" + inputOrigin.toUpperCase() + " <span style='color:#cbab54'>➝</span> " + inputDest.toUpperCase() + "</h2>" +
                "<i>Select a schedule to view seats...</i>" +
                "</div></html>");

        totalFareLabel.setText("Select a schedule");

        // Reset the visual seat map (make it empty until row selected)
        if(busSeatPanel != null) busSeatPanel.removeAll();

        cardLayout.show(cardPanel, CONFIRMATION_REVIEW);
    }

    // =================================================================================
    // 3. SELECTION PAGE
    // =================================================================================
    private JPanel createSelectionPage() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(createTopBar("Select Schedule & Seat"), BorderLayout.NORTH);

        JPanel content = new JPanel(new GridLayout(1, 2, 20, 0));
        content.setBackground(COL_LIGHT_BG);
        content.setBorder(new EmptyBorder(20, 20, 20, 20));

        // LEFT COLUMN
        JPanel leftPanel = new JPanel(new BorderLayout(0, 20));
        leftPanel.setOpaque(false);

        JPanel scheduleContainer = createStyledPanel("AVAILABLE SCHEDULES");
        scheduleContainer.add(new JScrollPane(scheduleTable), BorderLayout.CENTER);

        JPanel mapContainer = createStyledPanel("ROUTE INFO");
        routeMapLabel = new JLabel("", SwingConstants.CENTER);
        mapContainer.add(routeMapLabel, BorderLayout.CENTER);
        mapContainer.add(routeEstimateLabel, BorderLayout.SOUTH);

        leftPanel.add(scheduleContainer, BorderLayout.CENTER);
        leftPanel.add(mapContainer, BorderLayout.SOUTH);

        // RIGHT COLUMN
        JPanel rightPanel = createStyledPanel("SELECT YOUR SEAT");

        // Initialize the panel that holds the buttons
        busSeatPanel = new JPanel(new GridBagLayout());
        busSeatPanel.setBackground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(busSeatPanel); // Scrollable seat map
        scroll.setBorder(null);
        rightPanel.add(scroll, BorderLayout.CENTER);

        JPanel farePanel = new JPanel(new BorderLayout());
        farePanel.setBackground(Color.WHITE);
        farePanel.setBorder(new EmptyBorder(10,0,0,0));
        farePanel.add(totalFareLabel, BorderLayout.CENTER);

        RoundedButton proceedBtn = new RoundedButton("PROCEED TO CHECKOUT");
        proceedBtn.addActionListener(e -> validateAndProceed());
        farePanel.add(proceedBtn, BorderLayout.SOUTH);

        rightPanel.add(farePanel, BorderLayout.SOUTH);

        content.add(leftPanel);
        content.add(rightPanel);
        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    // --- LOGIC: Update Seat Map based on Backend Vehicle ---
    private void updateSeatMap(Vehicle vehicle) {
        busSeatPanel.removeAll(); // Clear old buttons

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 5;
        gbc.insets = new Insets(0,0,15,0);

        JLabel driver = new JLabel("DRIVER", SwingConstants.CENTER);
        driver.setOpaque(true);
        driver.setBackground(Color.LIGHT_GRAY);
        driver.setPreferredSize(new Dimension(200, 30));
        busSeatPanel.add(driver, gbc);

        gbc.gridwidth = 1;
        gbc.insets = new Insets(4, 4, 4, 4);

        int seatIndex = 1;
        // Simple 4-column layout logic (2-aisle-2)
        // We loop until we reach the capacity of the backend vehicle
        int capacity = 50; // Max visual limit

        for (int row = 0; row < 12; row++) {
            for (int col = 0; col < 5; col++) {
                gbc.gridx = col;
                gbc.gridy = row + 1;

                if (col == 2) {
                    busSeatPanel.add(Box.createHorizontalStrut(20), gbc);
                } else {
                    // Check if we have exceeded this bus's actual capacity
                    String seatNumStr = String.valueOf(seatIndex);
                    Seat backendSeat = vehicle.findSeat(seatNumStr);

                    if (backendSeat != null) {
                        JButton seatBtn = new JButton(seatNumStr);
                        styleSeatButton(seatBtn);

                        // --- BACKEND CHECK: Is seat available? ---
                        if (!backendSeat.isAvailable()) {
                            seatBtn.setBackground(Color.LIGHT_GRAY);
                            seatBtn.setEnabled(false); // Disable booked seats
                            seatBtn.setToolTipText("Already Booked");
                        } else {
                            // Add click listener
                            seatBtn.addActionListener(e -> {
                                // Reset all other green buttons
                                for(Component c : busSeatPanel.getComponents()) {
                                    if(c instanceof JButton && c.isEnabled()) {
                                        c.setBackground(new Color(153, 255, 153));
                                        c.setForeground(new Color(0, 100, 0));
                                    }
                                }
                                // Highlight selected
                                seatBtn.setBackground(COL_PRIMARY);
                                seatBtn.setForeground(COL_BACKGROUND);

                                // Store selected backend seat
                                currentSeat = backendSeat;
                                calculateTemporaryFare(); // Update price label
                            });
                        }
                        busSeatPanel.add(seatBtn, gbc);
                    }
                    seatIndex++;
                }
            }
        }
        busSeatPanel.revalidate();
        busSeatPanel.repaint();
    }

    // --- LOGIC: Calculate Fare using Backend Models before booking ---
    private void calculateTemporaryFare() {
        if (currentRoute == null || currentSeat == null) return;

        // Create a temporary passenger just to get the discount rate
        Passenger tempPass = new Passenger(nameField.getText(), 20, (String)typeDropdown.getSelectedItem());

        // We need to set the seat type temporarily to check price
        currentSeat.setPassengerType(tempPass.getPassengerType());

        // Create a temporary booking object just to run calculation
        Booking tempBooking = new Booking(tempPass, currentRoute, currentSeat);

        totalFareLabel.setText("Total Fare: PHP " + String.format("%.2f", tempBooking.getTotalFare()));

        // Reset seat state so we don't accidentally save it yet
        currentSeat.release();
    }

    private void validateAndProceed() {
        if (currentRoute == null) {
            JOptionPane.showMessageDialog(this, "Please select a schedule first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (currentSeat == null) {
            JOptionPane.showMessageDialog(this, "Please select a seat.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // --- BACKEND CONNECTION: Create the Real Booking ---
        // 1. Create Passenger
        String pName = nameField.getText();
        String pType = (String) typeDropdown.getSelectedItem();
        int pAge = 25; // Default age since we don't have an input field for it yet
        Passenger passenger = new Passenger(pName, pAge, pType);

        // 2. Configure Seat (Apply logic for Student/PWD)
        currentSeat.setPassengerType(pType);

        // 3. Create Booking Object
        currentBooking = new Booking(passenger, currentRoute, currentSeat);

        // Update Summary UI
        detailsNameLabel.setText("Passenger: " + passenger.getName() + " (" + pType + ")");
        detailsRouteLabel.setText("<html><b>" + currentRoute.getOrigin() + " ➝ " + currentRoute.getDestination() + "</b><br>" + currentRoute.getVehicle() + "</html>");
        detailsSeatLabel.setText("Seat Number: " + currentSeat.getSeatNumber());
        detailsFareLabel.setText("PHP " + String.format("%.2f", currentBooking.getTotalFare()));

        cardLayout.show(cardPanel, ORDER_DETAILS);
    }

    // =================================================================================
    // 4. SUMMARY PAGE
    // =================================================================================
    private JPanel createSummaryPage() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COL_BACKGROUND);

        JPanel receipt = new JPanel(new GridBagLayout());
        receipt.setBackground(Color.WHITE);
        receipt.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COL_PRIMARY, 4),
                new EmptyBorder(40, 60, 40, 60)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel logo = new JLabel("AMBUSSIN", SwingConstants.CENTER);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 30));
        logo.setForeground(COL_BACKGROUND);
        receipt.add(logo, gbc);

        gbc.gridy++;
        JLabel sub = new JLabel("TRIP SUMMARY", SwingConstants.CENTER);
        sub.setFont(FONT_BODY);
        sub.setForeground(Color.GRAY);
        receipt.add(sub, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(20, 0, 20, 0);
        receipt.add(new JSeparator(SwingConstants.HORIZONTAL), gbc);

        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.anchor = GridBagConstraints.WEST;

        detailsNameLabel.setFont(FONT_BODY_BOLD);
        receipt.add(detailsNameLabel, gbc);
        gbc.gridy++;

        detailsRouteLabel.setFont(FONT_BODY);
        receipt.add(detailsRouteLabel, gbc);
        gbc.gridy++;

        detailsSeatLabel.setFont(FONT_BODY);
        receipt.add(detailsSeatLabel, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(30, 0, 5, 0);
        JLabel totalTxt = new JLabel("TOTAL AMOUNT");
        totalTxt.setForeground(Color.GRAY);
        receipt.add(totalTxt, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 30, 0);
        detailsFareLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        detailsFareLabel.setForeground(COL_ACCENT);
        receipt.add(detailsFareLabel, gbc);

        gbc.gridy++;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.setBackground(Color.WHITE);

        RoundedButton edit = new RoundedButton("EDIT");
        edit.setBackground(Color.LIGHT_GRAY);
        edit.setForeground(Color.BLACK);
        edit.addActionListener(e -> cardLayout.show(cardPanel, CONFIRMATION_REVIEW));

        RoundedButton pay = new RoundedButton("PAY & CONFIRM");
        pay.addActionListener(e -> confirmBookingAndShowTicket());

        btnPanel.add(edit);
        btnPanel.add(pay);
        receipt.add(btnPanel, gbc);

        panel.add(receipt);
        return panel;
    }

    // =================================================================================
    // 5. SUCCESS PAGE & TICKET
    // =================================================================================
    private void confirmBookingAndShowTicket() {
        if(currentBooking != null) {
            // --- BACKEND LOGIC: Confirm the booking ---
            // This sets the seat to unavailable and changes status to CONFIRMED
            currentBooking.confirm();
        }

        JDialog d = new JDialog(this, "Boarding Pass", true);
        d.setSize(500, 400);
        d.setLocationRelativeTo(this);
        d.setLayout(new BorderLayout());

        JPanel ticket = new JPanel(new GridBagLayout());
        ticket.setBackground(Color.WHITE);

        // Use the backend generateTicket() string, convert newlines to HTML for Swing
        String rawTicket = currentBooking.generateTicket();
        String htmlContent = rawTicket.replace("\n", "<br>");

        String html = String.format("<html><div style='font-family:monospace; width:350px; border:2px dashed gray; padding:15px;'>" +
                        "<h2 style='color:#5e1125'>AMBUSSIN TICKET</h2>" +
                        "<hr>" +
                        "%s" +
                        "<br><div style='text-align:center; color:green; margin-top:10px;'>*** PAID ***</div>" +
                        "</div></html>",
                htmlContent);

        ticket.add(new JLabel(html));
        d.add(ticket, BorderLayout.CENTER);

        RoundedButton close = new RoundedButton("PRINT & FINISH");
        close.addActionListener(e -> {
            d.dispose();
            cardLayout.show(cardPanel, CONFIRMATION_FINAL);
        });

        JPanel p = new JPanel(); p.setBackground(Color.WHITE); p.add(close);
        d.add(p, BorderLayout.SOUTH);
        d.setVisible(true);
    }

    private JPanel createSuccessPage() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COL_BACKGROUND);

        JLabel tick = new JLabel("✔");
        tick.setFont(new Font("Segoe UI", Font.BOLD, 100));
        tick.setForeground(COL_PRIMARY);

        JLabel msg = new JLabel("Booking Confirmed!");
        msg.setFont(FONT_HEADER);
        msg.setForeground(Color.WHITE);

        RoundedButton home = new RoundedButton("BOOK ANOTHER TRIP");
        home.addActionListener(e -> {
            // Clear fields
            nameField.setText("");
            originField.setText("");
            destinationField.setText("");

            // Clean up state
            currentBooking = null;
            currentRoute = null;
            currentSeat = null;

            cardLayout.show(cardPanel, LANDING);
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx=0; gbc.gridy=0;
        panel.add(tick, gbc);
        gbc.gridy++;
        panel.add(msg, gbc);
        gbc.gridy++;
        gbc.insets = new Insets(30,0,0,0);
        panel.add(home, gbc);

        return panel;
    }

    // =================================================================================
    // UTILITIES & STYLING
    // =================================================================================

    private JPanel createTopBar(String title) {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(COL_BACKGROUND);
        bar.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel brand = new JLabel("AMBUSSIN", SwingConstants.LEFT);
        brand.setForeground(COL_PRIMARY);
        brand.setFont(new Font("Segoe UI", Font.BOLD, 20));

        JLabel screenTitle = new JLabel(title, SwingConstants.RIGHT);
        screenTitle.setForeground(Color.WHITE);
        screenTitle.setFont(FONT_BODY);

        bar.add(brand, BorderLayout.WEST);
        bar.add(screenTitle, BorderLayout.EAST);
        return bar;
    }

    private JPanel createStyledPanel(String title) {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(230,230,230), 1),
                new EmptyBorder(15, 15, 15, 15)
        ));
        JLabel l = new JLabel(title);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(Color.GRAY);
        p.add(l, BorderLayout.NORTH);
        return p;
    }

    private JTextField createStyledTextField() {
        JTextField tf = new JTextField(15);
        tf.setFont(FONT_BODY);
        tf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.GRAY, 1),
                new EmptyBorder(8, 8, 8, 8)
        ));
        return tf;
    }

    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(COL_TEXT_DARK);
        return l;
    }

    private JComboBox<String> createDateDropdown() {
        List<String> dates = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMM d");
        for (int i = 0; i < 7; i++) {
            dates.add(LocalDate.now().plusDays(i).format(formatter));
        }
        JComboBox<String> cb = new JComboBox<>(dates.toArray(new String[0]));
        cb.setBackground(Color.WHITE);
        cb.setFont(FONT_BODY);
        return cb;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(30);
        table.setFont(FONT_BODY);
        table.setSelectionBackground(COL_BACKGROUND);
        table.setSelectionForeground(COL_PRIMARY);
        table.setShowVerticalLines(false);
        table.setDefaultEditor(Object.class, null); // Make non-editable

        JTableHeader header = table.getTableHeader();
        header.setBackground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBorder(new LineBorder(Color.LIGHT_GRAY, 0, false));

        // Listener to select the Route Object
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                // Get the Route ID from the hidden logic (column 0)
                String routeID = table.getValueAt(table.getSelectedRow(), 0).toString();

                // Find the backend object
                for(Route r : allRoutes) {
                    if(r.getRouteInfo().contains(routeID)) {
                        currentRoute = r;
                        // Update the map using the backend vehicle
                        updateSeatMap(r.getVehicleObject());
                        break;
                    }
                }
                totalFareLabel.setText("Please select a seat...");
            }
        });
    }

    private void styleSeatButton(JButton btn) {
        btn.setPreferredSize(new Dimension(45, 45));
        btn.setBackground(new Color(153, 255, 153));
        btn.setForeground(new Color(0, 100, 0));
        btn.setFocusPainted(false);
        btn.setBorder(new LineBorder(new Color(100, 200, 100)));
        btn.setFont(new Font("Arial", Font.BOLD, 10));
    }

    // --- CUSTOM BUTTON CLASS ---
    class RoundedButton extends JButton {
        public RoundedButton(String label) {
            super(label);
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setBackground(COL_PRIMARY);
            setForeground(COL_BACKGROUND);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { setBackground(COL_PRIMARY_DARK); repaint(); }
                public void mouseExited(MouseEvent e) { setBackground(COL_PRIMARY); repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            super.paintComponent(g);
            g2.dispose();
        }
    }
}