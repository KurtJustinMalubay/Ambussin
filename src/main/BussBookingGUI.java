package main;

import main.models.*; // Assumes your Passenger, Route, Seat, Vehicle, Booking classes are here

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BussBookingGUI extends JFrame {

    // --- GUI DESIGNER BINDINGS (Must match .form "binding" attributes) ---
    private JPanel mainCardPanel;

    // Landing Page
    private JButton landingStartButton; // Custom Create

    // Search Page
    private JTextField passengerNameField;
    private JComboBox<String> passengerTypeDropdown;
    private JComboBox<String> travelDateDropdown;
    private JComboBox<String> originDropdown;
    private JComboBox<String> destinationDropdown;
    private JButton searchButton; // Custom Create

    // Selection Page
    private JTable scheduleTable;
    private JLabel routeMapLabel;
    private JPanel seatMapPanel; // Custom Create
    private JLabel totalFareLabel;
    private JButton proceedButton; // Custom Create

    // Summary Page
    private JLabel summaryNameLabel;
    private JLabel summaryRouteLabel;
    private JLabel summarySeatLabel;
    private JLabel summaryPriceLabel;
    private JButton payButton; // Custom Create

    // Success Page
    private JButton homeButton; // Custom Create

    // --- LOGIC VARIABLES ---
    private static final String PAGE_LANDING = "LandingPage";
    private static final String PAGE_SEARCH = "SearchPage";
    private static final String PAGE_SELECTION = "SelectionPage";
    private static final String PAGE_SUMMARY = "SummaryPage";
    private static final String PAGE_SUCCESS = "SuccessPage";

    private final List<Route> databaseRoutes = new ArrayList<>();
    private static final String[] CITY_DATA = {
            "Manila", "Quezon City", "Makati", "Taguig", "Pasay", "Pasig",
            "Baguio", "Laoag", "Vigan", "Tuguegarao", "Dagupan", "Angeles (Pampanga)",
            "Batangas City", "Tagaytay", "Naga", "Legazpi", "Puerto Princesa",
            "Cebu City", "Iloilo City", "Bacolod", "Davao City", "Cagayan de Oro"
    };

    private Route currentRoute;
    private Seat currentSeat;
    private Booking currentBooking;

    public BussBookingGUI() {
        setTitle("AMBUSSIN Bus Booking System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screen);
        setLocationRelativeTo(null);

        cleanupOldBookings();
        initializeDatabase();

        setupComponentStyles();

        setContentPane(mainCardPanel);

        listeners();

        setVisible(true);
    }
    private void createUIComponents() {
        // Buttons
        landingStartButton = new RoundedButton("BOOK A TICKET");
        searchButton = new RoundedButton("SEARCH SCHEDULES");
        proceedButton = new RoundedButton("PROCEED TO CHECKOUT");
        payButton = new RoundedButton("PAY & CONFIRM");
        homeButton = new RoundedButton("BOOK ANOTHER TRIP");

        // Panels
        seatMapPanel = new JPanel(new GridBagLayout());
        seatMapPanel.setBackground(Color.WHITE);
    }

    private void listeners() {
        CardLayout cl = (CardLayout) mainCardPanel.getLayout();

        landingStartButton.addActionListener(e -> cl.show(mainCardPanel, PAGE_SEARCH));

        searchButton.addActionListener(e -> performSearch(cl));

        scheduleTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && scheduleTable.getSelectedRow() != -1) {
                handleTableSelection();
            }
        });

        proceedButton.addActionListener(e -> {
            if (validateSelection()) {
                updateSummaryDetails();
                cl.show(mainCardPanel, PAGE_SUMMARY);
            }
        });

        payButton.addActionListener(e -> {
            confirmBookingAndShowTicket();
        });

        // 6. Home -> Restart App
        homeButton.addActionListener(e -> {
            passengerNameField.setText("");
            originDropdown.setSelectedItem("");
            destinationDropdown.setSelectedItem("");
            cl.show(mainCardPanel, PAGE_LANDING);
        });

        // 7. Auto-Complete Logic for Dropdowns
        addAutoComplete(originDropdown);
        addAutoComplete(destinationDropdown);
    }

    private void performSearch(CardLayout cl) {
        String inputOrigin = (String) originDropdown.getEditor().getItem();
        String inputDest = (String) destinationDropdown.getEditor().getItem();

        if (passengerNameField.getText().isEmpty() || inputOrigin == null || inputDest == null) {
            showBorderlessError("Please fill in all fields.");
            return;
        }

        List<Route> foundRoutes = databaseRoutes.stream()
                .filter(r -> r.getOrigin().equalsIgnoreCase(inputOrigin) && r.getDestination().equalsIgnoreCase(inputDest))
                .toList();

        if (foundRoutes.isEmpty()) {
            showBorderlessError("No routes found from " + inputOrigin + " to " + inputDest);
            return;
        }

        currentRoute = null;
        currentSeat = null;
        currentBooking = null;
        seatMapPanel.removeAll();
        seatMapPanel.repaint();
        totalFareLabel.setText("Select a schedule");

        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "TIME", "BUS INFO", "PRICE"}, 0);
        for (Route r : foundRoutes) {
            model.addRow(new Object[]{
                    r.getRouteInfo().split("\\[")[1].split("]")[0],
                    "08:00 AM",
                    r.getVehicle(),
                    String.format("%.2f", r.getBaseFare())
            });
        }
        scheduleTable.setModel(model);
        routeMapLabel.setText("<html><center><h2>" + inputOrigin + " ➝ " + inputDest + "</h2></center></html>");

        cl.show(mainCardPanel, PAGE_SELECTION);
    }

    private void handleTableSelection() {
        String routeID = scheduleTable.getValueAt(scheduleTable.getSelectedRow(), 0).toString();
        String selectedDate = (String) travelDateDropdown.getSelectedItem();

        for (Route r : databaseRoutes) {
            if (r.getRouteInfo().contains(routeID)) {
                currentRoute = r;
                r.getVehicleObject().loadBookings(selectedDate);
                renderSeatMap(r.getVehicleObject());
                break;
            }
        }
        totalFareLabel.setText("Please select a seat...");
    }

    private void renderSeatMap(Vehicle vehicle) {
        seatMapPanel.removeAll();
        GridBagConstraints gbc = new GridBagConstraints();

        // Driver
        gbc.gridwidth = 5; gbc.insets = new Insets(0, 0, 15, 0);
        JLabel driver = new JLabel("DRIVER", SwingConstants.CENTER);
        driver.setOpaque(true); driver.setBackground(Color.LIGHT_GRAY);
        driver.setPreferredSize(new Dimension(200, 30));
        seatMapPanel.add(driver, gbc);

        // Seats
        gbc.gridwidth = 1; gbc.insets = new Insets(4, 4, 4, 4);
        int seatIndex = 1;

        for (int row = 0; row < 12; row++) {
            for (int col = 0; col < 5; col++) {
                gbc.gridx = col; gbc.gridy = row + 1;

                if (col == 2) {
                    seatMapPanel.add(Box.createHorizontalStrut(20), gbc); // Aisle
                } else {
                    Seat backendSeat = vehicle.findSeat(String.valueOf(seatIndex));
                    if (backendSeat != null) {
                        JButton seatBtn = new JButton(String.valueOf(seatIndex));
                        styleSeatButton(seatBtn);

                        if (!backendSeat.isAvailable()) {
                            seatBtn.setBackground(Color.LIGHT_GRAY);
                            seatBtn.setEnabled(false);
                        } else {
                            // SEAT CLICK LISTENER
                            seatBtn.addActionListener(e -> {
                                resetSeatColors();
                                seatBtn.setBackground(new Color(203, 171, 84)); // Gold
                                seatBtn.setForeground(new Color(94, 17, 37));   // Maroon
                                currentSeat = backendSeat;
                                calculateFare();
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

    private void calculateFare() {
        if (currentRoute == null || currentSeat == null) return;
        String type = (String) passengerTypeDropdown.getSelectedItem();
        Passenger tempPass = new Passenger("Temp", type);
        currentSeat.setPassengerType(type);
        Booking tempBooking = new Booking(tempPass, currentRoute, currentSeat);
        totalFareLabel.setText("Total Fare: PHP " + String.format("%.2f", tempBooking.getTotalFare()));
        currentSeat.release();
    }

    private boolean validateSelection() {
        if (currentRoute == null || currentSeat == null) {
            showBorderlessError("Please select a schedule and a seat.");
            return false;
        }
        return true;
    }

    private void updateSummaryDetails() {
        String name = passengerNameField.getText();
        String type = (String) passengerTypeDropdown.getSelectedItem();

        Passenger passenger = new Passenger(name, type);
        currentSeat.setPassengerType(type); // Set definitive type
        currentBooking = new Booking(passenger, currentRoute, currentSeat);

        summaryNameLabel.setText("Passenger: " + name + " (" + type + ")");
        summaryRouteLabel.setText("Route: " + currentRoute.getOrigin() + " ➝ " + currentRoute.getDestination());
        summarySeatLabel.setText("Seat Number: " + currentSeat.getSeatNumber());
        summaryPriceLabel.setText("PHP " + String.format("%.2f", currentBooking.getTotalFare()));
    }

    //KA JOEL CODE
    private void confirmBookingAndShowTicket() {
        if (currentBooking != null) {
            currentBooking.confirm();
            String selectedDate = (String) travelDateDropdown.getSelectedItem();
            currentRoute.getVehicleObject().saveBooking(selectedDate, currentSeat.getSeatNumber());
        }
        // Generate Ticket HTML
        String rawTicket = currentBooking.generateTicket().replace("\n", "<br>");
        String html = String.format("<html><div style='font-family:monospace; width:300px; padding:10px;'>" +
                "<h2 style='color:#5e1125; text-align:center;'>AMBUSSIN TICKET</h2><hr>%s" +
                "<br><div style='text-align:center; color:green;'>*** PAID ***</div></div></html>", rawTicket);

        // Show Custom Dialog
        JDialog ticketDialog = new JDialog(this, "Boarding Pass", true);
        ticketDialog.setUndecorated(true);
        ticketDialog.setSize(400, 450);
        ticketDialog.setLocationRelativeTo(this);

        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(new LineBorder(new Color(203, 171, 84), 2));

        p.add(new JLabel(html, SwingConstants.CENTER), BorderLayout.CENTER);

        RoundedButton closeBtn = new RoundedButton("PRINT & FINISH");
        closeBtn.addActionListener(e -> {
            ticketDialog.dispose();
            ((CardLayout) mainCardPanel.getLayout()).show(mainCardPanel, PAGE_SUCCESS);
        });

        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(closeBtn);
        p.add(btnPanel, BorderLayout.SOUTH);

        ticketDialog.add(p);
        ticketDialog.setVisible(true);
    }

    private void setupComponentStyles() {
        List<String> dates = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMM d");
        for (int i = 0; i < 7; i++) dates.add(LocalDate.now().plusDays(i).format(formatter));
        DefaultComboBoxModel<String> dateModel = new DefaultComboBoxModel<>(dates.toArray(new String[0]));
        travelDateDropdown.setModel(dateModel);

        passengerTypeDropdown.setModel(new DefaultComboBoxModel<>(new String[]{"REGULAR", "STUDENT", "SENIOR", "PWD"}));

        originDropdown.setModel(new DefaultComboBoxModel<>(CITY_DATA));
        destinationDropdown.setModel(new DefaultComboBoxModel<>(CITY_DATA));

        // Table Style
        scheduleTable.setRowHeight(30);
        scheduleTable.setDefaultEditor(Object.class, null);
    }

    private void addAutoComplete(JComboBox<String> comboBox) {
        DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) comboBox.getModel();
        JTextField editor = (JTextField) comboBox.getEditor().getEditorComponent();

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
    private void styleSeatButton(JButton btn) {
        btn.setPreferredSize(new Dimension(45, 45));
        btn.setBackground(new Color(153, 255, 153));
        btn.setForeground(new Color(0, 100, 0));
        btn.setFocusPainted(false);
        btn.setBorder(new LineBorder(new Color(100, 200, 100)));
        btn.setFont(new Font("Arial", Font.BOLD, 10));
    }

    private void resetSeatColors() {
        for (Component c : seatMapPanel.getComponents()) {
            if (c instanceof JButton && c.isEnabled()) {
                c.setBackground(new Color(153, 255, 153));
                c.setForeground(new Color(0, 100, 0));
            }
        }
    }

    private void showBorderlessError(String message) {
        JOptionPane pane = new JOptionPane(message, JOptionPane.ERROR_MESSAGE);
        JDialog d = pane.createDialog(this, "Error");
//        d.setUndecorated(true);
        d.setVisible(true);
    }

    //RANDOMIZE PATH NI FOR FARE
    private void initializeDatabase() {
        databaseRoutes.clear();
        for (int i = 0; i < CITY_DATA.length; i++) {
            for (int j = 0; j < CITY_DATA.length; j++) {
                if (i == j) continue;
                String origin = CITY_DATA[i];
                String dest = CITY_DATA[j];
                String routeID = "R" + (i + 1) + "-" + (j + 1);
                double baseFare = 150.0 + (Math.abs(origin.hashCode() - dest.hashCode()) % 500);

                Vehicle vehicle;
                if ((i + j) % 3 == 0) vehicle = new Vehicle("BUS-" + routeID, "Deluxe Sleeper", 30);
                else if ((i + j) % 2 == 0) vehicle = new Vehicle("BUS-" + routeID, "Aircon", 45);
                else vehicle = new Vehicle("BUS-" + routeID, "Non-Aircon", 50);

                databaseRoutes.add(new Route(routeID, origin, dest, baseFare, vehicle));
            }
        }
    }

    private void cleanupOldBookings() {
        File folder = new File("data_bookings");
        if (!folder.exists()) return;
        List<String> validDateStrings = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMM d");
        for (int i = 0; i < 7; i++) {
            String cleanDate = LocalDate.now().plusDays(i).format(formatter).replace(", ", "_").replace(" ", "-");
            validDateStrings.add(cleanDate);
        }
        File[] files = folder.listFiles();
        if (files != null) {
            for (File f : files) {
                boolean keep = false;
                for (String valid : validDateStrings) if (f.getName().endsWith(valid + ".txt")) keep = true;
                if (!keep) f.delete();
            }
        }
    }

    //FROM JOEL CODE
    static class RoundedButton extends JButton {
        public RoundedButton(String label) {
            super(label);
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setBackground(new Color(203, 171, 84));
            setForeground(new Color(94, 17, 37));
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { setBackground(new Color(163, 131, 50)); repaint(); }
                public void mouseExited(MouseEvent e) { setBackground(new Color(203, 171, 84)); repaint(); }
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BussBookingGUI::new);
    }
}