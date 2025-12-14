package main.managers;

import main.models.Bus;
import main.models.Vehicle;

import java.awt.*;
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
            String safeName = name.replace(",", " ");

            String record = vehicleId + "," + seat + "," + safeName + "," + type + "," + price + "," + date;
            bw.write(record);
            bw.newLine();
        }catch (IOException ioe){
            ioe.printStackTrace();
        }
    }

    // --- Used for Admin Panel ---
    public String[][] loadTransactions() throws FileNotFoundException {
        List<String[]> list = new ArrayList<>();
        File file = new File(TRANS_FILE);
        if(!file.exists()){return new String[0][0];}

        try(BufferedReader br = new BufferedReader(new FileReader(file))){
            String line;
            while((line = br.readLine()) != null) {
                String[] fields = line.split(",");

                if (fields.length > 6) {
                    String datePart1 = fields[5];
                    String datePart2 = fields[6];
                    String fullDate = datePart1 + "," + datePart2;

                    String[] normalized = new String[6];
                    System.arraycopy(fields, 0, normalized, 0, 5);
                    normalized[5] = fullDate;
                    list.add(normalized);
                } else {
                    list.add(fields);
                }
            }
        }catch (IOException ioe){ioe.printStackTrace();}

        String[][] data = new String[list.size()][];
        for(int i = 0; i < list.size(); i++) data[i] = list.get(i);
        return data;
    }

    public List<Point> getBookedSeats(String targetId, String targetDate) {
        List<Point> occupied = new ArrayList<>();
        File file = new File(TRANS_FILE);
        if (!file.exists()) return occupied;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length < 6) continue;

                String id = fields[0].trim();
                String seat = fields[1].trim();

                String storedDate = fields[5];
                if (fields.length > 6) {
                    storedDate += "," + fields[6];
                }

                String cleanStored = storedDate.replaceAll("\\s+", "");
                String cleanTarget = targetDate.replaceAll("\\s+", "");

                // 3. Compare
                if (id.equalsIgnoreCase(targetId.trim()) && cleanStored.equalsIgnoreCase(cleanTarget)) {
                    try {
                        String[] coords = seat.split("-");
                        int r = Integer.parseInt(coords[0]) - 1;
                        int c = Integer.parseInt(coords[1]) - 1;
                        occupied.add(new Point(r, c));
                    } catch (Exception e) {
                        System.err.println("Skipping invalid seat: " + seat);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return occupied;
    }

    public void loadBookings(List<Vehicle> bus){
        // Intentionally empty.
    }
}