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

    private LandingPanel landing;
    private BookingPanel booking;
    private SelectionPanel selection;
    private AdminPanel admin;

    public MainFrame() {
        setTitle("Ambussin: Kiosk");
        setUndecorated(true);

        /*TEST*/
        Dimension screen = getToolkit().getScreenSize();
        Dimension preferred = new Dimension(1024, 756);
        setSize(screen);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cards = new CardLayout();
        mainPanel = new JPanel(cards);

        List<Vehicle> data;
        try {
            data = DataManager.getInstance().loadVehicles();
            DataManager.getInstance().loadBookings(data);
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Error loading database: " + e.getMessage());
            data = List.of();
        }

        landing = new LandingPanel(this);
        booking = new BookingPanel(this, data);
        selection = new SelectionPanel(this, data);
        admin = new AdminPanel(this, data);

        mainPanel.add(landing.getMainPanel(), "LANDING");
        mainPanel.add(booking.getMainPanel(), "BOOKING");
        mainPanel.add(selection.getMainPanel(), "SELECTION");
        mainPanel.add(admin.getMainPanel(), "ADMIN");

        add(mainPanel);
        goToLanding();
    }

    public void goToLanding() { cards.show(mainPanel, "LANDING"); }
    public void goToBooking() {
        booking.resetForm();
        cards.show(mainPanel, "BOOKING");
    }
    public void goToSelection(String dest, String busType, String pType, String date, String name) {
        selection.loadResults(dest, busType, pType, date, name);
        cards.show(mainPanel, "SELECTION");
    }
    public void showAdmin() { admin.refreshLogs();cards.show(mainPanel, "ADMIN"); }

    public void refreshApp(){
        try{
            List<Vehicle> newData = DataManager.getInstance().loadVehicles();
            DataManager.getInstance().loadBookings(newData);
            booking.reloadData(newData);
            selection.update(newData);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }
}