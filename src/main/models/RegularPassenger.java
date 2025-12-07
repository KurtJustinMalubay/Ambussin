package main.models;

public class RegularPassenger extends Passenger {
    public RegularPassenger(String name, int age) {super(name,age);}
    public double computeFare(double basePrice) { return basePrice;}
}
