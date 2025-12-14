package main.gui.components;

import main.managers.DataManager;
import main.models.Bus;
import main.models.Vehicle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BusCardRenderer extends JPanel implements ListCellRenderer<Vehicle> {
    private JLabel lblId = new JLabel();
    private JLabel lblSeats = new JLabel();
    private JLabel lblTime = new JLabel();
    private JPanel cardContent = new JPanel(new BorderLayout());
    private static final List<String> SCHEDULE = genTimeSlots();

    private String selectedDateForCounting = "";

    private static List<String> genTimeSlots(){
        List<String> times = new ArrayList<>();
        for(int hour = 6; hour <= 22; hour++){
            String tPeriod = (hour >= 12) ? "PM" : "AM";
            int displayHour = (hour > 12) ? hour - 12 : hour;
            times.add(String.format("%02d:00 %s", displayHour, tPeriod));
            if(hour != 22) times.add(String.format("%02d:30 %s", displayHour, tPeriod));
        }
        return times;
    }

    public BusCardRenderer() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 5, 10, 5));
        setOpaque(false);

        cardContent.setBackground(Color.WHITE);
        cardContent.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        lblId.setFont(new Font("Arial", Font.BOLD, 16));
        lblId.setPreferredSize(new Dimension(80, 0));
        cardContent.add(lblId, BorderLayout.WEST);

        JPanel rightSide = new JPanel(new GridLayout(1, 2, 10, 0));
        rightSide.setOpaque(false);

        lblSeats.setHorizontalAlignment(SwingConstants.CENTER);
        lblTime.setHorizontalAlignment(SwingConstants.TRAILING);

        rightSide.add(lblSeats);
        rightSide.add(lblTime);
        cardContent.add(rightSide, BorderLayout.CENTER);

        JPanel paddingWrapper = new JPanel(new BorderLayout());
        paddingWrapper.setOpaque(false);
        paddingWrapper.setBorder(new EmptyBorder(10, 15, 10, 15));
        paddingWrapper.add(cardContent);

        add(cardContent, BorderLayout.CENTER);
    }

    public void setDateToCount(String date) {
        this.selectedDateForCounting = date;
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Vehicle> list, Vehicle value, int index, boolean isSelected, boolean cellHasFocus) {
        Bus b = (Bus) value;
        lblId.setText(b.getVehicleID());

        List<Point> bookedSeats = new ArrayList<>();
        if (selectedDateForCounting != null && !selectedDateForCounting.isEmpty()) {
            bookedSeats = DataManager.getInstance().getBookedSeats(b.getVehicleID(), selectedDateForCounting);
        }

        int capacity = 0;
        int available = 0;

        for (int r = 0; r < b.getRows(); r++) {
            for (int c = 0; c < b.getCols(); c++) {
                if (b.isSeatAvailable(r, c)) {
                    capacity++;

                    if (!bookedSeats.contains(new Point(r, c))) {
                        available++;
                    }
                }
            }
        }

        lblSeats.setText(available + "/" + capacity);
        lblTime.setText(SCHEDULE.get(index % SCHEDULE.size()));

        if (isSelected) {
            cardContent.setBackground(new Color(230, 245, 255));
            cardContent.setBorder(BorderFactory.createLineBorder(new Color(0, 120, 215), 2));
        } else {
            cardContent.setBackground(Color.WHITE);
            cardContent.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        }
        return this;
    }
}