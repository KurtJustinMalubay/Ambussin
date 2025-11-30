package main.models;

public class StandardBus extends Bus{
    public StandardBus(String vehicleId, String origin, String destination){
        super(vehicleId, origin, destination, 150, 40);
    }
    public String toString(){return "Standard Bus";}
}
