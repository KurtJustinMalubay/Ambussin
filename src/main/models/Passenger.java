package main.models;

public class Passenger {
    private String name;
    private int age;
    private String passengerType;

    public Passenger(String name, int age, String passengerType) {
        this.name = name;
        this.age = age;
        this.passengerType = passengerType;
    }
    public String getName() {
        return name;
    }
    public int getAge() {
        return age;
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
                "Age: " + age + "\n" +
                "Passenger Type: " + passengerType + "\n" +
                "Discount Rate: " + (getDiscountRate() * 100) + "%";
    }
}
