package main.managers;

import main.models.DiscountedPassenger;
import main.models.Passenger;
import main.models.RegularPassenger;

public class PassengerFactory {
    public static Passenger createPassenger(String type, String name, int age){
        type = type.toLowerCase();
        return switch(type){
            case "senior", "student", "pwd", "pregnant" -> new DiscountedPassenger(name,age);
            default -> new RegularPassenger(name,age);
        };
    }
}
