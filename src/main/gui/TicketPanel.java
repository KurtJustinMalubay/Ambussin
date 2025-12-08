package main.gui;

import main.exceptions.InvalidSeatException;
import main.managers.DataManager;
import main.managers.PassengerFactory;
import main.models.Bus;
import main.models.Passenger;
import main.models.Vehicle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TicketPanel {
    private JPanel mainPanel;
    private JPanel bodyPanel;
    private JLabel lblQr;
    private JPanel detailsPanel;
    private JButton btnConfirm;

    private Window parentWindow;
    private MainFrame controller;
    private Bus vehicle;
    private int row, col;
    private String pType;
    private double finalPrice;
    private String date;

    // 1. New field for Name
    private String passengerName;

    // 2. Updated Constructor to accept 'name'
    public TicketPanel(Window parentWindow, MainFrame controller, Bus v, int r, int c, String pType, String date, String name) {
        this.parentWindow = parentWindow;
        this.controller = controller;
        this.vehicle = v;
        this.row = r;
        this.col = c;
        this.pType = pType;
        this.date = date;
        this.passengerName = name; // Store it

        initStyling();
        displayData();

        btnConfirm.addActionListener(e -> confirmBooking());
    }

    private void initStyling() {
        lblQr.setText("");
        try{
            java.net.URL imgURL = getClass().getResource("/QR.png");
            if(imgURL != null){
                ImageIcon originalIcon = new ImageIcon(imgURL);
                Image scaledImage = originalIcon.getImage()
                        .getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                lblQr.setIcon(new ImageIcon(scaledImage));
            }
        }catch (Exception e){
            lblQr.setText(e.getMessage());
        }
        lblQr.setHorizontalAlignment(SwingConstants.CENTER);
    }

    private void displayData() {
        // Use the actual name for the factory (optional, but good practice)
        Passenger p = PassengerFactory.createPassenger(pType, passengerName, 0);
        this.finalPrice = p.computeFare(vehicle.getBasePrice());

        detailsPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 3. Added "Name" to the display and adjusted row numbers (0 to 7)
        addDetailRow(detailsPanel, gbc, 0, "Name:", passengerName);
        addDetailRow(detailsPanel, gbc, 1, "Date:", date);
        addDetailRow(detailsPanel, gbc, 2, "BusID:", vehicle.getVehicleID());
        addDetailRow(detailsPanel, gbc, 3, "BusType:", vehicle.getVehicleType());
        addDetailRow(detailsPanel, gbc, 4, "PassengerType:", pType);
        addDetailRow(detailsPanel, gbc, 5, "Destination:", ((Bus)vehicle).getDestination());
        addDetailRow(detailsPanel, gbc, 6, "Seat:", (row+1) + "-" + (col+1));
        addDetailRow(detailsPanel, gbc, 7, "Price:", String.format("%.2f", finalPrice));
    }

    private void addDetailRow(JPanel p, GridBagConstraints gbc, int row, String label, String value) {
        JLabel lblKey = new JLabel(label);
        lblKey.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0; gbc.anchor = GridBagConstraints.WEST;
        p.add(lblKey, gbc);

        JLabel lblVal = new JLabel(value);
        lblVal.setFont(new Font("Arial", Font.BOLD, 14));
        lblVal.setHorizontalAlignment(SwingConstants.RIGHT);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1.0; gbc.anchor = GridBagConstraints.EAST;
        p.add(lblVal, gbc);
    }

    private void confirmBooking() {
        try {
            vehicle.bookSeat(row, col);

            DataManager.getInstance().saveTransaction(
                    vehicle.getVehicleID(),
                    (row + 1) + "-" + (col + 1),
                    passengerName, // 4. Using the actual name variable here
                    pType,
                    finalPrice,
                    date
            );

            parentWindow.dispose();
            JOptionPane.showMessageDialog(null, "Ticket Created Successfully!");
            controller.goToLanding();

        } catch (InvalidSeatException e){
            JOptionPane.showMessageDialog(mainPanel, "Booking Failed: " + e.getMessage(), "Seat Error", JOptionPane.ERROR_MESSAGE);
            parentWindow.dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(mainPanel, "Error: " + e.getMessage());
        }
    }

    private void createUIComponents() {
        btnConfirm = new main.gui.components.RoundedButton("Confirm")
                .setNormalColor(new Color(244, 208, 63))
                .setHoverColor(new Color(255, 225, 100))
                .setPressedColor(new Color(200, 170, 50));
        btnConfirm.setPreferredSize(new Dimension(100, 25));
    }

    public JPanel getMainPanel() { return mainPanel; }
}