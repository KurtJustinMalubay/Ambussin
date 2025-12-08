package main.gui;

import main.managers.DataManager;
import main.models.Bus;
import main.models.Vehicle;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AdminPanel {
    // Header/Body
    private JPanel mainPanel;
    private JPanel bodyPanel;
    private JButton btnLogout;
    // MainContent - LeftCard
    private JButton btnSaveAdd;
    private JButton btnShowLogs;
    private JPanel navPanel;
    private JTextField textId;
    private JComboBox<String> cmbType;
    private JComboBox<String> cmbDest;
    private JTextField textPrice;
    // MainContent - RightCard
    private JPanel logsPanel;
    private JTable table;

    private MainFrame controller;
    private DefaultTableModel model;
    private List<Vehicle> existingVehicles;

    private static final String TYPE_PLACEHOLDER = "Type...";
    private static final String DEST_PLACEHOLDER = "Destination...";

    public AdminPanel(MainFrame controller, List<Vehicle> vehicles) {
        this.controller = controller;
        this.existingVehicles = vehicles;
        inputs();
        table();
        refreshLogs();
        btnSaveAdd.addActionListener(e -> saveBus());
        btnShowLogs.addActionListener(e -> refreshLogs());
        btnLogout.addActionListener(e -> {
           textId.setText(""); textPrice.setText("");
           controller.goToLanding();
        });
    }

    ///  Para tabang na methodolohiya: autoRemovePlaceHolders, InputsCreation, TableCreation
    private void autoRemovePlaceHolder(JComboBox<String> box, String placeholder) {
        box.addItem(placeholder);
        box.addActionListener(e -> {
           Object selected = box.getSelectedItem();
           if(selected != null && !selected.equals(placeholder)) {
               box.removeItem(placeholder);
           }
        });
    }
    private void inputs(){
        autoRemovePlaceHolder(cmbType, TYPE_PLACEHOLDER);
        cmbType.addItem("AIRCON");
        cmbType.addItem("STANDARD");

        Set<String> places = new HashSet<>();
        if(existingVehicles != null){
            for(Vehicle v: existingVehicles){
                if(v instanceof Bus) places.add(((Bus) v).getDestination());
            }
        }
        autoRemovePlaceHolder(cmbDest, DEST_PLACEHOLDER);
        for(String p: places) cmbDest.addItem(p);
        cmbDest.setEditable(true);
    }
    private void table(){
        String[] header = {"BusID", "Seat", "Name", "Type", "Amount", "DatetoRide"};
        model = new DefaultTableModel(header, 0){
            @Override
            public boolean isCellEditable(int row, int column) {return false;}
        };
        table.setModel(model);
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
    }

    public void refreshLogs(){
        model.setRowCount(0);
        try{
            String[][] data = DataManager.getInstance().loadTransactions();
            for(String[] row: data){ model.addRow(row); }
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }

    public void saveBus(){
        // TODO: Bus Error Handilng || Unchecked Errors
        if(textId.getText().isEmpty() || textPrice.getText().isEmpty()){
            JOptionPane.showMessageDialog(mainPanel, "Please fill all the fields.");
            return;
        }

        String id = textId.getText().trim().toUpperCase();
        String type = ((String) cmbType.getSelectedItem()).toUpperCase();
        String dest = (String) cmbDest.getSelectedItem();

        if(dest == null || dest.trim().isEmpty() || dest.equals(DEST_PLACEHOLDER)){
            JOptionPane.showMessageDialog(mainPanel, "Please enter a destination.");
            return;
        }
        if(type == null || type.trim().isEmpty() ||  type.equals(TYPE_PLACEHOLDER)){
            JOptionPane.showMessageDialog(mainPanel, "Please enter a valid type.");
            return;
        }

        boolean isDuplicated = existingVehicles.stream().anyMatch(v -> v.getVehicleID().equalsIgnoreCase(id));
        if(isDuplicated){
            JOptionPane.showMessageDialog(mainPanel, "BusID [" + id + "] already exists." +
                    "\nPlease choose another busID.", "Duplicated BusID",  JOptionPane.WARNING_MESSAGE);
            return;
        }

        if(id.startsWith("AC") && !type.equals("AIRCON")){
            JOptionPane.showMessageDialog(mainPanel, "Please enter a valid type.", "Type Mismatch", JOptionPane.WARNING_MESSAGE);
            return;
        }else if(id.startsWith("ORD") && !type.equals("STANDARD")){
            JOptionPane.showMessageDialog(mainPanel, "Please enter a valid type.", "Type Mismatch", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try{
            Double.parseDouble(textPrice.getText());
            DataManager.getInstance().addBus(
                    textId.getText(),
                    (String) cmbType.getSelectedItem(),
                    dest,
                    textPrice.getText()
            );

            boolean exist = false;
            for(int i = 0; i < cmbDest.getItemCount(); i++){
                if(cmbDest.getItemAt(i).equalsIgnoreCase(dest)){
                    exist = true; break;
                }
            }
            if(!exist){ cmbDest.addItem(dest); }
            JOptionPane.showMessageDialog(mainPanel, "Bus saved successfully.");

            controller.refreshApp();
            textId.setText("");
            textPrice.setText("");

        }catch (NumberFormatException e){
            JOptionPane.showMessageDialog(mainPanel, "Please enter a valid price.", "Input Mismatch", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        btnLogout = new main.gui.components.RoundedButton("Logout")
                .setNormalColor(new Color(0, 0, 0))
                .setHoverColor(new Color(255, 255, 255));
        btnSaveAdd = new main.gui.components.RoundedButton("Show Add")
                .setNormalColor(Color.WHITE)
                .setHoverColor(Color.LIGHT_GRAY)
                .setPressedColor(Color.DARK_GRAY)
                .setBorderColor(Color.BLACK);
        btnShowLogs = new main.gui.components.RoundedButton("Show Logs")
                .setNormalColor(Color.WHITE)
                .setHoverColor(Color.LIGHT_GRAY)
                .setPressedColor(Color.DARK_GRAY)
                .setBorderColor(Color.BLACK);
        bodyPanel = new main.gui.components.ImagePanel("/Cool_bg.png");
        bodyPanel.setLayout(new BorderLayout());
    }

    public JPanel getMainPanel() { return mainPanel; }
}