package main;

import main.models.Passenger;
import main.data.PassengerFileHandler;
import javax.swing.*;
import java.util.List;

public class Main {

//    public Main(){
//
//    }
    public static void main(String[] args){
        //SwingUtilities.invokeLater(Main::new);
        Passenger p1 = new Passenger("Alice", 25, "Student");
        Passenger p2 = new Passenger("Bob", 60, "Senior");

        PassengerFileHandler.savePassenger(p1);
        PassengerFileHandler.savePassenger(p2);

        List<Passenger> passengers = PassengerFileHandler.loadPassengers();

        for(Passenger p : passengers){
            System.out.println(p);
            System.out.println("----------");
        }
    }
}


//test