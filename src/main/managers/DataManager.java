package main.managers;

import main.models.Vehicle;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DataManager {
    private static DataManager instance;
    private static final String BUS_FILE = "buses.csv";
    private static final String TRANS_FILE = "transactions.csv";

    private DataManager(){}

    public static DataManager getInstance(){
        if(instance == null){
            instance = new DataManager();
        }
        return instance;
    }

    public void addBus(String id, String type, String dest, String price, String cap){
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(BUS_FILE, true))){
            String cols = type.equalsIgnoreCase("Aircon") ? "4" : "5";
            String line = id + "," + type + "," + dest + "," + price + "," + cap + "," + cols;
            bw.newLine();
            bw.write(line);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public List<Vehicle> loadVehicles() throws FileNotFoundException {
        List<Vehicle> list = new ArrayList<>();
        File file = new File(BUS_FILE);

        if(!file.exists()){throw new FileNotFoundException("File not found");}
        try (BufferedReader br = new BufferedReader(new FileReader(file))){
            String line;
            boolean isHeader = true;
            while((line = br.readLine()) != null){
                if(isHeader){isHeader = false; continue;}
                String[] fields = line.split(",");
                Vehicle v = VehicleFactory.createVehicle(fields);
                list.add(v);
            }
        }catch (IOException ioe){
            ioe.printStackTrace();
        }
        return list;
    }

    public void saveTransaction(String vehicleId, String seat, String name, String type, double price){
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(TRANS_FILE, true))){
            // Schema: ID, Seat, Name, Type, Price
            String record = vehicleId + "," + seat + "," + name + "," + type + "," + price;
            bw.write(record);
            bw.newLine();
        }catch (IOException ioe){
            ioe.printStackTrace();
        }
    }

    public String[][] loadTransactions() throws FileNotFoundException {
        List<String[]> list = new ArrayList<>();
        File file = new File(TRANS_FILE);
        if(!file.exists()){throw new FileNotFoundException("File not found");}

        try(BufferedReader br = new BufferedReader(new FileReader(file))){
            String line;
            while((line = br.readLine()) != null) list.add(line.split(","));
        }catch (IOException ioe){
            ioe.printStackTrace();
        }

        String[][] data = new String[list.size()][];
        for(int i = 0; i < list.size(); i++) data[i] = list.get(i);
        return data;
    }
}
