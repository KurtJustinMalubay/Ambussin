package main.models;

public abstract class Passenger extends Person {
    public Passenger(String name, int age) {super(name,age);}
    public abstract double computeFare(double basePrice);
}
