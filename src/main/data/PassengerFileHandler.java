package main.data;

import main.models.Passenger;
import java.io.*;
import java.util.*;

public class PassengerFileHandler {
    private static final String FILE_PATH = "src/main/data/passengers.txt" ;

    public static void savePassenger(Passenger passenger) {
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, true))){
            bw.write(passenger.getName() + "," + passenger.getAge() + "," + passenger.getPassengerType());
            bw.newLine();
        }catch(IOException e){
            System.out.println("Error writing passenger:  " + e.getMessage());
        }
    }
    public static List<Passenger> loadPassengers() {
        List<Passenger> passengers = new ArrayList<>();


        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                if (data.length == 3) {
                    String name = data[0];
                    int age = Integer.parseInt(data[1]);
                    String type = data[2];

                    Passenger p = new Passenger(name, age, type);
                    passengers.add(p);
                }
            }

        } catch (IOException e) {
            System.out.println("Error reading passengers: " + e.getMessage());
        }

        return passengers;
    }
}
