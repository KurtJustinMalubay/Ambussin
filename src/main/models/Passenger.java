package main.models;

public class Passenger {
    private String name;
    private String passengerType;

    public Passenger(String name, String passengerType) {
        this.name = name;
        this.passengerType = passengerType;
    }
    public String getName() {
        return name;
    }
    public String getPassengerType() {
        return passengerType;
    }

    public double getDiscountRate(){
        String type = passengerType.toLowerCase();
        if(type.equals("pwd") || type.equals("senior") || type.equals("student")){
            return 0.15;
        }
        return 0.0;

    }
    public String toString() {
        return "Name: " + name + "\n" +
                "Passenger Type: " + passengerType + "\n" +
                "Discount Rate: " + (getDiscountRate() * 100) + "%";
    }
}
