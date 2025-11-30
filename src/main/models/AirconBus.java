package main.models;

public class AirconBus extends Bus{
    public AirconBus(String vehicleId, String origin, String destination){
        super(vehicleId, origin, destination, 200, 30);
    }
    public String toString(){return "Aircon Bus";}
}
