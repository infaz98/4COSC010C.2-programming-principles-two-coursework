package com.controller;

import com.mongodb.MongoSocketException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.model.DatabaseHelper;
import com.model.Passenger;
import org.json.simple.parser.ParseException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.logging.Logger;

public class TrainStation extends Application {
    private static Logger logger = Logger.getLogger(TrainStation.class.getName());

    private static Passenger[] waitingRoom = new Passenger[42];
    private static Passenger[] trainQueue = new Passenger[42];

    //additional passenger[] to hold the passengers who already inside the train
    private static Passenger[] boardPassenger = new Passenger[42];

    public static void setBoardPassenger(Passenger[] boardPassenger) {
        TrainStation.boardPassenger = boardPassenger;
    }

    public static void setWaitingRoom(Passenger[] waitingRoom) {
        TrainStation.waitingRoom = waitingRoom;
    }

    public static void setTrainQueue(Passenger[] trainQueue) {
        TrainStation.trainQueue = trainQueue;
    }

    public static Passenger[] getBoardPassenger() {
        return boardPassenger;
    }

    public static Passenger[] getWaitingRoom() {
        return waitingRoom;
    }

    public static Passenger[] getTrainQueue() {
        return trainQueue;
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        menuOptions();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void menuOptions() throws IOException, ParseException, RuntimeException {

        //connect to the Mongo Atlas at the begin
        try {
            DatabaseHelper.connectDB();
        } catch (RuntimeException e) {
            System.out.println(e);
            logger.info("Make Sure You have Internet Connection !\n");
        }
        openingScene();

        String userInput;
        Scanner scanner = new Scanner(System.in);

        do {
            options();
            System.out.println("{A,D,V,R,S,L,B,F,O,d}");
            System.out.print("\nPlease Select an Appropriate Letter: ");

            userInput = scanner.next();
            System.out.println();

            switch (userInput) {
                case "A":
                    try {
                        TrainStationController.movePassengerToTrainQueueGUI();
                    } catch (RuntimeException e) {
                        internetConnectivityErrorMessage(e);
                    }
                    break;
                case "V":
                    TrainStationController.viewPassengerInformation();
                    break;
                case "S":
                    TrainStationController.saveTrainQueueInformation();
                    break;
                case "D":
                    try {
                        TrainStationController.removePassengerFromTrainQueue();
                    } catch (RuntimeException e) {
                        internetConnectivityErrorMessage(e);
                    }
                    break;
                case "L":
                    TrainStationController trainStationController = new TrainStationController();
                    trainStationController.loadingTrainQueueInformation();
                    break;
                case "R":
                    try {
                        TrainStationController.simulation();
                    } catch (RuntimeException e) {
                        internetConnectivityErrorMessage(e);
                    }
                    break;
                case "B":
                    try {
                        PassengerBooking.seatBooking();
                    } catch (RuntimeException e) {
                        internetConnectivityErrorMessage(e);
                    }
                    break;
                case "O":
                    PassengerBooking.sortPassengers();
                    break;
                case "F":
                    PassengerBooking.searchByName();
                    break;
                case "d":
                    try {
                        PassengerBooking.deletePassenger();
                    } catch (RuntimeException e) {
                        internetConnectivityErrorMessage(e);
                    }
                    break;
                default:
                    if (!userInput.equals("Q")) {
                        System.out.println("Invalid Selection Try a Letter Shown in below the Table");
                    }

            }

        } while (!(userInput.equals("Q")));
        try {
            DatabaseHelper.disconnectDB();
        } catch (RuntimeException e) {
            System.out.println(e);
            System.out.println("Program didn't Closed Properly due to Issue in Internet Connectivity");
        }
    }

    //  progress bar animation and the title of the program at the beginning of the program
    public static void openingScene() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        System.out.println("!***********************************  Welcome to Ticketing Management System  ****************************************!");
        System.out.println("");
        System.out.println("                                      Denuwara Menike     " + dtf.format(now) + "\n");
        for (int v = 0; v <= 200; v = v + 20) {
            progressPercentage(v);
            try {
                Thread.sleep(300);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void progressPercentage(int remain) {
        if (remain > 200) {
            throw new IllegalArgumentException();
        }
        int maxBareSize = 10;
        int remainPercent = ((100 * remain) / 200) / maxBareSize;
        char defaultChar = '-';
        String icon = "*";
        String bare = new String(new char[maxBareSize]).replace('\0', defaultChar) + "]";
        StringBuilder bareDone = new StringBuilder();
        bareDone.append("                                                [");
        for (int i = 0; i < remainPercent; i++) {
            bareDone.append(icon);
        }
        String bareRemain = bare.substring(remainPercent, bare.length());
        System.out.print("\r" + bareDone + bareRemain + " " + remainPercent * 10 + "%");
        if (remain == 200) {
            System.out.print("\n");
        }

    }

    public static void options() {
        System.out.println(" _____________________________________________________________________________________________________________________");
        System.out.println("|                                                                                                                     |");
        System.out.println("| A. Add Passenger to the Train Queue                          V. View Train Queue and Seating                        |");
        System.out.println("| D. Delete Passenger From Train Queue                         R. Run the Simulation (Board the Train Queue to Train) |");
        System.out.println("| S. Store the the Train Queue and Passenger Information       L. Load Train Queue and Passenger Information          |");
        System.out.println("|_____________________________________________________________________________________________________________________|");
        System.out.println("|                                                                                                                     |");
        System.out.println("| B. Book a Seat or Load Booking Information                   O. Sort Passengers                                     |");
        System.out.println("| F. Finding Customer Information                              d. Delete Booked Passenger                             |");
        System.out.println("|_____________________________________________________________________________________________________________________|");
    }

    private void internetConnectivityErrorMessage(Exception e) {
        System.out.println(e);
        System.out.println("Make Sure you Have Connected to the Internet");
    }

}
