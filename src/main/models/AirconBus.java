package main.models;

public class AirconBus extends Bus{
    private static final int FIXED_CAPACITY = 37;
    private static final int FIXED_COLS = 5;
    public AirconBus(String vehicleId, String origin, String destination, double basePrice){
        super(vehicleId, origin, destination, basePrice, FIXED_CAPACITY, FIXED_COLS);
    }
    public String getVehicleType(){return super.getVehicleType() + " - Aircon";}
}
