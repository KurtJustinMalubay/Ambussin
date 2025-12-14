package main.gui;

import main.gui.components.BusCardRenderer;
import main.gui.components.RoundedButton;
import main.managers.DataManager;
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
    private JButton backButton;
    private MainFrame controller;

    private List<Vehicle> allVehicles;
    private Bus selectedBus;
    private int sRow = -1, sCol = -1;
    private String passengerType;
    private String selectedDate;
    private String passengerName;

    private RoundedButton currentSelectedBtn = null;

    // Store the renderer so we can update the date
    private BusCardRenderer renderer;

    // Color Constants
    private final Color COLOR_OPEN = new Color(102, 204, 102);
    private final Color COLOR_TAKEN = new Color(211, 93, 93);
    private final Color COLOR_DRIVER = new Color(90, 200, 250);
    private final Color COLOR_SELECTED = new Color(74, 143, 74);
    private final Color COLOR_AISLE_WHITE = Color.WHITE;

    public SelectionPanel(MainFrame controller, List<Vehicle> vehicles) {
        this.controller = controller;
        this.allVehicles = vehicles;

        initStyling();

        backButton.addActionListener(e -> {
            controller.goToBooking();
        });

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
                        controller, selectedBus, sRow, sCol, passengerType, selectedDate, passengerName
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

        // --- FIX: Initialize renderer as a field ---
        renderer = new BusCardRenderer();
        busList.setCellRenderer(renderer);
        // -------------------------------------------

        busList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        busList.setBackground(new Color(0,0,0,0));
        busList.setOpaque(false);

        if(busList.getParent() instanceof JViewport) {
            JScrollPane sp = (JScrollPane) busList.getParent().getParent();
            sp.setBorder(null);
            sp.getViewport().setBackground(new Color(224, 224, 224));
        }

        legendPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));
        legendPanel.add(createLegendItem(COLOR_OPEN, "Open"));
        legendPanel.add(createLegendItem(COLOR_TAKEN, "Taken"));
        legendPanel.add(createLegendItem(COLOR_DRIVER, "Driver"));
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

    public void loadResults(String dest, String busType, String pType, String date, String name) {
        this.passengerType = pType;
        this.selectedDate = date;
        this.passengerName = name;

        // --- FIX: Pass the selected date to the renderer ---
        if (renderer != null) {
            renderer.setDateToCount(date);
        }
        // --------------------------------------------------

        List<Vehicle> filtered = allVehicles.stream()
                .filter(v -> v instanceof Bus &&
                        ((Bus) v).getDestination().equalsIgnoreCase(dest) &&
                        v.getVehicleType().contains(busType))
                .collect(Collectors.toList());

        DefaultListModel<Vehicle> model = new DefaultListModel<>();
        for (Vehicle v : filtered) model.addElement(v);
        busList.setModel(model);

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
        seatContainer.setLayout(new GridBagLayout());

        List<Point> bookedSeats = DataManager.getInstance().getBookedSeats(b.getVehicleID(), this.selectedDate);

        int lastRowIndex = b.getRows() - 1;
        int lastColIndex = b.getCols() - 1;
        int middleCol = b.getCols() / 2;

        for (int r = 0; r < b.getRows(); r++) {
            for (int c = 0; c < b.getCols(); c++) {

                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridx = c;
                gbc.gridy = r;
                gbc.weightx = 1.0;
                gbc.weighty = 1.0;
                gbc.fill = GridBagConstraints.BOTH;
                gbc.insets = new Insets(2, 2, 2, 2);

                boolean isBackRow = (r == 0);
                boolean isDriverRow = (r == lastRowIndex);
                boolean isAisle = (c == middleCol && !isBackRow && !isDriverRow);

                boolean isDriverStart = (isDriverRow && c == lastColIndex - 1);
                boolean isHiddenByDriver = (isDriverRow && c == lastColIndex);

                boolean isBooked = bookedSeats.contains(new Point(r, c));

                if (isHiddenByDriver) continue;

                JComponent componentToAdd;

                if (isDriverStart) {
                    RoundedButton btn = createSeatButton();
                    btn.setNormalColor(COLOR_DRIVER);
                    btn.setText("D");
                    btn.setEnabled(false);
                    gbc.gridwidth = 2;
                    componentToAdd = btn;
                }
                else if (isDriverRow) {
                    componentToAdd = createWhiteSpacer();
                }
                else if (isAisle) {
                    componentToAdd = createWhiteSpacer();
                }
                else {
                    RoundedButton btn = createSeatButton();

                    if (!b.isSeatAvailable(r, c) || isBooked) {
                        btn.setNormalColor(COLOR_TAKEN);
                        btn.setEnabled(false);
                    } else {
                        btn.setNormalColor(COLOR_OPEN);
                        btn.setHoverColor(COLOR_SELECTED);
                        int finalR = r;
                        int finalC = c;
                        btn.addActionListener(e -> selectSeat(btn, finalR, finalC));
                    }
                    componentToAdd = btn;
                }
                seatContainer.add(componentToAdd, gbc);
            }
        }
        seatContainer.revalidate();
        seatContainer.repaint();
    }

    private RoundedButton createSeatButton() {
        RoundedButton btn = new RoundedButton("");
        btn.setRadius(10);
        Dimension d = new Dimension(45, 45);
        btn.setPreferredSize(d);
        btn.setMinimumSize(d);
        btn.setMargin(new Insets(0, 0, 0, 0));
        return btn;
    }

    private JPanel createWhiteSpacer() {
        JPanel p = new JPanel();
        p.setBackground(COLOR_AISLE_WHITE);
        p.setOpaque(true);
        Dimension d = new Dimension(45, 45);
        p.setPreferredSize(d);
        p.setMinimumSize(d);
        return p;
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
        mainPanel = new JPanel();
        busList = new JList<>();
        listHeader = new JPanel();

        seatContainer = new main.gui.components.RoundedPanel(40, new Color(84, 120, 125));
        bodyPanel = new main.gui.components.ImagePanel("/Cool_bg.png");
        bodyPanel.setLayout(new BorderLayout());

        backButton = new main.gui.components.RoundedButton("Back")
                .setNormalColor(new Color(244, 208, 63))
                .setHoverColor(new Color(255, 225, 100))
                .setPressedColor(new Color(200, 170, 50));
        backButton.setForeground(Color.BLACK);

        btnConfirm = new main.gui.components.RoundedButton("Confirm")
                .setNormalColor(new Color(244, 208, 63))
                .setHoverColor(new Color(255, 225, 100))
                .setPressedColor(new Color(200, 170, 50));
    }
}