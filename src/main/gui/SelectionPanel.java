package main.gui;

import main.gui.components.BusCardRenderer;
import main.gui.components.RoundedButton;
import main.models.Bus;
import main.models.Vehicle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class SelectionPanel {
    private JPanel mainPanel;
    private JPanel bodyPanel;
    private JPanel listHeader;
    private JList<Vehicle> busList;
    private JPanel seatContainer;
    private JPanel legendPanel;
    private JButton btnConfirm;
    private JPanel busWrapper;
    private MainFrame controller;

    private List<Vehicle> allVehicles;

    private Bus selectedBus;
    private int sRow = -1, sCol = -1;
    private String passengerType;
    private String selectedDate;

    // Add these to your class fields
    private RoundedButton currentSelectedBtn = null; // Track the specific button instance
    private final Color COLOR_OPEN = new Color(102, 204, 102);
    private final Color COLOR_TAKEN = new Color(211, 93, 93);
    private final Color COLOR_DRIVER = new Color(90, 200, 250);
    private final Color COLOR_SELECTED = new Color(74, 143, 74); // Darker Green
    private final Color COLOR_AISLE = new Color(224, 224, 224); // Or transparent

    public SelectionPanel(MainFrame controller, List<Vehicle> vehicles) {
        this.controller = controller;
        this.allVehicles = vehicles;

        initStyling();

        busList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && busList.getSelectedIndex() != -1) {
                Bus b = (Bus) busList.getModel().getElementAt(busList.getSelectedIndex());
                loadSeats(b);
            }
        });

        btnConfirm.addActionListener(e -> {
            if (selectedBus != null && sRow != -1) {
                TicketDialog dialog = new TicketDialog(
                        (JFrame) SwingUtilities.getWindowAncestor(mainPanel),
                        controller, selectedBus, sRow, sCol, passengerType, selectedDate
                );
                dialog.setVisible(true);
            }
        });
    }

    private void initStyling() {
        bodyPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        busWrapper.setLayout(new GridLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;

        busWrapper.remove(seatContainer);
        busWrapper.add(seatContainer, gbc);

        busList.setCellRenderer(new BusCardRenderer());
        busList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        busList.setBackground(new Color(0,0,0,0));
        busList.setOpaque(false);

        if(busList.getParent() instanceof JViewport) {
            JScrollPane sp = (JScrollPane) busList.getParent().getParent();
            sp.setBorder(null);
            sp.getViewport().setBackground(new Color(224, 224, 224));
        }

        legendPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));
        legendPanel.add(createLegendItem(new Color(102, 204, 102), "Open"));
        legendPanel.add(createLegendItem(new Color(211, 93, 93), "Taken"));
        legendPanel.add(createLegendItem(new Color(90, 200, 250), "Driver"));
    }

    private JPanel createLegendItem(Color c, String text) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        p.setOpaque(false);
        JPanel box = new JPanel();
        box.setPreferredSize(new Dimension(15, 15));
        box.setBackground(c);
        box.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        p.add(box);
        p.add(new JLabel(text));
        return p;
    }

    public void loadResults(String dest, String busType, String pType, String date) {
        this.passengerType = pType;
        this.selectedDate = date;

        List<Vehicle> filtered = allVehicles.stream()
                .filter(v -> v instanceof Bus &&
                        ((Bus) v).getDestination().equalsIgnoreCase(dest) &&
                        v.getVehicleType().contains(busType))
                .collect(Collectors.toList());

        DefaultListModel<Vehicle> model = new DefaultListModel<>();
        for (Vehicle v : filtered) model.addElement(v);
        busList.setModel(model);

        // Reset
        seatContainer.removeAll();
        seatContainer.repaint();
        btnConfirm.setEnabled(false);
    }

    private void loadSeats(Bus b) {
        this.selectedBus = b;
        this.sRow = -1;
        this.sCol = -1;
        this.currentSelectedBtn = null;
        btnConfirm.setEnabled(false);

        seatContainer.removeAll();
        // Use the bus dimensions
        seatContainer.setLayout(new GridLayout(b.getRows(), b.getCols(), 5, 5));

        for (int r = 0; r < b.getRows(); r++) {
            for (int c = 0; c < b.getCols(); c++) {

                boolean isDriver = (r == 0 && c == 0);
                int middleCol = b.getCols() / 2;
                boolean isAisle = (c == middleCol && r != b.getRows() - 1);

                RoundedButton btn = new RoundedButton("");
                btn.setRadius(10);
                btn.setPreferredSize(new Dimension(45, 45));
                btn.setMargin(new Insets(0, 0, 0, 0));

                if (isDriver) {
                    btn.setNormalColor(new Color(90, 200, 250)); // Driver Blue
                    btn.setEnabled(false);
                }
                else if (isAisle) {
                    btn.setOpaque(false);
                    btn.setContentAreaFilled(false);
                    btn.setBorderPainted(false);
                    btn.setEnabled(false);
                }
                else if (!b.isSeatAvailable(r, c)) {
                    btn.setNormalColor(new Color(211, 93, 93)); // Taken Red
                    btn.setEnabled(false);
                }
                else {
                    btn.setNormalColor(new Color(102, 204, 102)); // Open Green
                    btn.setHoverColor(new Color(74, 143, 74));    // Darker Green on Hover

                    int finalR = r;
                    int finalC = c;
                    btn.addActionListener(e -> selectSeat(btn, finalR, finalC));
                }
                seatContainer.add(btn);
            }
        }
        seatContainer.revalidate();
        seatContainer.repaint();
    }

    private void selectSeat(RoundedButton btn, int r, int c) {
        if (currentSelectedBtn != null) {
            currentSelectedBtn.setNormalColor(COLOR_OPEN);
            currentSelectedBtn.repaint();
        }

        btn.setNormalColor(COLOR_SELECTED);

        currentSelectedBtn = btn;
        sRow = r;
        sCol = c;

        btnConfirm.setEnabled(true);
    }

    public void update(List<Vehicle> newData) {this.allVehicles = newData;}

    public JPanel getMainPanel() { return mainPanel; }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        seatContainer = new main.gui.components.RoundedPanel(40, new Color(84, 120, 125));

        btnConfirm = new main.gui.components.RoundedButton("Confirm")
                .setNormalColor(new Color(244, 208, 63))
                .setHoverColor(new Color(255, 225, 100))
                .setPressedColor(new Color(200, 170, 50));
        bodyPanel = new main.gui.components.ImagePanel("/Cool_bg.png");
        bodyPanel.setLayout(new BorderLayout());
    }
}