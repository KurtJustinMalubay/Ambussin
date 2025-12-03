package main;

import javax.swing.*;
import javax.swing.SwingUtilities;
import com.ambussin.BusBookingApp;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(BusBookingApp::new);
    }
}
