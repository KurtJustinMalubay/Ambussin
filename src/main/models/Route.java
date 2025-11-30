package main.models;

public class Route {
    private String routeID;
    private String origin;
    private String destination;
    private double baseFare;
    private Vehicle vehicle;

    public Route(String routeID, String origin, String destination, double baseFare, Vehicle vehicle) {
        this.routeID = routeID;
        this.origin = origin;
        this.destination = destination;
        this.baseFare = baseFare;
        this.vehicle = vehicle;
    }
    public String getRouteInfo() {
        return String.format("[%s] %s -> %s (Fare: %.2f)", routeID, origin, destination, baseFare);
    }
    public double getBaseFare() {
        return baseFare;
    }
    public double getPrice() {
        return baseFare;
    }
    public String getOrigin() {
        return origin;
    }
    public String getDestination() {
        return destination;
    }
    public String getVehicle() {
        if (vehicle != null) {
            return vehicle.toString();
        }
        return "No Vehicle Assigned";
    }
    public Vehicle getVehicleObject() {
        return vehicle;
    }
}