package main.models;

public class Passenger {
    private String name;
    private int age;
    private String passengerType; // Expected values: "REGULAR", "STUDENT", "PWD"

    public Passenger(String name, int age, String passengerType) {
        this.name = name;
        this.age = age;
        // Ensure the type is stored in uppercase to match Seat.java switch cases
        this.passengerType = passengerType.toUpperCase();
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getPassengerType() {
        return passengerType;
    }

    // Setter in case details need to be updated
    public void setPassengerType(String passengerType) {
        this.passengerType = passengerType.toUpperCase();
    }

    //SA MAIN NI
    //    public void displayInfo() {
    //        System.out.println("Passenger Details:");
    //        System.out.println("Name: " + name);
    //        System.out.println("Age:  " + age);
    //        System.out.println("Type: " + passengerType);
    //    }

    @Override
    public String toString() {
        return name + " (" + passengerType + ")";
    }
}