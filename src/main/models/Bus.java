package main.models;

public abstract class Bus extends Vehicle {
    private String origin;
    private String destination;
    private double basePrice;
    private boolean[] seats;

    public Bus(String vehicleId, String origin, String destination, double basePrice, int capacity) {
        super(vehicleId, "Bus");
        this.origin = origin;
        this.destination = destination;
        this.basePrice = basePrice;
        this.seats = new boolean[capacity];
    }

    public boolean isSeatOccupied(int idx){return seats[idx];}
    public String getRoute(){return origin + " -> " + destination;}
    public double getBasePrice(){return basePrice;}
    public int getCapacity(){return seats.length;}
}
