package main;
import main.models.*; // Imports backend models (Passenger, Route, Seat, Vehicle, Booking)

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import java.awt.Component;
import java.util.stream.Collectors;

public class BusBookingApp extends JFrame {

    // --- 1. THEME & CONSTANTS ---
    private static final Color PRIMARY_COLOR = new Color(203, 171, 84);    // Gold/Yellow
    private static final Color PRIMARY_DARK_COLOR = new Color(163, 131, 50);
    private static final Color BACKGROUND_COLOR = new Color(94, 17, 37);   // Maroon
    private static final Color LIGHT_BG_COLOR = new Color(245, 245, 245);  // Light Grey
    private static final Color TEXT_COLOR = new Color(50, 50, 50);
    private static final Color ACCENT_COLOR = new Color(220, 53, 69);      // Red (for errors/prices)

    private static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 28);
    private static final Font FONT_SUBHEADER = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);

    // Navigation IDs for CardLayout
    private static final String PAGE_LANDING = "LandingPage";
    private static final String PAGE_SEARCH = "SearchPage";
    private static final String PAGE_SELECTION = "SelectionPage";
    private static final String PAGE_SUMMARY = "SummaryPage";
    private static final String PAGE_SUCCESS = "SuccessPage";

    // --- DATA SOURCE: PHILIPPINE CITIES ---
    // [CUSTOMIZE HERE]: Add or remove cities to change available routes
    private static final String[] CITY_DATA = {
            // NCR
            "Manila", "Quezon City", "Makati", "Taguig", "Pasay", "Pasig",
            // LUZON
            "Baguio", "Laoag", "Vigan", "Tuguegarao", "Dagupan", "Angeles (Pampanga)",
            "Olongapo", "Batangas City", "Tagaytay", "Lucena", "Naga", "Legazpi", "Puerto Princesa",
            // VISAYAS
            "Cebu City", "Lapu-Lapu", "Mandaue", "Iloilo City", "Bacolod",
            "Dumaguete", "Tacloban", "Ormoc", "Tagbilaran", "Roxas City",
            // MINDANAO
            "Davao City", "Cagayan de Oro", "General Santos", "Zamboanga City",
            "Butuan", "Iligan", "Cotabato City", "Dipolog", "Pagadian", "Tagum"
    };

    // --- 2. UI COMPONENTS ---
    private final JPanel mainCardPanel;
    private final CardLayout cardLayout;

    // Input Fields
    private JTextField passengerNameField;
    private JComboBox<String> passengerTypeDropdown;
    private JComboBox<String> travelDateDropdown;
    private JComboBox<String> originDropdown;       // Searchable
    private JComboBox<String> destinationDropdown;  // Searchable

    // Dynamic Display Elements
    private JTable scheduleTable;
    private JLabel routeMapLabel;     // Shows "Origin -> Dest"
    private JLabel totalFareLabel;    // Shows real-time price
    private JPanel seatMapPanel;      // The grid of seat buttons

    // Summary Page Labels
    private JLabel summaryNameLabel;
    private JLabel summaryRouteLabel;
    private JLabel summarySeatLabel;
    private JLabel summaryPriceLabel;

    // --- 3. STATE MANAGEMENT (BACKEND CONNECTION) ---
    private final List<Route> databaseRoutes = new ArrayList<>(); // Acts as the temporary DB

    // Holds the data selected by the user during the process
    private Route currentRoute;
    private Seat currentSeat;
    private Booking currentBooking;

    // =================================================================================
    //  CONSTRUCTOR
    // =================================================================================
    public BusBookingApp() {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

        setTitle("AMBUSSIN Bus Booking System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(screen);
        setUndecorated(true);
        setLocationRelativeTo(null);

        cleanupOldBookings();
        initializeDatabase();

        // 2. Setup Layout
        cardLayout = new CardLayout();
        mainCardPanel = new JPanel(cardLayout);

        // 3. Initialize Reusable UI Components
        initializeComponents();

        // 4. Build and Add Pages
        mainCardPanel.add(createLandingPage(), PAGE_LANDING);
        mainCardPanel.add(createSearchPage(), PAGE_SEARCH);
        mainCardPanel.add(createSelectionPage(), PAGE_SELECTION);
        mainCardPanel.add(createSummaryPage(), PAGE_SUMMARY);
        mainCardPanel.add(createSuccessPage(), PAGE_SUCCESS);

        add(mainCardPanel);
        cardLayout.show(mainCardPanel, PAGE_LANDING);
        setVisible(true);
    }
    // INITIALIZE ROUTES, Fake Distance for simulation lang and more
    private void initializeDatabase() {
        databaseRoutes.clear();
        System.out.println("Initializing Database Routes...");

        for (int i = 0; i < CITY_DATA.length; i++) {
            for (int j = 0; j < CITY_DATA.length; j++) {

                // Prevent creating a route from Manila to Manila
                if (i == j) continue;

                String origin = CITY_DATA[i];
                String dest = CITY_DATA[j];
                String routeID = "R" + (i + 1) + "-" + (j + 1);

                // [SIMULATION]: Calculate a fake distance based on name length to vary price
                double fakeDistance = Math.abs(origin.hashCode() - dest.hashCode()) % 500;
                double baseFare = 150.0 + fakeDistance;

                // [SIMULATION]: Assign different bus types based on ID
                Vehicle vehicle;
                if ((i + j) % 3 == 0) {
                    vehicle = new Vehicle("BUS-" + routeID, "Deluxe Sleeper", 30);
                    baseFare += 500; // Premium price
                } else if ((i + j) % 2 == 0) {
                    vehicle = new Vehicle("BUS-" + routeID, "Aircon", 45);
                    baseFare += 200;
                } else {
                    vehicle = new Vehicle("BUS-" + routeID, "Non-Aircon", 50);
                }
                databaseRoutes.add(new Route(routeID, origin, dest, baseFare, vehicle));
            }
        }
        System.out.println("Database Ready: " + databaseRoutes.size() + " routes generated.");
    }
    // REMOVE UNWANTED FILES
    private void cleanupOldBookings() {
        File folder = new File("data_bookings");
        if (!folder.exists()) return;
        // 1. Generate the list of "Valid" file suffixes for the next 7 days
        // Format matches Vehicle.java: "Monday_Dec-6"
        List<String> validDateStrings = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMM d");
        for (int i = 0; i < 7; i++) {
            // Get date string (e.g., "Saturday, Dec 6")
            String rawDate = LocalDate.now().plusDays(i).format(formatter);
            // Convert to file format (e.g., "Saturday_Dec-6")
            String cleanDate = rawDate.replace(", ", "_").replace(" ", "-");
            validDateStrings.add(cleanDate);
        }
        // 2. Scan the folder and delete invalid files
        File[] files = folder.listFiles();
        if (files != null) {
            int deletedCount = 0;
            for (File f : files) {
                boolean keepFile = false;
                // Check if the filename ends with any of the valid date strings
                for (String validDate : validDateStrings) {
                    // We check for validDate + ".txt" to ensure exact match at the end
                    if (f.getName().endsWith(validDate + ".txt")) {
                        keepFile = true;
                        break;
                    }
                }
                // If the file date is not in our 7-day window, delete it
                if (!keepFile) {
                    if(f.delete()) {
                        deletedCount++;
                    }
                }
            }
            if (deletedCount > 0) {
                System.out.println("Maintenance: Cleared " + deletedCount + " old booking files.");
            }
        }
    }

    // CONTINUE INITIALIZING
    private void initializeComponents() {
        passengerNameField = createStyledTextField();

        // Dropdowns
        originDropdown = createSearchableDropdown();
        destinationDropdown = createSearchableDropdown();
        travelDateDropdown = createDateDropdown();

        passengerTypeDropdown = new JComboBox<>(new String[]{"REGULAR", "STUDENT", "SENIOR", "PWD"});
        passengerTypeDropdown.setBackground(Color.WHITE);
        passengerTypeDropdown.setFont(FONT_BODY);

        // Labels
        routeMapLabel = new JLabel("", SwingConstants.CENTER);
        totalFareLabel = new JLabel("Select a schedule", SwingConstants.CENTER);
        totalFareLabel.setFont(FONT_SUBHEADER);
        totalFareLabel.setForeground(BACKGROUND_COLOR);

        // Summary Labels
        summaryNameLabel = new JLabel();
        summaryRouteLabel = new JLabel();
        summarySeatLabel = new JLabel();
        summaryPriceLabel = new JLabel();

        // Table
        scheduleTable = new JTable();
        styleTable(scheduleTable);
    }

    //CREATION OF PAGES
    private JPanel createLandingPage() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BACKGROUND_COLOR);

        JLabel logo = new JLabel("AMBUSSIN", SwingConstants.CENTER);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 64));
        logo.setForeground(PRIMARY_COLOR);

        JLabel slogan = new JLabel("Luxury Travel. Affordable Prices.", SwingConstants.CENTER);
        slogan.setFont(new Font("Segoe UI", Font.ITALIC, 20));
        slogan.setForeground(Color.WHITE);

        RoundedButton startButton = new RoundedButton("BOOK A TICKET");
        startButton.setPreferredSize(new Dimension(250, 60));
        startButton.addActionListener(e -> cardLayout.show(mainCardPanel, PAGE_SEARCH));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(logo, gbc);

        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 40, 0);
        panel.add(slogan, gbc);

        gbc.gridy = 2;
        panel.add(startButton, gbc);

        return panel;
    }

    private JPanel createSearchPage() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(createHeaderBar("Find Your Trip"), BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(LIGHT_BG_COLOR);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(40, 60, 40, 60)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.gridx = 0; gbc.gridy = 0;

        // Form Fields
        formPanel.add(createLabel("Passenger Name"), gbc);
        gbc.gridy++;
        formPanel.add(passengerNameField, gbc);

        gbc.gridy++;
        formPanel.add(createLabel("Passenger Type (For Discounts)"), gbc);
        gbc.gridy++;
        formPanel.add(passengerTypeDropdown, gbc);

        gbc.gridy++;
        formPanel.add(createLabel("Travel Date"), gbc);
        gbc.gridy++;
        formPanel.add(travelDateDropdown, gbc);

        gbc.gridy++;
        formPanel.add(createLabel("Origin"), gbc);
        gbc.gridy++;
        formPanel.add(originDropdown, gbc);

        gbc.gridy++;
        formPanel.add(createLabel("Destination"), gbc);
        gbc.gridy++;
        formPanel.add(destinationDropdown, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(30, 0, 0, 0);
        RoundedButton searchButton = new RoundedButton("SEARCH SCHEDULES");
        searchButton.addActionListener(e -> performSearch());
        formPanel.add(searchButton, gbc);

        centerPanel.add(formPanel);
        panel.add(centerPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createSelectionPage() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(createHeaderBar("Select Schedule & Seat"), BorderLayout.NORTH);

        JPanel content = new JPanel(new GridLayout(1, 2, 20, 0));
        content.setBackground(LIGHT_BG_COLOR);
        content.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Left Side: Table & Map Info
        JPanel leftPanel = new JPanel(new BorderLayout(0, 20));
        leftPanel.setOpaque(false);

        JPanel scheduleContainer = createContainerPanel("AVAILABLE SCHEDULES");
        scheduleContainer.add(new JScrollPane(scheduleTable), BorderLayout.CENTER);

        JPanel mapContainer = createContainerPanel("ROUTE INFO");
        mapContainer.add(routeMapLabel, BorderLayout.CENTER);

        leftPanel.add(scheduleContainer, BorderLayout.CENTER);
        leftPanel.add(mapContainer, BorderLayout.SOUTH);

        // Right Side: Visual Seat Map
        JPanel rightPanel = createContainerPanel("SELECT YOUR SEAT");
        seatMapPanel = new JPanel(new GridBagLayout());
        seatMapPanel.setBackground(Color.WHITE);
        JScrollPane scroll = new JScrollPane(seatMapPanel);
        scroll.setBorder(null);
        rightPanel.add(scroll, BorderLayout.CENTER);

        JPanel farePanel = new JPanel(new BorderLayout());
        farePanel.setBackground(Color.WHITE);
        farePanel.add(totalFareLabel, BorderLayout.CENTER);

        RoundedButton proceedButton = new RoundedButton("PROCEED TO CHECKOUT");
        proceedButton.addActionListener(e -> validateAndProceed());
        farePanel.add(proceedButton, BorderLayout.SOUTH);
        rightPanel.add(farePanel, BorderLayout.SOUTH);

        content.add(leftPanel);
        content.add(rightPanel);
        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createSummaryPage() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BACKGROUND_COLOR);

        JPanel receiptCard = new JPanel(new GridBagLayout());
        receiptCard.setBackground(Color.WHITE);
        receiptCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(PRIMARY_COLOR, 4), new EmptyBorder(40, 60, 40, 60)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.CENTER;

        // Receipt Content
        JLabel logo = new JLabel("AMBUSSIN", SwingConstants.CENTER);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 30));
        logo.setForeground(BACKGROUND_COLOR);
        receiptCard.add(logo, gbc);

        gbc.gridy++;
        JLabel sub = new JLabel("TRIP SUMMARY", SwingConstants.CENTER);
        sub.setFont(FONT_BODY); sub.setForeground(Color.GRAY);
        receiptCard.add(sub, gbc);

        gbc.gridy++; gbc.insets = new Insets(20, 0, 20, 0);
        receiptCard.add(new JSeparator(SwingConstants.HORIZONTAL), gbc);

        gbc.insets = new Insets(5, 0, 5, 0); gbc.anchor = GridBagConstraints.WEST;
        summaryNameLabel.setFont(FONT_BOLD); receiptCard.add(summaryNameLabel, gbc);
        gbc.gridy++;
        summaryRouteLabel.setFont(FONT_BODY); receiptCard.add(summaryRouteLabel, gbc);
        gbc.gridy++;
        summarySeatLabel.setFont(FONT_BODY); receiptCard.add(summarySeatLabel, gbc);

        gbc.gridy++; gbc.insets = new Insets(30, 0, 5, 0);
        JLabel totalTxt = new JLabel("TOTAL AMOUNT");
        totalTxt.setForeground(Color.GRAY);
        receiptCard.add(totalTxt, gbc);

        gbc.gridy++; gbc.insets = new Insets(0, 0, 30, 0);
        summaryPriceLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        summaryPriceLabel.setForeground(ACCENT_COLOR);
        receiptCard.add(summaryPriceLabel, gbc);

        gbc.gridy++; gbc.anchor = GridBagConstraints.CENTER;
        RoundedButton payButton = new RoundedButton("PAY & CONFIRM");
        payButton.addActionListener(e -> confirmBookingAndShowTicket());
        receiptCard.add(payButton, gbc);

        panel.add(receiptCard);
        return panel;
    }

    private JPanel createSuccessPage() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BACKGROUND_COLOR);

        JLabel msg = new JLabel("Booking Confirmed!");
        msg.setFont(FONT_HEADER); msg.setForeground(Color.WHITE);

        RoundedButton homeButton = new RoundedButton("BOOK ANOTHER TRIP");
        homeButton.addActionListener(e -> {
            passengerNameField.setText("");
            originDropdown.setSelectedItem("");
            destinationDropdown.setSelectedItem("");
            cardLayout.show(mainCardPanel, PAGE_LANDING);
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx=0; gbc.gridy=0;
        panel.add(msg, gbc);
        gbc.gridy++; gbc.insets = new Insets(30,0,0,0);
        panel.add(homeButton, gbc);
        return panel;
    }

    // =================================================================================
    //  APP LOGIC (SEARCH, CALCULATE, BOOK)
    // =================================================================================

    private void performSearch() {
        String inputOrigin = (String) originDropdown.getEditor().getItem();
        String inputDest = (String) destinationDropdown.getEditor().getItem();
        // Validation
        if(passengerNameField.getText().isEmpty() || inputOrigin == null || inputDest == null || inputOrigin.isEmpty() || inputDest.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Missing Info", JOptionPane.WARNING_MESSAGE);
            return;
        }
        List<Route> foundRoutes = databaseRoutes.stream()
                .filter(r -> r.getOrigin().equalsIgnoreCase(inputOrigin) &&
                        r.getDestination().equalsIgnoreCase(inputDest))
                .toList();
        if (foundRoutes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No routes found from " + inputOrigin + " to " + inputDest, "No Schedules", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        currentRoute = null;
        currentSeat = null;
        currentBooking = null;

        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "TIME", "BUS INFO", "PRICE"}, 0);
        for (Route r : foundRoutes) {
            model.addRow(new Object[]{
                    r.getRouteInfo().split("\\[")[1].split("]")[0], // Extract ID for display
                    "08:00 AM",
                    r.getVehicle(),
                    String.format("%.2f", r.getBaseFare())
            });
        }
        scheduleTable.setModel(model);
        routeMapLabel.setText("<html><div style='text-align:center;'>" +
                "<h2 style='color:#5e1125'>" + inputOrigin.toUpperCase() + " <span style='color:#cbab54'>➝</span> " + inputDest.toUpperCase() + "</h2>" +
                "<i>Select a schedule to view seats...</i></div></html>");

        totalFareLabel.setText("Select a schedule");
        if(seatMapPanel != null) seatMapPanel.removeAll();

        cardLayout.show(mainCardPanel, PAGE_SELECTION);
    }
    /**
     * [CRITICAL] Updates the seat grid based on the File Database.
     */
    private void renderSeatMap(Vehicle vehicle) {
        seatMapPanel.removeAll();
        GridBagConstraints gbc = new GridBagConstraints();

        // Draw Driver
        gbc.gridwidth = 5; gbc.insets = new Insets(0,0,15,0);
        JLabel driver = new JLabel("DRIVER", SwingConstants.CENTER);
        driver.setOpaque(true); driver.setBackground(Color.LIGHT_GRAY);
        driver.setPreferredSize(new Dimension(200, 30));
        seatMapPanel.add(driver, gbc);

        // Draw Seats (Grid Layout Logic)
        gbc.gridwidth = 1; gbc.insets = new Insets(4, 4, 4, 4);
        int seatIndex = 1;

        for (int row = 0; row < 12; row++) {
            for (int col = 0; col < 5; col++) {
                gbc.gridx = col; gbc.gridy = row + 1;
                // Aisle logic (Column 2 is empty)
                if (col == 2) {
                    seatMapPanel.add(Box.createHorizontalStrut(20), gbc);
                } else {
                    Seat backendSeat = vehicle.findSeat(String.valueOf(seatIndex));

                    if (backendSeat != null) {
                        JButton seatBtn = new JButton(String.valueOf(seatIndex));
                        styleSeatButton(seatBtn);

                        // [LOGIC]: Check if booked in backend
                        if (!backendSeat.isAvailable()) {
                            seatBtn.setBackground(Color.LIGHT_GRAY);
                            seatBtn.setEnabled(false);
                            seatBtn.setToolTipText("Booked");
                        } else {
                            // Selection Action
                            seatBtn.addActionListener(e -> {
                                resetSeatButtonColors();
                                seatBtn.setBackground(PRIMARY_COLOR);
                                seatBtn.setForeground(BACKGROUND_COLOR);
                                currentSeat = backendSeat;
                                calculateTemporaryFare();
                            });
                        }
                        seatMapPanel.add(seatBtn, gbc);
                    }
                    seatIndex++;
                }
            }
        }
        seatMapPanel.revalidate();
        seatMapPanel.repaint();
    }

    private void calculateTemporaryFare() {
        if (currentRoute == null || currentSeat == null) return;

        String type = (String) passengerTypeDropdown.getSelectedItem();

        // Create temporary objects to run calculation logic
        Passenger tempPass = new Passenger("Temp", type);
        assert type != null;
        currentSeat.setPassengerType(type);
        Booking tempBooking = new Booking(tempPass, currentRoute, currentSeat);

        totalFareLabel.setText("Total Fare: PHP " + String.format("%.2f", tempBooking.getTotalFare()));
        currentSeat.release(); // Clean up state
    }

    private void validateAndProceed() {
        if (currentRoute == null || currentSeat == null) {
            //showBorderlessError(this, "Please select a schedule and a seat.");
            JOptionPane.showMessageDialog(this, "Please select a schedule and a seat.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Create Real Objects for Booking
        String name = passengerNameField.getText();
        String type = (String) passengerTypeDropdown.getSelectedItem();
        Passenger passenger = new Passenger(name, type);
        assert type != null;
        currentSeat.setPassengerType(type);
        currentBooking = new Booking(passenger, currentRoute, currentSeat);
        // Update Summary Page
        summaryNameLabel.setText("Passenger: " + passenger.getName() + " (" + type + ")");
        summaryRouteLabel.setText("<html><b>" + currentRoute.getOrigin() + " ➝ " + currentRoute.getDestination() + "</b><br>" + currentRoute.getVehicle() + "</html>");
        summarySeatLabel.setText("Seat Number: " + currentSeat.getSeatNumber());
        summaryPriceLabel.setText("PHP " + String.format("%.2f", currentBooking.getTotalFare()));
        cardLayout.show(mainCardPanel, PAGE_SUMMARY);
    }

    private void confirmBookingAndShowTicket() {
        if(currentBooking != null) {
            // 1. Confirm in Memory
            currentBooking.confirm();

            // 2. [CRITICAL] Save to Text File Database
            String selectedDate = (String) travelDateDropdown.getSelectedItem();
            assert selectedDate != null;
            currentRoute.getVehicleObject().saveBooking(selectedDate, currentSeat.getSeatNumber());
        }

        // 3. Generate HTML Ticket
        JDialog ticketDialog = new JDialog(this, "Boarding Pass", true);
        ticketDialog.setSize(500, 400);
        ticketDialog.setLocationRelativeTo(this);
        ticketDialog.setLayout(new BorderLayout());

        JPanel ticketPanel = new JPanel(new GridBagLayout());
        ticketPanel.setBackground(Color.WHITE);

        String rawTicket = currentBooking.generateTicket().replace("\n", "<br>");
        String html = String.format("<html><div style='font-family:monospace; width:350px; border:2px dashed gray; padding:15px;'>" +
                        "<h2 style='color:#5e1125'>AMBUSSIN TICKET</h2><hr>%s" +
                        "<br><div style='text-align:center; color:green;'>*** PAID ***</div></div></html>",
                rawTicket);

        ticketPanel.add(new JLabel(html));
        ticketDialog.add(ticketPanel, BorderLayout.CENTER);

        RoundedButton closeButton = new RoundedButton("PRINT & FINISH");
        closeButton.addActionListener(e -> {
            ticketDialog.dispose();
            cardLayout.show(mainCardPanel, PAGE_SUCCESS);
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(closeButton);
        ticketDialog.add(buttonPanel, BorderLayout.SOUTH);
        ticketDialog.setVisible(true);
    }

    // =================================================================================
    //  UTILITIES & STYLING HELPERS
    // =================================================================================

    /**
     * Styles the schedule table and adds the listener that loads file data when a row is clicked.
     */
    private void styleTable(JTable table) {
        table.setRowHeight(30);
        table.setFont(FONT_BODY);
        table.setDefaultEditor(Object.class, null); // Make non-editable

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                String routeID = table.getValueAt(table.getSelectedRow(), 0).toString();
                String selectedDate = (String) travelDateDropdown.getSelectedItem();

                // Find the Route Object
                for(Route r : databaseRoutes) {
                    if(r.getRouteInfo().contains(routeID)) {
                        currentRoute = r;
                        // [CRITICAL]: Load data from file for this specific bus and date
                        r.getVehicleObject().loadBookings(selectedDate);
                        renderSeatMap(r.getVehicleObject());
                        break;
                    }
                }
                totalFareLabel.setText("Please select a seat...");
            }
        });
    }

    /**
     * Adds "Type to Filter" logic to JComboBoxes.
     */
    private void addAutoCompleteLogic(JComboBox<String> comboBox) {
        final DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) comboBox.getModel();
        final JTextField editor = (JTextField) comboBox.getEditor().getEditorComponent();

        editor.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                SwingUtilities.invokeLater(() -> {
                    String text = editor.getText();
                    if (text.isEmpty()) return;

                    List<String> filtered = new ArrayList<>();
                    for (String city : CITY_DATA) {
                        if (city.toLowerCase().contains(text.toLowerCase())) filtered.add(city);
                    }

                    if (!filtered.isEmpty()) {
                        model.removeAllElements();
                        for (String s : filtered) model.addElement(s);
                        editor.setText(text);
                        comboBox.showPopup();
                    }
                });
            }
        });
    }

    private JComboBox<String> createSearchableDropdown() {
        JComboBox<String> cb = new JComboBox<>(CITY_DATA);
        cb.setEditable(true);
        cb.setBackground(Color.WHITE);
        cb.setFont(FONT_BODY);
        addAutoCompleteLogic(cb);
        return cb;
    }
    private void resetSeatButtonColors() {
        for(Component c : seatMapPanel.getComponents()) {
            if(c instanceof JButton && c.isEnabled()) {
                c.setBackground(new Color(153, 255, 153));
                c.setForeground(new Color(0, 100, 0));
            }
        }
    }
    // --- UI Factory Methods ---
    private JPanel createHeaderBar(String title) {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(BACKGROUND_COLOR);
        bar.setBorder(new EmptyBorder(15, 20, 15, 20));
        JLabel brand = new JLabel("AMBUSSIN", SwingConstants.LEFT);
        brand.setForeground(PRIMARY_COLOR); brand.setFont(new Font("Segoe UI", Font.BOLD, 20));
        JLabel screenTitle = new JLabel(title, SwingConstants.RIGHT);
        screenTitle.setForeground(Color.WHITE); screenTitle.setFont(FONT_BODY);
        bar.add(brand, BorderLayout.WEST); bar.add(screenTitle, BorderLayout.EAST);
        return bar;
    }
    private JTextField createStyledTextField() {
        JTextField tf = new JTextField(15);
        tf.setFont(FONT_BODY);
        tf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.GRAY, 1), new EmptyBorder(8, 8, 8, 8)));
        return tf;
    }
    private JPanel createContainerPanel(String title) {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(230,230,230), 1), new EmptyBorder(15, 15, 15, 15)));
        JLabel l = new JLabel(title);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12)); l.setForeground(Color.GRAY);
        p.add(l, BorderLayout.NORTH);
        return p;
    }
    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12)); l.setForeground(TEXT_COLOR);
        return l;
    }
    private JComboBox<String> createDateDropdown() {
        List<String> dates = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMM d");
        for (int i = 0; i < 7; i++) {
            dates.add(LocalDate.now().plusDays(i).format(formatter));
        }
        return new JComboBox<>(dates.toArray(new String[0]));
    }
    private void styleSeatButton(JButton btn) {
        btn.setPreferredSize(new Dimension(45, 45));
        btn.setBackground(new Color(153, 255, 153)); // Light Green
        btn.setForeground(new Color(0, 100, 0));
        btn.setFocusPainted(false);
        btn.setBorder(new LineBorder(new Color(100, 200, 100)));
        btn.setFont(new Font("Arial", Font.BOLD, 10));
    }
    // Custom Button Class
    static class RoundedButton extends JButton {
        public RoundedButton(String label) {
            super(label);
            setOpaque(false); setContentAreaFilled(false);
            setBorderPainted(false); setFocusPainted(false);
            setBackground(PRIMARY_COLOR); setForeground(BACKGROUND_COLOR);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { setBackground(PRIMARY_DARK_COLOR); repaint(); }
                public void mouseExited(MouseEvent e) { setBackground(PRIMARY_COLOR); repaint(); }
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
    private void showBorderlessError(Component parent, String message) {
        // Create the pane with the message and Error icon
        JOptionPane optionPane = new JOptionPane(message, JOptionPane.ERROR_MESSAGE);

        // Create the dialog. The title "Error" won't be seen, but is required by the code.
        JDialog dialog = optionPane.createDialog(parent, "Error");

        // Remove the title bar and X button
        dialog.setUndecorated(true);

        // Show the window
        dialog.setVisible(true);
    }
    // Main Entry Point
    public static void main(String[] args) {
        SwingUtilities.invokeLater(BusBookingApp::new);
    }
}