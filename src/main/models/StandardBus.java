package main.models;

public class StandardBus extends Bus{
    public StandardBus(String vehicleId, String origin, String destination, double basePrice, int capacity){
        super(vehicleId, origin, destination, basePrice, capacity, 5);
    }

    public String getVehicleType(){return super.getVehicleType() + " - Standard";}
}
