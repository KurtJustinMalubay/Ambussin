package main.managers;

import main.models.Bus;
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
        if(instance == null) instance = new DataManager();
        return instance;
    }

    public void addBus(String id, String type, String dest, String price){
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(BUS_FILE, true))){
            String capacity = type.equalsIgnoreCase("AIRCON") ? "41" : "49";
            // Schema: BusId, BusType, Destination, Price, Capacity
            String line = id + "," + type + "," + dest + "," + price + "," + capacity;
            bw.newLine();
            bw.write(line);
        }catch(IOException e){e.printStackTrace();}
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
        }catch (IOException ioe){ioe.printStackTrace();}
        return list;
    }

    public void saveTransaction(String vehicleId, String seat, String name, String type, double price, String date){
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(TRANS_FILE, true))){
            // Schema: ID, Seat, Name, Type, Price, Date
            String record = vehicleId + "," + seat + "," + name + "," + type + "," + price + "," + date;
            bw.write(record);
            bw.newLine();
        }catch (IOException ioe){
            ioe.printStackTrace();
        }
    }

    public String[][] loadTransactions() throws FileNotFoundException {
        List<String[]> list = new ArrayList<>();
        File file = new File(TRANS_FILE);
        if(!file.exists()){return new String[0][0];}
        try(BufferedReader br = new BufferedReader(new FileReader(file))){
            String line;
            while((line = br.readLine()) != null) list.add(line.split(","));
        }catch (IOException ioe){ioe.printStackTrace();}

        String[][] data = new String[list.size()][];
        for(int i = 0; i < list.size(); i++) data[i] = list.get(i);
        return data;
    }

    public void loadBookings(List<Vehicle> bus){
        File file = new File(TRANS_FILE);
        if(!file.exists()){return;}
        try (BufferedReader br = new BufferedReader(new FileReader(file))){
            String line;
            while((line = br.readLine()) != null){
                String[] fields = line.split(",");
                if(fields.length < 2) continue;
                String id = fields[0];
                String seat = fields[1];

                for(Vehicle v : bus){
                    if(v.getVehicleID().equals(id)){
                        try{
                            String[] seatCoords = seat.split("-");
                            int r =  Integer.parseInt(seatCoords[0]) - 1;
                            int c = Integer.parseInt(seatCoords[1]) - 1;
                            if(v instanceof Bus){
                                try{
                                    ((Bus)v).bookSeat(r,c);
                                }catch(Exception e){}
                            }
                        }catch(NumberFormatException e){
                            System.err.println("Invalid seat format: " + seat);
                        }
                    }
                }
            }
        }catch(IOException ioe){ioe.printStackTrace();}
    }
}
