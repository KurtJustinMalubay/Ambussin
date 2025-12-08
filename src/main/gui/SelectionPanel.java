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

    private RoundedButton currentSelectedBtn = null;
    private final Color COLOR_OPEN = new Color(102, 204, 102);
    private final Color COLOR_TAKEN = new Color(211, 93, 93);
    private final Color COLOR_SELECTED = new Color(74, 143, 74);
    private final Color COLOR_DRIVER = new Color(90, 200, 250);

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

        busWrapper.setLayout(new GridBagLayout());
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
            sp.setOpaque(false);
            sp.getViewport().setOpaque(false);
        }

        legendPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 15));
        legendPanel.add(createLegendItem(COLOR_OPEN, "Open"));
        legendPanel.add(createLegendItem(COLOR_TAKEN, "Taken"));
        legendPanel.add(createLegendItem(COLOR_DRIVER, "Driver"));
    }

    private JPanel createLegendItem(Color c, String text) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        p.setOpaque(false);
        JPanel box = new JPanel();
        box.setPreferredSize(new Dimension(20, 20));
        box.setBackground(c);
        box.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        p.add(box);
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Arial", Font.PLAIN, 14));
        p.add(lbl);
        return p;
    }

    public void loadResults(String dest, String busType, String pType, String date) {
        this.passengerType = pType;
        this.selectedDate = date;

        List<Vehicle> filtered = allVehicles.stream()
                .filter(v -> v instanceof Bus &&
                        ((Bus) v).getDestination().equalsIgnoreCase(dest) &&
                        v.getVehicleType().toLowerCase().contains(busType.toLowerCase()))
                .collect(Collectors.toList());

        DefaultListModel<Vehicle> model = new DefaultListModel<>();
        for (Vehicle v : filtered) model.addElement(v);
        busList.setModel(model);

        seatContainer.removeAll();
        seatContainer.repaint();
        btnConfirm.setEnabled(false);

        if (!model.isEmpty()) {
            busList.setSelectedIndex(0);
        }
    }

    private void loadSeats(Bus b) {
        this.selectedBus = b;
        this.sRow = -1;
        this.sCol = -1;
        this.currentSelectedBtn = null;
        btnConfirm.setEnabled(false);

        seatContainer.removeAll();
        seatContainer.setBorder(new EmptyBorder(20, 20, 20, 20));
        seatContainer.setLayout(new GridLayout(b.getRows(), b.getCols(), 15, 15));

        for (int r = 0; r < b.getRows(); r++) {
            for (int c = 0; c < b.getCols(); c++) {

                boolean isDriver = (r == 0 && c == 0);
                int middleCol = b.getCols() / 2;
                boolean isAisle = (c == middleCol && r != b.getRows() - 1);

                RoundedButton btn = new RoundedButton("");
                btn.setRadius(20);
                btn.setPreferredSize(new Dimension(75, 75));
                btn.setMargin(new Insets(0, 0, 0, 0));

                if (isDriver) {
                    btn.setNormalColor(COLOR_DRIVER);
                    btn.setEnabled(false);
                    btn.setText("D");
                    btn.setForeground(Color.WHITE);
                    btn.setFont(new Font("Arial", Font.BOLD, 16));
                }
                else if (isAisle) {
                    btn.setOpaque(false);
                    btn.setContentAreaFilled(false);
                    btn.setBorderPainted(false);
                    btn.setEnabled(false);
                    btn.setNormalColor(new Color(0,0,0,0));
                }
                else if (!b.isSeatAvailable(r, c)) {
                    btn.setNormalColor(COLOR_TAKEN);
                    btn.setEnabled(false);
                }
                else {
                    btn.setNormalColor(COLOR_OPEN);
                    btn.setHoverColor(new Color(74, 143, 74));

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
        btnConfirm.repaint();
    }

    public void update(List<Vehicle> newData) {this.allVehicles = newData;}

    public JPanel getMainPanel() { return mainPanel; }

    private void createUIComponents() {
        seatContainer = new main.gui.components.RoundedPanel(40, new Color(84, 120, 125));

        btnConfirm = new main.gui.components.RoundedButton("Confirm")
                .setNormalColor(new Color(244, 208, 63))
                .setHoverColor(new Color(255, 225, 100))
                .setPressedColor(new Color(200, 170, 50));

        btnConfirm.setPreferredSize(new Dimension(300, 50));
        btnConfirm.setFont(new Font("Arial", Font.BOLD, 18));

        bodyPanel = new main.gui.components.ImagePanel("/Cool_bg.png");
        bodyPanel.setLayout(new BorderLayout());
    }
}