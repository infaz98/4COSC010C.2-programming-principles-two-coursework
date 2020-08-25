package com.controller;

import com.model.Passenger;

public class PassengerQueue {

    private Passenger[] queueArray;

    private int front;                              // front point of the queue
    private int rear;                               // rear point of the queue
    private int maxLength;                          // max length of the queue
    private int count;                              // current size of the queue


    // constructor to initialize the queue
    PassengerQueue(int Length){

        queueArray = new Passenger[Length];
        maxLength = Length;
        front = 0;
        rear = -1;
        count = 0;

    }

    public int getLength(){
        return count;
    }

    // function to check if the queue is full
    public boolean isFull(){
        return (getLength() == maxLength);
    }

    // adding passenger to the queue
    public void add(Passenger nextPassenger){
        if(!isFull()){
            rear = (rear + 1) % maxLength;
            queueArray[rear] = nextPassenger;
            count++;
        }
    }


    // function to check if the queue is full
    public boolean isEmpty(){
        return (getLength() == 0);
    }

    // function to remove the front element of the queue
    private void remove(){
        if(!isEmpty()){
            front = (front + 1) % maxLength;
            count--;
        }
    }

    public Passenger[] returnTheQueueElements(){
        return queueArray;
    }

}
