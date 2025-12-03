package main;

import javax.swing.*;
import javax.swing.SwingUtilities;
import com.ambussin.BusBookingApp;  // Make sure to import the GUI class

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(BusBookingApp::new);
    }
}
