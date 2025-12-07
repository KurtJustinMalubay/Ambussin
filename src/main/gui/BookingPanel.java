package main.gui;

import main.models.Bus;
import main.models.Vehicle;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BookingPanel {
    private JPanel mainPanel;
    private JComboBox<String> cmbDate;
    private JComboBox<String> cmbDest;
    private JComboBox<String> cmbPassenger;
    private JPanel radioPanel;
    private JRadioButton rbAircon;
    private JRadioButton rbStandard;
    private JButton btnSearch;
    private JPanel bodyPanel;
    private MainFrame controller;

    private List<Vehicle> allVehicles;

    private static final String DATE_PLACEHOLDER = "Select Date...";
    private static final String DEST_PLACEHOLDER = "Destination...";
    private static final String TYPE_PLACEHOLDER = "Type...";

    public BookingPanel(MainFrame controller, List<Vehicle> vehicles) {
        this.controller = controller;
        this.allVehicles = vehicles;

        setupDateSelector();

        // Destinations
        setupAutoRemovePlaceholder(cmbDest, DEST_PLACEHOLDER);
        Set<String> dests = new HashSet<>();
        for (Vehicle v : vehicles) {
            if (v instanceof Bus) {
                dests.add(((Bus) v).getDestination());
            }
        }
        for (String d : dests) cmbDest.addItem(d);

        // Passenger Types
        setupAutoRemovePlaceholder(cmbPassenger, TYPE_PLACEHOLDER);
        cmbPassenger.addItem("Regular");
        cmbPassenger.addItem("Student");
        cmbPassenger.addItem("Senior");
        cmbPassenger.addItem("PWD");

        stylePlaceholders();

        // Radios
        ButtonGroup bg = new ButtonGroup();
        bg.add(rbAircon); bg.add(rbStandard);
        rbAircon.setSelected(true);

        btnSearch.addActionListener(e -> search());
    }

    private void setupAutoRemovePlaceholder(JComboBox<String> box, String placeholder) {
        box.addItem(placeholder);

        box.addActionListener(e -> {
            Object selected = box.getSelectedItem();
            if (selected != null && !selected.equals(placeholder)) {
                box.removeItem(placeholder);
            }
        });
    }

    private void setupDateSelector() {
        setupAutoRemovePlaceholder(cmbDate, DATE_PLACEHOLDER);
        LocalDate localDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        for(int i = 0; i < 7; i++){
            LocalDate nextDate = localDate.plusDays(i);
            cmbDate.addItem(nextDate.format(formatter));
        }
    }

    private void search(){
        String date = (String) cmbDate.getSelectedItem();
        String dest = (String) cmbDest.getSelectedItem();
        String pType = (String) cmbPassenger.getSelectedItem();

        if (date == null || date.equals("Select Date...")) {
            JOptionPane.showMessageDialog(mainPanel, "Please select a Travel Date.");
            return;
        }
        if (dest == null || dest.equals("Destination...")) {
            JOptionPane.showMessageDialog(mainPanel, "Please select a Destination.");
            return;
        }
        if (pType == null || pType.equals("Type...")) {
            JOptionPane.showMessageDialog(mainPanel, "Please select a Passenger Type.");
            return;
        }

        String busType = rbAircon.isSelected() ? "Aircon" : "Standard";

        boolean busExists = false;
        for(Vehicle v : allVehicles){
            if(v instanceof Bus b) {
                if(b.getDestination().equalsIgnoreCase(dest) &&
                    b.getVehicleType().toLowerCase().contains(busType.toLowerCase())){
                    busExists = true;
                    break;
                }
            }
        }
        if(!busExists){
            JOptionPane.showMessageDialog(mainPanel,
                    "No " + busType + " buses available for " + dest + ".", "Route Unvailable", JOptionPane.WARNING_MESSAGE);
            return;
        }
        controller.goToSelection(dest, busType, pType, date);
    }

    private void stylePlaceholders() {
        DefaultListCellRenderer renderer = new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if ("Select Destination...".equals(value) ||
                        "Select Type...".equals(value) ||
                        "Select Date...".equals(value)) {
                    setForeground(Color.GRAY);
                } else {
                    setForeground(Color.BLACK);
                }
                return this;
            }
        };
        cmbDest.setRenderer(renderer);
        cmbPassenger.setRenderer(renderer);
        cmbDate.setRenderer(renderer);
    }

    public void reloadData(List<Vehicle> vehicles) {
        cmbDest.removeAllItems();
        setupAutoRemovePlaceholder(cmbDest, DEST_PLACEHOLDER);
        Set<String> dests = new HashSet<>();
        for (Vehicle v : vehicles) {
            if (v instanceof Bus) { dests.add(((Bus) v).getDestination()); }
        }
        for(String d : dests) cmbDest.addItem(d);
        stylePlaceholders();
    }

    public JPanel getMainPanel() { return mainPanel; }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        btnSearch = new main.gui.components.RoundedButton("Search Schedules").setNormalColor(new Color(244, 208, 63))
                .setHoverColor(new Color(255, 225, 100))
                .setPressedColor(new Color(200, 170, 50));
        bodyPanel = new main.gui.components.ImagePanel("/Cool_bg.png");
        bodyPanel.setLayout(new BorderLayout());
    }
}