package com.model;

public class Passenger {

    private String firstName;
    private String lastName;
    private int secondsInQueue;
    private int seatNumber;

    public int getSeatNumber() { return seatNumber; }

    public void setSeatNumber(int seatNumber) { this.seatNumber = seatNumber; }

    public int getSecondsInQueue() { return secondsInQueue; }

    public String getFirstName() { return firstName; }

    public String getLastName() { return lastName; }

    public void setSecondsInQueue(int secondsInQueue) { this.secondsInQueue = secondsInQueue; }

    public void setFirstName(String firstName) { this.firstName = firstName; }

    public void setLastName(String lastName) { this.lastName = lastName; }

}
