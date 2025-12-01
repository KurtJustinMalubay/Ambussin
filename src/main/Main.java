import javax.swing.SwingUtilities;
import com.ambussin.BusBookingApp; // <-- Make sure to import the GUI class

public class Main {

    public static void main(String[] args) {
        // This is the code that launches the GUI window.
        SwingUtilities.invokeLater(BusBookingApp::new);
    }
}