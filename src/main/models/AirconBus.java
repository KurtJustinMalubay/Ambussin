package main.models;

public class AirconBus extends Bus{
    public AirconBus(String vehicleId, String origin, String destination, double basePrice, int capacity){
        super(vehicleId, origin, destination, basePrice, capacity, 4);
    }
    public String getVehicleType(){return super.getVehicleType() + " - Aircon";}
}
