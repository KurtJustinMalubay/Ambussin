package main.models;

public abstract class Vehicle {
    private String vehicleID;
    private String type;

    public Vehicle(String vehicleID, String type) {
        this.vehicleID = vehicleID;
        this.type = type;
    }
    public String getVehicleID() {return vehicleID;}
    public String getType() {return type;}
}
