package main.models;

import main.exceptions.InvalidSeatException;

public abstract class Bus extends Vehicle {
    private String origin;
    private String destination;
    private double basePrice;
    private boolean[][] seats;
    private int capacity;

    public Bus(String vehicleId, String origin, String destination, double basePrice, int capacity, int cols) {
        super(vehicleId);
        this.origin = origin;
        this.destination = destination;
        this.basePrice = basePrice;
        this.capacity = capacity;

        int seatsInBackRow = cols;
        int seatsPerMiddleRow = cols - 1;
        int remainingCapacity = capacity - seatsInBackRow;
        int middleRows = (int) Math.ceil((double) remainingCapacity / seatsPerMiddleRow);

        int totalRows = 1 + middleRows + 1;

        this.seats = new boolean[totalRows][cols];

        for(int r = 0; r < totalRows; r++){
            for(int c = 0; c < cols; c++){
                if (r == 0) {
                    this.seats[r][c] = true; // Back Row
                } else if (r == totalRows - 1) {
                    this.seats[r][c] = false; // Driver Row
                } else {
                    int middleCol = cols / 2;
                    this.seats[r][c] = (c != middleCol); // Middle rows (skip aisle)
                }
            }
        }
    }

    public int getRows(){return seats.length;}
    public int getCols(){return seats[0].length;}
    public String getDestination(){return destination;}
    public double getBasePrice(){return basePrice;}
    public boolean isSeatAvailable(int r, int c){return seats[r][c];}

    public void bookSeat(int r, int c) throws InvalidSeatException {
        seats[r][c] = false;
    }
    public String getVehicleType(){return "Bus";}
}