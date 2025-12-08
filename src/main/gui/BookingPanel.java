package main.gui;

import main.models.Bus;
import main.models.Vehicle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
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
    private JTextField nameField;
    private MainFrame controller;

    private List<Vehicle> allVehicles;

    private static final String DATE_PLACEHOLDER = "Select Date...";
    private static final String DEST_PLACEHOLDER = "Destination...";
    private static final String TYPE_PLACEHOLDER = "Type...";
    private static final String NAME_PLACEHOLDER = "Passenger Name";

    public BookingPanel(MainFrame controller, List<Vehicle> vehicles) {
        this.controller = controller;
        this.allVehicles = vehicles;

        setupDateSelector();
        setupNameField();

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

        // Setup Radio Button Group
        ButtonGroup bg = new ButtonGroup();
        bg.add(rbAircon); bg.add(rbStandard);

        // Listener for Destination changes
        cmbDest.addActionListener(e -> {
            SwingUtilities.invokeLater(this::updateBusTypeAvailability);
        });

        btnSearch.addActionListener(e -> search());

        // --- APPLY DEFAULT STATE ON STARTUP ---
        resetForm();
    }

    /**
     * Resets all fields to their default "Placeholder" state.
     */
    public void resetForm() {
        nameField.setText(NAME_PLACEHOLDER);
        nameField.setForeground(Color.GRAY);

        resetComboBox(cmbDate, DATE_PLACEHOLDER);
        resetComboBox(cmbDest, DEST_PLACEHOLDER);
        resetComboBox(cmbPassenger, TYPE_PLACEHOLDER);

        rbAircon.setEnabled(false);
        rbStandard.setEnabled(false);

        ButtonGroup bg = ((DefaultButtonModel)rbAircon.getModel()).getGroup();
        if(bg != null) bg.clearSelection();

        if (mainPanel != null) mainPanel.repaint();
    }

    private void resetComboBox(JComboBox<String> box, String placeholder) {
        boolean exists = false;
        for (int i = 0; i < box.getItemCount(); i++) {
            if (box.getItemAt(i).equals(placeholder)) {
                exists = true;
                break;
            }
        }
        if (!exists) {
            box.insertItemAt(placeholder, 0);
        }
        box.setSelectedItem(placeholder);
    }

    private void updateBusTypeAvailability() {
        Object selectedObj = cmbDest.getSelectedItem();

        if (selectedObj == null || selectedObj.toString().equals(DEST_PLACEHOLDER)) {
            rbAircon.setEnabled(false);
            rbStandard.setEnabled(false);
            ButtonGroup bg = ((DefaultButtonModel)rbAircon.getModel()).getGroup();
            if(bg != null) bg.clearSelection();
            return;
        }

        String selectedDest = selectedObj.toString();
        boolean hasAircon = false;
        boolean hasStandard = false;

        for (Vehicle v : allVehicles) {
            if (v instanceof Bus) {
                Bus b = (Bus) v;
                if (b.getDestination().trim().equalsIgnoreCase(selectedDest.trim())) {
                    String vType = b.getVehicleType().toLowerCase();
                    if (vType.contains("aircon")) hasAircon = true;
                    else if (vType.contains("standard")) hasStandard = true;
                }
            }
        }

        rbAircon.setEnabled(hasAircon);
        rbStandard.setEnabled(hasStandard);

        if (hasAircon && !hasStandard) rbAircon.setSelected(true);
        else if (!hasAircon && hasStandard) rbStandard.setSelected(true);
        else if (hasAircon && hasStandard) {
            if (!rbAircon.isSelected() && !rbStandard.isSelected()) rbAircon.setSelected(true);
        }
    }

    private void setupNameField() {
        nameField.setText(NAME_PLACEHOLDER);
        nameField.setForeground(Color.GRAY);

        nameField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (nameField.getText().equals(NAME_PLACEHOLDER)) {
                    nameField.setText("");
                    nameField.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (nameField.getText().trim().isEmpty()) {
                    nameField.setText(NAME_PLACEHOLDER);
                    nameField.setForeground(Color.GRAY);
                }
            }
        });
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
        String pName = nameField.getText();

        if (pName == null || pName.trim().isEmpty() || pName.equals(NAME_PLACEHOLDER)) {
            JOptionPane.showMessageDialog(mainPanel, "Please enter the Passenger Name.");
            return;
        }
        if (date == null || date.equals(DATE_PLACEHOLDER)) {
            JOptionPane.showMessageDialog(mainPanel, "Please select a Travel Date.");
            return;
        }
        if (dest == null || dest.equals(DEST_PLACEHOLDER)) {
            JOptionPane.showMessageDialog(mainPanel, "Please select a Destination.");
            return;
        }
        if (pType == null || pType.equals(TYPE_PLACEHOLDER)) {
            JOptionPane.showMessageDialog(mainPanel, "Please select a Passenger Type.");
            return;
        }

        if (!rbAircon.isSelected() && !rbStandard.isSelected()) {
            JOptionPane.showMessageDialog(mainPanel, "Please select a Bus Type (Aircon/Standard).");
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
                    "No " + busType + " buses available for " + dest + ".", "Route Unavailable", JOptionPane.WARNING_MESSAGE);
            return;
        }
        controller.goToSelection(dest, busType, pType, date, pName);
    }

    private void stylePlaceholders() {
        DefaultListCellRenderer renderer = new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (DEST_PLACEHOLDER.equals(value) ||
                        TYPE_PLACEHOLDER.equals(value) ||
                        DATE_PLACEHOLDER.equals(value)) {
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
        Set<String> dests = new HashSet<>();
        for (Vehicle v : vehicles) {
            if (v instanceof Bus) { dests.add(((Bus) v).getDestination()); }
        }
        for(String d : dests) cmbDest.addItem(d);

        resetForm();
    }

    public JPanel getMainPanel() { return mainPanel; }

    private void createUIComponents() {
        JTextField txtName = new JTextField();
        txtName.setPreferredSize(new Dimension(250, 25));
        txtName.setFont(new Font("Arial", Font.PLAIN, 14));

        btnSearch = new main.gui.components.RoundedButton("Search Schedules")
                .setNormalColor(new Color(244, 208, 63))
                .setHoverColor(new Color(255, 225, 100))
                .setPressedColor(new Color(200, 170, 50));

        bodyPanel = new main.gui.components.ImagePanel("/Cool_bg.png");
        bodyPanel.setLayout(new BorderLayout());
    }
}