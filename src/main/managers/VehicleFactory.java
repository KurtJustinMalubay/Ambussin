package main.managers;

import main.models.AirconBus;
import main.models.StandardBus;
import main.models.Vehicle;

public class VehicleFactory {
    private static int parseInt(String s){
        try{return Integer.parseInt(s.trim());} catch (NumberFormatException e){return 0;}
    }
    private static double parseDouble(String s){
        try{return Double.parseDouble(s.trim());} catch (NumberFormatException e){return 0.0;}
    }

    public static Vehicle createVehicle(String[] data){
        // Schema: ID, Type, Dest, Price, Capacity, Cols
        if(data.length < 5){return null;}
        String id =  data[0].trim();
        String type =  data[1].trim();
        String dest =  data[2].trim();
        double price =   parseDouble(data[3].trim());
        int capacity  = parseInt(data[4].trim());

        if(type.equalsIgnoreCase("Aircon")){
            return new AirconBus(id, "Terminal", dest, price, capacity);
        }else{
            return new StandardBus(id, "Terminal", dest, price, capacity);
        }
    }
}
