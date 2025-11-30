package main.models;

public class DiscountedPassenger extends Passenger {
    public DiscountedPassenger(String name, int age) {super(name,age);}
    public double computeFare(double basePrice) {
        return basePrice * 0.80;
    }
}
