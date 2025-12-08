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

        int sFront = cols;
        int sBack = cols - 1;

        int remainingCap = capacity - sFront;
        int backRows = (int) Math.ceil((double) remainingCap / sBack);
        int rows = backRows + 1;

        this.seats = new boolean[rows][cols];
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < cols; j++){
                this.seats[i][j] = true;
            }
        }

        int middleSeats = cols / 2;
        for(int i = 1; i < rows; i++){
            this.seats[i][middleSeats] = false;
        }
    }

    public int getRows(){return seats.length;}
    public int getCols(){return seats[0].length;}
    public String getDestination(){return destination;}
    public double getBasePrice(){return basePrice;}
    public boolean isSeatAvailable(int r, int c){return seats[r][c];}
    public void bookSeat(int r, int c) throws InvalidSeatException {
        if(r < 0 || r >= getRows() || c < 0 || c >= getCols()){
            throw new InvalidSeatException("Error: Seat out of bounds.");
        }
        if(!seats[r][c]){
            throw new InvalidSeatException("Seat " + (r+1) + "-" + (c+1) + " is already occupied.");
        }
        seats[r][c] = false;
    }
    public String getVehicleType(){return "Bus";}

}