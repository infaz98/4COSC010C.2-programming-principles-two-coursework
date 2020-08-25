package com.controller;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import com.model.DatabaseHelper;
import com.model.Passenger;
import org.bson.Document;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;

public class PassengerBooking {


    public static final int SEAT_CAPACITY = 42;

    @FXML
    private Label label1, label2, label3, label4, label5, label6, label7, label8, label9, label10, label11, label12, label13, label14, label15, label16, label17, label18, label19, label20, label21;
    @FXML
    private Label label22, label23, label24, label25, label26, label27, label28, label29, label30, label31, label32, label33, label34, label35, label36, label37, label38, label39, label40, label41, label42;

    @FXML
    Label collectionInfo;
    @FXML
    public TextField textFieldFirstName, textFieldLastName, textFieldSeat;

    @FXML
    private Circle closeButton;

    @FXML
    private DatePicker datePicker;

    private static Passenger[] bookedSeats = new Passenger[SEAT_CAPACITY];

    //counter variable to iterate through bookedSeats array
    private int[] arrayCount = {0};

    //variable to hols the collection name
    private static String bookingDate;

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd,MM,yyyy");
    private static Scanner scanner = new Scanner(System.in);

    private static double xOffset = 0;
    private static double yOffset = 0;

    /**
     * moving the window by click and drag (Stage style is TRANSPARENT)
     */
    public static void moveScreen(Parent root, Stage stage) {

        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        root.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
    }

    public static void seatBooking() throws IOException {

        
        Parent root = FXMLLoader.load(PassengerQueue.class.getClassLoader().getResource("PassengerBooking.fxml"));

        Scene scene = new Scene(root, 1030, 775);
        scene.setFill(Color.TRANSPARENT);

        Stage stage = new Stage();

        stage.initStyle(StageStyle.TRANSPARENT);

    //  stage.getIcons().add(new Image(PassengerBooking.class.getResourceAsStream("Icon.jpg")));
        stage.setTitle("Seat Booking");
        stage.setScene(scene);
        moveScreen(root, stage);
        stage.showAndWait();
    }

    @FXML
    private void clearFields() {
        textFieldFirstName.setText("");
        textFieldLastName.setText("");
        textFieldSeat.setText("");
    }

    //change the color of the booked seats into red according to availability
    public void changeColor() {
        Label[] labels = {label1, label2, label3, label4, label5, label6, label7, label8, label9, label10, label11, label12, label13, label14, label15, label16, label17, label18, label19, label20, label21,
                label22, label23, label24, label25, label26, label27, label28, label29, label30, label31, label32, label33, label34, label35, label36, label37, label38, label39, label40, label41, label42};
        for (Label label : labels) {
            label.setStyle("-fx-background-color: #d3e5f5; -fx-background-radius: 8;");
        }
        for (Passenger passenger : bookedSeats) {
            if (passenger != null) {
                labels[passenger.getSeatNumber() - 1].setStyle("-fx-background-color: red; -fx-background-radius: 8;");
            }
        }

    }

    //After selecting a Date then check the availability of the seats for that date and show booked seats in red color
    public void checkAvailability() throws ParseException {

        Label[] labels = {label1, label2, label3, label4, label5, label6, label7, label8, label9, label10, label11, label12, label13, label14, label15, label16, label17, label18, label19, label20, label21,
                label22, label23, label24, label25, label26, label27, label28, label29, label30, label31, label32, label33, label34, label35, label36, label37, label38, label39, label40, label41, label42};

        //clearing all the existing data before loading another data set
        for (int i = 0; i < SEAT_CAPACITY; i++) {
            if (bookedSeats[i] != null) {
                bookedSeats[i] = null;
            }
        }
        for (Label label : labels) {
            label.setStyle("-fx-background-color: #d3e5f5; -fx-background-radius: 8;");
        }
        arrayCount[0] = 0;

        String date = " ";
        LocalDate localDate = datePicker.getValue();

        if (localDate != null) {
            date = formatter.format(localDate);
        }
        MongoCollection<Document> collection = null;
        MongoDatabase database = DatabaseHelper.getDatabase();
        try{
            collection = database.getCollection(date);
        }catch (NullPointerException e){
            System.out.println(e);
            System.out.println("Make Sure You are Connected to Internet");
        }

        //checking if the collection name is available at the database collection
        boolean isExists = database.listCollectionNames().into(new ArrayList<>()).contains(date);

        if (!isExists) {
            collectionInfo.setText("No Bookings on the Date");
        } else {
            bookingDate = date;
            collectionInfo.setText("");
            MongoCursor<Document> cursor = collection.find().iterator();
            JSONParser parser = new JSONParser();

            while (cursor.hasNext()) {

                Passenger passenger = new Passenger();

                String document = cursor.next().toJson();
                JSONObject json = (JSONObject) parser.parse(document);

                String firstName = (String) json.get("firstName");
                passenger.setFirstName(firstName);

                String lastName = (String) json.get("lastName");
                passenger.setLastName(lastName);

                long seatNumber = (long) json.get("seatNumber");
                int seat = (int) seatNumber;
                passenger.setSeatNumber(seat);

                bookedSeats[arrayCount[0]] = passenger;
                arrayCount[0] = arrayCount[0] + 1;
            }

            for (Passenger passenger : bookedSeats) {
                if (passenger != null) {
                    labels[passenger.getSeatNumber() - 1].setStyle("-fx-background-color: red; -fx-background-radius: 8;");
                }
            }
        }
    }

    @FXML
    public void closeWindow(MouseEvent event) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void bookSeat() throws ParseException {

        int seatNumber = 0;
        String firstName = textFieldFirstName.getText();
        String lastName = textFieldLastName.getText();

        if (firstName.equals("") || lastName.equals("")) {
            firstName = "null";
            lastName = "null";
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        try {
            seatNumber = Integer.parseInt(textFieldSeat.getText());
        } catch (NumberFormatException e) {
            alert.setTitle("Booking Error");
            alert.setContentText("Please Enter a Correct Seat Number");
            alert.showAndWait();
            textFieldSeat.setText("");
        }

        for (Passenger passenger : bookedSeats) {
            if (passenger != null) {
                if (passenger.getSeatNumber() == seatNumber) {
                    alert.setTitle("Booking Error");
                    alert.setContentText("Seat Is Already Booked");
                    alert.showAndWait();
                    textFieldSeat.setText("");
                }
            }
        }

        String date = " ";
        LocalDate localDate = datePicker.getValue();
        if (localDate != null) {
            date = formatter.format(localDate);
        }

        MongoDatabase database = DatabaseHelper.getDatabase();
        MongoCollection<Document> collection = database.getCollection(date);

        //checking if the collection name is available at the database collection
        boolean isExists = database.listCollectionNames().into(new ArrayList<>()).contains(date);

        if ((seatNumber > 0 && seatNumber <= 42) && !(firstName.equals("null") && lastName.equals("null")) && localDate != null) {

            if (!isExists) {
                database.createCollection(date);
            }

            Document passengerDocument = new Document();

            passengerDocument.put("firstName", firstName);
            passengerDocument.put("lastName", lastName);
            passengerDocument.put("status", "notBoard");
            passengerDocument.put("seatNumber", seatNumber);

            collection.insertOne(passengerDocument);
            alert.setTitle("Booking Confirm");
            alert.setContentText("Booking has been Confirmed");
            alert.showAndWait();
            textFieldSeat.setText("");
        } else {
            System.out.println("Please Enter All Information");
        }
        checkAvailability();
        clearFields();
    }

    /**
     * on clicking the seat number label display passenger information related to the seat and ask whether need to delete
     */
    @FXML
    private void passengerInfo(MouseEvent event) {

        String date = " ";
        LocalDate localDate = datePicker.getValue();

        if (localDate != null) {
            date = formatter.format(localDate);
        }

        MongoCollection<Document> collection = null;
        MongoDatabase database = DatabaseHelper.getDatabase();
        try {
            collection = database.getCollection(date);

        }catch (NullPointerException e){
            System.out.println(e);
            System.out.println("Make Sure You are connected to the Internet");
        }

        int seatNumber = Integer.parseInt(((Label) event.getSource()).getText());

        for (Passenger passenger : bookedSeats) {
            if (passenger != null) {
                if (passenger.getSeatNumber() == seatNumber) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.initStyle(StageStyle.UNDECORATED);
                    alert.setTitle("Passenger Information");
                    alert.setHeaderText("Name : " + passenger.getFirstName() + " " + passenger.getLastName() + "\nSeat Number : " + passenger.getSeatNumber());
                    alert.setContentText("Do you Want to Delete the Passenger ?");
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == ButtonType.OK) {

                        for (int x = 0; x < SEAT_CAPACITY; x++) {
                            if (bookedSeats[x] != null) {
                                if (bookedSeats[x].getSeatNumber() == seatNumber) {
                                    System.out.println("Seat Number" + bookedSeats[x].getSeatNumber() + " for " + bookedSeats[x].getFirstName() + " " + bookedSeats[x].getLastName() + " has been deleted ");
                                    collection.deleteOne(Filters.eq("seatNumber", seatNumber));
                                    bookedSeats[x] = null;
                                    changeColor();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Sorting passenger names according to bubble ort algorithm and display along with seat number
     */
    public static void sortPassengers() {

        int passengerCount = 0;
        for (Passenger passenger : bookedSeats) {
            if (passenger != null) {
                passengerCount++;
            }
        }
        Passenger temp;

        int count = 0;
        Passenger[] sortedPassenger = new Passenger[passengerCount];
        for (int x = 0; x < SEAT_CAPACITY; x++) {
            if (bookedSeats[x] != null) {
                sortedPassenger[count] = bookedSeats[x];
                count++;
            }
        }
        System.out.println("\nSeat Number\t\t Customer Name ");
        System.out.println("---------------------------------");

        for (int j = 0; j < sortedPassenger.length; j++) {
            for (int i = j + 1; i < sortedPassenger.length; i++) {
                if ((sortedPassenger[i].getFirstName() + sortedPassenger[i].getLastName()).compareTo((sortedPassenger[j].getFirstName() + sortedPassenger[j].getLastName())) < 0) {
                    temp = sortedPassenger[j];
                    sortedPassenger[j] = sortedPassenger[i];
                    sortedPassenger[i] = temp;
                }
            }
            System.out.println(sortedPassenger[j].getSeatNumber() + "\t\t\t\t" + sortedPassenger[j].getFirstName() + " " + sortedPassenger[j].getLastName());
        }
        System.out.println("\n");
    }

    /**
     * Compare the first name and last name input by the user with the passenger object firstName and lastName instance if found display details
     */
    public static void searchByName() {
        String firstName;
        String lastName;
        boolean isFound = false;

        System.out.print("Please Enter the First Name of Passenger: ");
        firstName = scanner.next();

        System.out.print("Please Enter the Last Name of Passenger: ");
        lastName = scanner.next();

        for (Passenger passenger : bookedSeats) {
            if (passenger != null) {
                if ((passenger.getFirstName() + " " + passenger.getLastName()).equals(firstName + " " + lastName)) {
                    System.out.println("Seat Number for Passenger " + passenger.getFirstName() + " " + passenger.getLastName() + " is " + passenger.getSeatNumber());
                    System.out.println("Seat is booked for " + bookingDate);
                    System.out.println();
                    isFound = true;
                }
            }
        }
        if (!isFound) {
            System.out.print("Sorry Cant find any Information under" + " " + firstName + " " + lastName);
            System.out.println();
        }
    }
    // delete passenger from console
    public static void deletePassenger() {

        MongoDatabase database = DatabaseHelper.getDatabase();
        MongoCollection<Document> collection = null;
        try {
            collection = database.getCollection(bookingDate);
        } catch (IllegalArgumentException e){
            System.out.println("Please Make Sure the Database has been loaded");
        }
        String userInput;
        int userInt = 0;
        System.out.print("Please Enter the Seat Number: ");
        userInput = scanner.next();

        try {
            userInt = Integer.parseInt(userInput);
        } catch (NumberFormatException e) {
            System.out.println("Invalid Input");
        }
        for (int x = 0; x < SEAT_CAPACITY; x++) {
            if (bookedSeats[x] != null) {
                if (bookedSeats[x].getSeatNumber() == userInt) {
                    System.out.print("Confirm whether Passenger name is " + bookedSeats[x].getFirstName() + " " + bookedSeats[x].getLastName() + " (Y/N) : ");
                    userInput = scanner.next();
                    if ((userInput.equals("Y")) || userInput.equals("y")) {
                       try{
                           collection.deleteOne(Filters.eq("seatNumber", userInt));
                       }catch (NullPointerException e){
                           System.out.println("Make Sure the DataBase is Connected");
                       }
                        System.out.print("Seat Number for Passenger " + bookedSeats[x].getFirstName() + " " + bookedSeats[x].getLastName() + " is " + bookedSeats[x].getSeatNumber());
                        System.out.println(" Has been Deleted");
                       bookedSeats[x] = null;
                    }
                }
            }
        }
        if(userInput.equals("N")||userInput.equals("n")){
            System.out.println("Couldn't Find any Seat");
        }
    }
}
