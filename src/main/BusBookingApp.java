package com.ambussin;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BusBookingApp extends JFrame {

    // --- THEME CONSTANTS ---
    // ts so fucking hard to deal with gng
    // pwede raman guro dili ni ninyo studyhan, inig ask ni sir kay pwede ra ako mo answer kay ako man gabuhat sa gui
    private static final Color COL_PRIMARY = new Color(203, 171, 84);    // Yellow
    private static final Color COL_PRIMARY_DARK = new Color(163, 131, 50); // Darker Yellow for hover
    private static final Color COL_BACKGROUND = new Color(94, 17, 37);   // Maroon
    private static final Color COL_LIGHT_BG = new Color(245, 245, 245);  // Light Grey for panels
    private static final Color COL_TEXT_DARK = new Color(50, 50, 50);
    private static final Color COL_ACCENT = new Color(220, 53, 69);      // Red for price/alerts

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

    // --- DATA FIELDS ---
    private JComboBox<String> dateDropdown;
    private JTextField nameField; // NEW: Passenger Name
    private JTextField originField;
    private JTextField destinationField;

    // --- DYNAMIC UI ELEMENTS ---
    private JTable scheduleTable;
    private JLabel routeEstimateLabel;
    private JLabel totalFareLabel;
    private JLabel routeMapLabel;

    // --- DETAILS SCREEN LABELS ---
    private JLabel detailsNameLabel; // NEW
    private JLabel detailsRouteLabel;
    private JLabel detailsSeatLabel;
    private JLabel detailsFareLabel;

    // --- LOGIC VARIABLES ---
    private String selectedSeatNumber = null;
    private List<JButton> seatButtons = new ArrayList<>();

    public BusBookingApp() {
        setTitle("AMBUSSIN Bus Booking System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 750);
        setLocationRelativeTo(null);

        // using a slight styling tweak for the flame guys
        setBackground(Color.WHITE);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Initialize Input Fields
        nameField = createStyledTextField();
        originField = createStyledTextField();
        destinationField = createStyledTextField();
        dateDropdown = createDateDropdown();

        // Initialize Labels
        scheduleTable = new JTable();
        styleTable(scheduleTable); // Custom styling method

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
    // 1. LANDING PAGE (Modern Splash Screen)
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

        // Layout centering
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(logo, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 40, 0);
        panel.add(slogan, gbc);

        gbc.gridy = 2;
        panel.add(startBtn, gbc);

        // Decorative footer
        JLabel footer = new JLabel("© 2025 Ambussin Transport Inc.", SwingConstants.CENTER);
        footer.setForeground(new Color(255, 255, 255, 100));
        gbc.gridy = 3;
        gbc.insets = new Insets(100, 0, 0, 0);
        panel.add(footer, gbc);

        return panel;
    }

    // =================================================================================
    // 2. SEARCH PAGE (Clean Form)
    // =================================================================================
    private JPanel createSearchPage() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(createTopBar("Start Your Journey"), BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(COL_LIGHT_BG);

        // White Card Container
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

        // Input Fields
        formCard.add(createLabel("Passenger Name"), gbc);
        gbc.gridy++;
        formCard.add(nameField, gbc);

        gbc.gridy++;
        formCard.add(createLabel("Travel Date"), gbc);
        gbc.gridy++;
        formCard.add(dateDropdown, gbc);

        gbc.gridy++;
        formCard.add(createLabel("Origin"), gbc);
        gbc.gridy++;
        formCard.add(originField, gbc);

        gbc.gridy++;
        formCard.add(createLabel("Destination"), gbc);
        gbc.gridy++;
        formCard.add(destinationField, gbc);

        // Search Button
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

        // Reset
        selectedSeatNumber = null;
        resetSeatColors();

        // Mock Data
        String date = (String) dateDropdown.getSelectedItem();
        Object[][] data = {
                {"08:00 AM", "Airconditioner (R001)", date},
                {"10:00 AM", "Non-Airconditioner (R002)", date},
                {"01:00 PM", "Airconditioner (R001)", date},
                {"03:30 PM", "Deluxe Sleeper (R005)", date}
        };
        scheduleTable.setModel(new DefaultTableModel(data, new String[]{"TIME", "BUS TYPE", "DATE"}));

        // Mock Map Text
        String origin = originField.getText().toUpperCase();
        String dest = destinationField.getText().toUpperCase();
        routeMapLabel.setText("<html><div style='text-align:center;'>" +
                "<h2 style='color:#5e1125'>" + origin + " <span style='color:#cbab54'>➝</span> " + dest + "</h2>" +
                "<i>Calculating optimal route...</i><br><br>" +
                "<b>Distance:</b> 45 km &nbsp;|&nbsp; <b>Traffic:</b> Moderate" +
                "</div></html>");

        totalFareLabel.setText("Select a schedule to view fare");

        cardLayout.show(cardPanel, CONFIRMATION_REVIEW);
    }

    // =================================================================================
    // 3. SELECTION PAGE (Split View)
    // =================================================================================
    private JPanel createSelectionPage() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(createTopBar("Select Schedule & Seat"), BorderLayout.NORTH);

        JPanel content = new JPanel(new GridLayout(1, 2, 20, 0)); // 2 Columns
        content.setBackground(COL_LIGHT_BG);
        content.setBorder(new EmptyBorder(20, 20, 20, 20));

        // LEFT COLUMN: Schedule & Map
        JPanel leftPanel = new JPanel(new BorderLayout(0, 20));
        leftPanel.setOpaque(false);

        JPanel scheduleContainer = createStyledPanel("AVAILABLE SCHEDULES");
        scheduleContainer.add(new JScrollPane(scheduleTable), BorderLayout.CENTER);

        JPanel mapContainer = createStyledPanel("ROUTE INFO");
        routeMapLabel = new JLabel("", SwingConstants.CENTER);
        mapContainer.add(routeMapLabel, BorderLayout.CENTER);
        mapContainer.add(routeEstimateLabel, BorderLayout.SOUTH);

        leftPanel.add(scheduleContainer, BorderLayout.CENTER); // Takes most space
        leftPanel.add(mapContainer, BorderLayout.SOUTH);

        // RIGHT COLUMN: Visual Bus Seat Map
        JPanel rightPanel = createStyledPanel("SELECT YOUR SEAT");
        rightPanel.add(createVisualBusLayout(), BorderLayout.CENTER);

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

    private JPanel createVisualBusLayout() {
        JPanel busPanel = new JPanel(new GridBagLayout());
        busPanel.setBackground(Color.WHITE);

        // Driver Seat Indicator
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 5;
        gbc.insets = new Insets(0,0,15,0);
        JLabel driver = new JLabel("DRIVER", SwingConstants.CENTER);
        driver.setOpaque(true);
        driver.setBackground(Color.LIGHT_GRAY);
        driver.setPreferredSize(new Dimension(200, 30));
        busPanel.add(driver, gbc);

        // Seat Grid (2 - Aisle - 2)
        gbc.gridwidth = 1;
        gbc.insets = new Insets(4, 4, 4, 4);

        int seatCount = 1;
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 5; col++) {
                gbc.gridx = col;
                gbc.gridy = row + 1;

                if (col == 2) {
                    // Aisle (Empty space)
                    busPanel.add(Box.createHorizontalStrut(20), gbc);
                } else {
                    // Seat Button
                    JButton seat = new JButton(String.valueOf(seatCount));
                    styleSeatButton(seat);

                    // Mock Logic: seat 3, 15, 22 are booked
                    if(seatCount == 3 || seatCount == 15 || seatCount == 22) {
                        seat.setBackground(new Color(220, 220, 220)); // Grey
                        seat.setEnabled(false);
                        seat.setBorder(new LineBorder(Color.GRAY));
                    } else {
                        seatButtons.add(seat); // Track available seats
                        seat.addActionListener(e -> {
                            resetSeatColors();
                            seat.setBackground(COL_PRIMARY);
                            seat.setForeground(COL_BACKGROUND);
                            selectedSeatNumber = seat.getText();
                        });
                    }

                    busPanel.add(seat, gbc);
                    seatCount++;
                }
            }
        }

        JScrollPane scroll = new JScrollPane(busPanel);
        scroll.setBorder(null);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(scroll, BorderLayout.CENTER);

        // Legend
        JPanel legend = new JPanel(new FlowLayout());
        legend.setBackground(Color.WHITE);
        legend.add(createLegendDot(new Color(153, 255, 153), "Available"));
        legend.add(createLegendDot(Color.LIGHT_GRAY, "Booked"));
        legend.add(createLegendDot(COL_PRIMARY, "Selected"));
        wrapper.add(legend, BorderLayout.SOUTH);

        return wrapper;
    }

    private void validateAndProceed() {
        if (scheduleTable.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(this, "Please select a bus schedule.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (selectedSeatNumber == null) {
            JOptionPane.showMessageDialog(this, "Please select a seat.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Gather Info
        String time = scheduleTable.getValueAt(scheduleTable.getSelectedRow(), 0).toString();
        String bus = scheduleTable.getValueAt(scheduleTable.getSelectedRow(), 1).toString();
        String fare = totalFareLabel.getText().replace("Calculated Fare: ", "");

        // Set Labels
        detailsNameLabel.setText("Passenger: " + nameField.getText());
        detailsRouteLabel.setText("<html><b>" + originField.getText().toUpperCase() + " ➝ " + destinationField.getText().toUpperCase() + "</b><br>" + time + " - " + bus + "</html>");
        detailsSeatLabel.setText("Seat Number: " + selectedSeatNumber);
        detailsFareLabel.setText(fare);

        cardLayout.show(cardPanel, ORDER_DETAILS);
    }

    // =================================================================================
    // 4. SUMMARY PAGE (Receipt Style)
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

        // Logo
        JLabel logo = new JLabel("AMBUSSIN", SwingConstants.CENTER);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 30));
        logo.setForeground(COL_BACKGROUND);
        receipt.add(logo, gbc);

        gbc.gridy++;
        JLabel sub = new JLabel("TRIP SUMMARY", SwingConstants.CENTER);
        sub.setFont(FONT_BODY);
        sub.setForeground(Color.GRAY);
        receipt.add(sub, gbc);

        // Divider
        gbc.gridy++;
        gbc.insets = new Insets(20, 0, 20, 0);
        receipt.add(new JSeparator(SwingConstants.HORIZONTAL), gbc);

        // Info
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

        // Total
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

        // Buttons
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.setBackground(Color.WHITE);

        RoundedButton edit = new RoundedButton("EDIT");
        edit.setBackground(Color.LIGHT_GRAY);
        edit.setForeground(Color.BLACK);
        edit.addActionListener(e -> cardLayout.show(cardPanel, CONFIRMATION_REVIEW));

        RoundedButton pay = new RoundedButton("PAY & CONFIRM");
        pay.addActionListener(e -> showTicketDialog());

        btnPanel.add(edit);
        btnPanel.add(pay);
        receipt.add(btnPanel, gbc);

        panel.add(receipt);
        return panel;
    }

    // =================================================================================
    // 5. SUCCESS PAGE & TICKET
    // =================================================================================
    private void showTicketDialog() {
        JDialog d = new JDialog(this, "Boarding Pass", true);
        d.setSize(500, 300);
        d.setLocationRelativeTo(this);
        d.setLayout(new BorderLayout());

        JPanel ticket = new JPanel(new GridBagLayout());
        ticket.setBackground(Color.WHITE);

        String html = String.format("<html><div style='font-family:sans-serif; width:350px; border:2px dashed gray; padding:15px;'>" +
                        "<h2 style='color:#5e1125'>AMBUSSIN <span style='font-size:10px; color:gray'>BOARDING PASS</span></h2>" +
                        "<hr>" +
                        "<b>Passenger:</b> %s<br>" +
                        "<b>Route:</b> %s ➝ %s<br>" +
                        "<b>Seat:</b> <span style='font-size:20px; color:#cbab54'>%s</span><br>" +
                        "<br><div style='text-align:right; color:green'>PAID: %s</div>" +
                        "</div></html>",
                nameField.getText(), originField.getText(), destinationField.getText(), selectedSeatNumber, detailsFareLabel.getText());

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
            // Reset fields
            nameField.setText("");
            originField.setText("");
            destinationField.setText("");
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

        JTableHeader header = table.getTableHeader();
        header.setBackground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBorder(new LineBorder(Color.LIGHT_GRAY, 0, false));

        // Listener to update Fare
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                String type = table.getValueAt(table.getSelectedRow(), 1).toString();
                if (type.contains("Non-Air")) totalFareLabel.setText("Calculated Fare: PHP 150.00");
                else if (type.contains("Sleeper")) totalFareLabel.setText("Calculated Fare: PHP 450.00");
                else totalFareLabel.setText("Calculated Fare: PHP 180.00");
            }
        });
    }

    private void styleSeatButton(JButton btn) {
        btn.setPreferredSize(new Dimension(45, 45));
        btn.setBackground(new Color(153, 255, 153)); // Light Green default
        btn.setForeground(new Color(0, 100, 0));
        btn.setFocusPainted(false);
        btn.setBorder(new LineBorder(new Color(100, 200, 100)));
        btn.setFont(new Font("Arial", Font.BOLD, 10));
    }

    private void resetSeatColors() {
        for(JButton btn : seatButtons) {
            if(btn.isEnabled()) {
                btn.setBackground(new Color(153, 255, 153));
                btn.setForeground(new Color(0, 100, 0));
            }
        }
    }

    private JPanel createLegendDot(Color c, String text) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        p.setBackground(Color.WHITE);
        JLabel dot = new JLabel("●");
        dot.setForeground(c);
        dot.setFont(new Font("Arial", Font.PLAIN, 20));
        p.add(dot);
        p.add(new JLabel(text));
        return p;
    }

    // --- CUSTOM BUTTON CLASS FOR MODERN LOOK ---
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