package main.models;

public abstract class Vehicle {
    private String vehicleID;

    public Vehicle(String vehicleID) {
        this.vehicleID = vehicleID;
    }
    public String getVehicleID() {return vehicleID;}
    public abstract String getVehicleType();
}
