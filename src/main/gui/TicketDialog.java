package main.gui;

import main.models.Bus;
import javax.swing.*;
import java.awt.*;

public class TicketDialog extends JDialog {
    public TicketDialog(JFrame parent, MainFrame controller, Bus v, int r, int c, String pType, String date, String name) {
        super(parent, true);
        setUndecorated(true);
        TicketPanel content = new TicketPanel(this, controller, v, r, c, pType, date, name);
        setContentPane(content.getMainPanel());
        pack();
        setLocationRelativeTo(parent);
    }
}