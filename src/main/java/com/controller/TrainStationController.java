package com.controller;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
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

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Logger;

public class TrainStationController {

    private static Random random = new Random();
    private static Scanner scanner = new Scanner(System.in);
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd,MM,yyyy");
    private static Logger logger = Logger.getLogger(TrainStationController.class.getName());


    private static double xOffset = 0;
    private static double yOffset = 0;

    private static int[] trainQueueCount = {0};                //instance to get the current iteration of train queue count
    private static int[] trainQueueLabelCount = {0};           //instance to get the current iteration train queue label count

    private static PassengerQueue passengerQueueObject;        // Passenger Queue - length of this object define at a time how many passengers should add to the train queue at a time

    @FXML
    DatePicker datePicker;
    @FXML
    Label labelDetail;
    @FXML
    public Circle closeButton;
    @FXML
    private Label trainQueue1, trainQueue2, trainQueue3, trainQueue4, trainQueue5, trainQueue6;
    @FXML
    private Label label1, label2, label3, label4, label5, label6, label7, label8, label9, label10, label11, label12, label13, label14, label15, label16, label17, label18, label19, label20, label21;
    @FXML
    private Label label22, label23, label24, label25, label26, label27, label28, label29, label30, label31, label32, label33, label34, label35, label36, label37, label38, label39, label40, label41, label42;
    @FXML
    Label randomLabel;

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

    /**
     * when user Pressed "A" PassengerQueue page will load and run the init method to initialize the PassengerQueue GUI
     * @throws IOException if file not fount then throws the IOException
     */
    public static void movePassengerToTrainQueueGUI() throws IOException {


        FXMLLoader loader = new FXMLLoader(TrainStationController.class.getClassLoader().getResource("PassengerQueue.fxml"));
        Parent root = loader.load();
        TrainStationController trainStationController = loader.<TrainStationController>getController();
        trainStationController.inti();

        Scene scene = new Scene(root, 1088, 930);
        scene.setFill(Color.TRANSPARENT);

        Stage stage = new Stage();

        stage.initStyle(StageStyle.TRANSPARENT);
  //    stage.getIcons().add(new Image(TrainStationController.class.getResourceAsStream("../view/Icon.jpg")));
        stage.setTitle("Adding Customers to the Train Queue");
        stage.setScene(scene);
        moveScreen(root, stage);
        stage.showAndWait();

        Passenger[] tem = passengerQueueObject.returnTheQueueElements();
        Passenger[] trainQueue = TrainStation.getTrainQueue();

        for (Passenger passenger : tem) {
            if (passenger != null) {
                trainQueue[trainQueueCount[0]] = passenger;
                trainQueueCount[0] = trainQueueCount[0] + 1;
            }
        }

        TrainStation.setTrainQueue(trainQueue);
    }

    /**
     * Initialize the waiting room and the train queue GUI before loading
     */
    public void inti() {

        int randNumber = random.nextInt(6) + 1;

        passengerQueueObject = new PassengerQueue(randNumber);
        trainQueueLabelCount[0] = 0;

        Label[] waitingRoomLabels = {label1, label2, label3, label4, label5, label6, label7, label8, label9, label10, label11, label12, label13, label14, label15, label16, label17, label18, label19, label20, label21};
        int waitingRoomLabelCount = 0;

        Passenger[] waitingRoom = TrainStation.getWaitingRoom();

        for (Passenger passenger : waitingRoom) {
            if (passenger != null) {
                waitingRoomLabels[waitingRoomLabelCount].setText(passenger.getSeatNumber() + " - " + passenger.getFirstName() + " " + passenger.getLastName());
                waitingRoomLabelCount++;
            }
        }

        String message;
        if (DatabaseHelper.getCollectionName() == null) {
            message = "No Collection Available";
        } else {
            message = "Opened Collection : " + DatabaseHelper.getCollectionName();
        }
        labelDetail.setText(message);

        int waitingRoomCount = 0;
        for (Passenger passenger : waitingRoom) {
            if (passenger != null) {
                waitingRoomCount++;
            }
        }

        if (waitingRoomCount > 0) {
            randomLabel.setText("Move " + Integer.toString(randNumber) + " Passengers from Waiting Room\nto Train Queue By Clicking on the Passenger");
        }
    }


    // when a passenger label is clicked at waiting room section relevent passenger will moved to the train queue
    public void addPassengerToTrainQueue(MouseEvent event) {

        Label[] trainQueueLabels = {trainQueue1, trainQueue2, trainQueue3, trainQueue4, trainQueue5, trainQueue6};
        Label[] waitingRoomLabels = {label1, label2, label3, label4, label5, label6, label7, label8, label9, label10, label11, label12, label13, label14, label15, label16, label17, label18, label19, label20, label21};

        Passenger[] waitingRoom = TrainStation.getWaitingRoom();

        String passengerName = ((Label) event.getSource()).getText();
        String[] tokens = passengerName.split(" - ");


        if (!passengerQueueObject.isFull()) {
            if (!(passengerName.equals(""))) {
                for (int z = 0; z < waitingRoom.length; z++) {
                    if (waitingRoom[z] != null) {
                        if ((waitingRoom[z].getFirstName() + " " + waitingRoom[z].getLastName()).equals(tokens[1])) {
                            passengerQueueObject.add(waitingRoom[z]);

                            for (Label label : waitingRoomLabels) {
                                String passenger = label.getText();
                                if (passenger.equals(passengerName)) {
                                    label.setText("");
                                }
                            }

                            trainQueueLabels[trainQueueLabelCount[0]].setText(waitingRoom[z].getSeatNumber() + " - " + (waitingRoom[z].getFirstName() + " " + waitingRoom[z].getLastName()));
                            trainQueueLabelCount[0] = trainQueueLabelCount[0] + 1;
                            DatabaseHelper.updateDB(waitingRoom[z].getSeatNumber(), "trainQueue");
                            waitingRoom[z] = null;
                        }
                    }
                }
            }

        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Number of Passengers to Move at a Time Reached");
            alert.setHeaderText("Please Board Passengers to the Train from the Train Queue (R) or\nPress (A) and add a Another set to the Train Queue");
            alert.showAndWait();
        }
    }


    public void loadPassengerDetailsFromDB() throws ParseException {

        int randNumber = random.nextInt(6) + 1;
        passengerQueueObject = new PassengerQueue(randNumber);


        Label[] waitingRoomLabels = {label1, label2, label3, label4, label5, label6, label7, label8, label9, label10, label11, label12, label13, label14, label15, label16, label17, label18, label19, label20};

        Passenger[] waitingRoom = TrainStation.getWaitingRoom();

        clearQueues();

        for (Label label : waitingRoomLabels) {
            label.setText("");
        }

        String date = " ";

        LocalDate localDate = datePicker.getValue();

        if (localDate != null) {
            date = formatter.format(localDate);
        }

        insertIntoQueues(date);

        //setting up the waiting room labels name
        int x;
        for (x = 0; x < waitingRoom.length; x++) {
            if (waitingRoom[x] != null) {
                waitingRoomLabels[x].setText(waitingRoom[x].getSeatNumber() + " - " + waitingRoom[x].getFirstName() + " " + waitingRoom[x].getLastName());
            }
        }

        String message;
        if (DatabaseHelper.getCollectionName() == null) {
            message = "No Collection Available";
        } else {
            message = "Opened Collection : " + DatabaseHelper.getCollectionName();
        }
        labelDetail.setText(message);

        //showing number of passengers to move to train queue if the waiting room is not empty
        int waitingRoomPassengerCount = 0;
        for (Passenger passenger : waitingRoom) {
            if (passenger != null) {
                waitingRoomPassengerCount++;
            }
        }
        if (waitingRoomPassengerCount > 0) {
            randomLabel.setText("Move " + Integer.toString(randNumber) + " Passengers from Waiting Room\nto Train Queue By Clicking on the Passenger");
        } else {
            randomLabel.setText("No Passengers to Move from Waiting Room");
        }

        //getting the number of passengers currently at the train queue
        Passenger[] trainQueue = TrainStation.getTrainQueue();
        int count = 0;
        for (Passenger passenger : trainQueue) {
            if (passenger != null) {
                count++;
            }
        }

        trainQueueCount[0] = count;
    }

    /**
     * clearing waiting room and train queue before loading an another set of data
     */
    public void clearQueues() {

        //setting up all the counter variables to the initial values
        trainQueueLabelCount[0] = 0;
        trainQueueCount[0] = 0;

        int x;
        for (x = 0; x < TrainStation.getWaitingRoom().length; x++) {
            TrainStation.getWaitingRoom()[x] = null;
        }
        for (x = 0; x < TrainStation.getTrainQueue().length; x++) {
            TrainStation.getTrainQueue()[x] = null;
        }
        for (x = 0; x < TrainStation.getBoardPassenger().length; x++) {
            TrainStation.getBoardPassenger()[x] = null;
        }
    }

    /**
     * insert into relevant queues while collection has documents
     * @param collectionName user input get by the date picker or from the console
     * @return boolean value true if collection exist in the database or false when not
     */
    public boolean insertIntoQueues(String collectionName) throws ParseException {

        Passenger[] waitingRoom = TrainStation.getWaitingRoom();
        Passenger[] trainQueue = TrainStation.getTrainQueue();
        Passenger[] boardPassenger = TrainStation.getBoardPassenger();

        int waitingCount = 0;
        int trainQCount = 0;
        int boardCount = 0;

        MongoDatabase database = DatabaseHelper.getDatabase();
        MongoCollection<Document> collection = database.getCollection(collectionName);

        //checking if the collection name is available at the database collection
        boolean isExists = database.listCollectionNames().into(new ArrayList<>()).contains(collectionName);

        if (!isExists) {
            System.out.println("No Booking Details found Try another date");
            DatabaseHelper.setCollectionName(null);

        } else {
            DatabaseHelper.setCollectionName(collectionName);

            MongoCursor<Document> cursor = collection.find().iterator();
            JSONParser parser = new JSONParser();

            //setting the passenger object and its instance while the collection has necessary records
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

                String status = (String) json.get("status");

                switch (status) {
                    case "notBoard":
                        waitingRoom[waitingCount] = passenger;
                        waitingCount++;
                        break;
                    case "trainQueue":
                        trainQueue[trainQCount] = passenger;
                        trainQCount++;
                        break;
                    case "board":
                        boardPassenger[boardCount] = passenger;
                        boardCount++;
                        break;
                }
            }

            TrainStation.setWaitingRoom(waitingRoom);
            TrainStation.setTrainQueue(trainQueue);
            TrainStation.setBoardPassenger(boardPassenger);
        }
        return isExists;
    }


    public static void viewPassengerInformation() throws IOException {

      FXMLLoader loader = new FXMLLoader(PassengerQueue.class.getClassLoader().getResource("PassengerView.fxml"));
Parent root = loader.load();
TrainStationController trainStationController = loader.<TrainStationController>getController();
trainStationController.initializeViewPassengerInfo();

        Scene scene = new Scene(root, 1188, 930);
        scene.setFill(Color.TRANSPARENT);

        Stage stage = new Stage();
        stage.initStyle(StageStyle.TRANSPARENT);
        //stage.getIcons().add(new Image(TrainStationController.class.getResourceAsStream("../view/Icon.jpg")));
        stage.setTitle("Adding Customers to the Waiting Room");
        stage.setScene(scene);
        moveScreen(root, stage);
        stage.showAndWait();
    }


    // to change the state of the navigation animation
    private boolean isRight = true;
    @FXML
    Label navigator, labelTitle;
    @FXML
    Label trainQueueAnim, waitingRoomAnim;
    @FXML
    Label labelView1, labelView2, labelView3, labelView4, labelView5, labelView6, labelView7, labelView8, labelView9, labelView10, labelView11, labelView12;
    @FXML
    Label labelView13, labelView14, labelView15, labelView16, labelView17, labelView18, labelView19, labelView20;


    public void initializeViewPassengerInfo() {

        Label[] labelViews = {labelView1, labelView2, labelView3, labelView4, labelView5, labelView6, labelView7, labelView8, labelView9, labelView10, labelView11, labelView12,
                labelView11, labelView12, labelView13, labelView14, labelView15, labelView16, labelView17, labelView18, labelView19, labelView20};

        Label[] labels = {label1, label2, label3, label4, label5, label6, label7, label8, label9, label10, label11, label12, label13, label14, label15, label16, label17, label18, label19, label20, label21,
                label22, label23, label24, label25, label26, label27, label28, label29, label30, label31, label32, label33, label34, label35, label36, label37, label38, label39, label40, label41, label42};

        Passenger[] waitingRoom = TrainStation.getWaitingRoom();
        Passenger[] trainQueue = TrainStation.getTrainQueue();
        Passenger[] boardPassenger = TrainStation.getBoardPassenger();

        int labelViewCount = 0;

        trainQueueAnim.setStyle("-fx-text-fill: white;");

        waitingRoomAnim.setStyle("-fx-text-fill: #666666;");

        for (Passenger passenger : waitingRoom) {
            if (passenger != null) {
                labels[passenger.getSeatNumber() - 1].setStyle("-fx-background-color: #ffb296;  -fx-background-radius: 8;");
            }
        }
        for (Passenger passenger : trainQueue) {
            if (passenger != null) {
                labels[passenger.getSeatNumber() - 1].setStyle("-fx-background-color: #f7fa98;  -fx-background-radius: 8;");
            }
        }

        for (Passenger passenger : boardPassenger) {
            if (passenger != null) {
                labels[passenger.getSeatNumber() - 1].setStyle("-fx-background-color: #83f78d;  -fx-background-radius: 8;");
            }
        }
        for (Passenger passenger : trainQueue) {
            if (passenger != null) {
                labelViews[labelViewCount].setText(String.valueOf(passenger.getSeatNumber()) + " " + passenger.getFirstName() + " " + passenger.getLastName());
                labelViewCount++;
            }
        }

        String message;
        if (DatabaseHelper.getCollectionName() != null) {
            labelDetail.setText("Date : " + DatabaseHelper.getCollectionName());
        }
    }

    /**
     * change the navigation of the ">" according to the mouse click
     * @param event
     */
    public void changeNavigator(MouseEvent event) {

        Label[] labelViews = {labelView1, labelView2, labelView3, labelView4, labelView5, labelView6, labelView7, labelView8, labelView9, labelView10, labelView11, labelView12,
                labelView11, labelView12, labelView13, labelView14, labelView15, labelView16, labelView17, labelView18, labelView19, labelView20};

        Passenger[] waitingRoom = TrainStation.getWaitingRoom();
        Passenger[] trainQueue = TrainStation.getTrainQueue();

        int labelCount = 0;

        for (Label label : labelViews) {
            label.setText("");
        }

        if (isRight) {
            navigator.setText("<");
            isRight = false;
            labelTitle.setText("Passengers at Waiting Room");
            waitingRoomAnim.setStyle("-fx-text-fill: white;");
            trainQueueAnim.setStyle("-fx-text-fill: #666666;");
            for (Passenger passenger : waitingRoom) {
                if (passenger != null) {
                    labelViews[labelCount].setText(String.valueOf(passenger.getSeatNumber()) + " . " + passenger.getFirstName() + " " + passenger.getLastName());
                    labelCount++;
                }
            }
        } else {
            navigator.setText(">");
            isRight = true;
            labelTitle.setText("Passengers at Train Queue");
            trainQueueAnim.setStyle("-fx-text-fill: white;");
            waitingRoomAnim.setStyle("-fx-text-fill: #666666;");
            for (Passenger passenger : trainQueue) {
                if (passenger != null) {
                    labelViews[labelCount].setText(String.valueOf(passenger.getSeatNumber()) + " . " + passenger.getFirstName() + " " + passenger.getLastName());
                    labelCount++;
                }
            }
        }
    }


    /**
     * if the passenger views' seat number clicked then fetching the relevant seat information
     * @param event passenger name label
     */
    public void getData(MouseEvent event) {

        Passenger[] waitingRoom = TrainStation.getWaitingRoom();
        Passenger[] trainQueue = TrainStation.getTrainQueue();
        Passenger[] boardPassenger = TrainStation.getBoardPassenger();
        String seatNumber = ((Label) event.getSource()).getText();

        int seat = Integer.parseInt(seatNumber);

        for (Passenger passenger : waitingRoom) {
            if (passenger != null && seat == passenger.getSeatNumber()) {

                String message = "Passenger is currently at the Waiting Room\nDenuwara Manike Colombo to Badulla";
                Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
                alert.setTitle("Passenger Information");
                alert.setHeaderText(passenger.getFirstName() + " " + passenger.getLastName());
                alert.showAndWait();
            }
        }

        for (Passenger passenger : trainQueue) {
            if (passenger != null && seat == passenger.getSeatNumber()) {

                String message = "Passenger is currently at the Train Queue\nDenuwara Manike Colombo to Badulla";
                Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
                alert.setTitle("Passenger Information");
                alert.setHeaderText(passenger.getFirstName() + " " + passenger.getLastName());
                alert.showAndWait();
            }
        }

        for (Passenger passenger : boardPassenger) {
            if (passenger != null && seat == passenger.getSeatNumber()) {

                String message = "Passenger is currently Seated at inside the Train\nDenuwara Manike Colombo to Badulla";
                Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
                alert.setTitle("Passenger Information");
                alert.setHeaderText(passenger.getFirstName() + " " + passenger.getLastName());
                alert.showAndWait();
            }
        }
    }

    @FXML
    Label numberOfPassenger, averageTimeInQueue, maximumTimeInQueue, minimumTImeInQueue;
    @FXML
    Label labelTime1, labelTime2, labelTime3, labelTime4, labelTime5, labelTime6;
    @FXML
    public TextField fileName;

    static int[] boardPassengerCount = {0};
    private static int totalTimeInTrainQueue;
    private static Passenger longestPassenger = null;
    private static Passenger shortestPassenger = null;



    /**
     * when "R" is pressed run the simulation and load the summery
     * meantime updates the database that the relevant passenger status to board
     */
    public static void simulation() throws IOException {

        Passenger[] trainQueue = TrainStation.getTrainQueue();
        Passenger[] boardPassengers = TrainStation.getBoardPassenger();

        int passengerCountForAnimation = 0;

        for (Passenger passenger : trainQueue) {
            if (passenger != null) {
                passengerCountForAnimation++;
            }
        }
        if (passengerCountForAnimation > 0) {
            processingAnimation();
        }

        int dice1;
        int dice2;
        int dice3;

        int shortestTime = 60;
        int longestTime = 0;
        int passengerCount = 0;

        for (Passenger passenger : trainQueue) {
            if (passenger != null) {
                dice1 = random.nextInt(6) + 1;
                dice2 = random.nextInt(6) + 1;
                dice3 = random.nextInt(6) + 1;
                passenger.setSecondsInQueue(dice1 + dice2 + dice3);

            }
        }

        for (Passenger passenger : trainQueue) {
            if (passenger != null) {
                if (passenger.getSecondsInQueue() < shortestTime) {
                    shortestTime = passenger.getSecondsInQueue();
                    shortestPassenger = passenger;
                }

                if (passenger.getSecondsInQueue() > longestTime) {
                    longestTime = passenger.getSecondsInQueue();
                    longestPassenger = passenger;
                }

                totalTimeInTrainQueue = totalTimeInTrainQueue + passenger.getSecondsInQueue();
                passengerCount = passengerCount + 1;
            }
        }


        FXMLLoader loader = new FXMLLoader(TrainStationController.class.getClassLoader().getResource("TrainQueue.fxml"));
        Parent root = loader.load();
        TrainStationController trainStationController = loader.<TrainStationController>getController();

        trainStationController.initializeTrainQueueSummery(totalTimeInTrainQueue, shortestPassenger, longestPassenger);

        Scene scene = new Scene(root, 939, 849);
        scene.setFill(Color.TRANSPARENT);

        Stage stage = new Stage();
        stage.initStyle(StageStyle.TRANSPARENT);
    //  stage.getIcons().add(new Image(TrainStationController.class.getResourceAsStream("../view/Icon.jpg")));
        stage.setTitle("Adding Customers to the Waiting Room");
        stage.setScene(scene);
        moveScreen(root, stage);
        stage.showAndWait();

        for (int x = 0; x < trainQueue.length; x++) {
            if (trainQueue[x] != null) {
                boardPassengers[boardPassengerCount[0]] = trainQueue[x];
                boardPassengerCount[0] = boardPassengerCount[0] + 1;
                DatabaseHelper.updateDB(trainQueue[x].getSeatNumber(), "board");
                trainQueue[x] = null;
            }
        }

        TrainStation.setBoardPassenger(boardPassengers);
        TrainStation.setTrainQueue(trainQueue);

    }

    /**
     * summarize and setup the Train Queue summery page before it is load
     */
    public void initializeTrainQueueSummery(float totalTimeCount, Passenger shortestPassenger, Passenger longestPassenger) {

        Label[] passengerNameLabel = {label1, label2, label3, label4, label5, label6, label7, label8, label9, label10, label11, label12, label13, label14, label15, label16, label17, label18, label19, label20};

        Label[] passengerTimeLabel = {label21, label22, label23, label24, label25, label26, label27, label28, label29, label30, label31, label32, label33, label34, label35, label36, label37, label38, label39, label40};


        Passenger[] trainQueue = TrainStation.getTrainQueue();

        int labelCount = 0;
        int passengerCount = 0;

        for (Passenger passenger : trainQueue) {
            if (passenger != null) {
                passengerCount++;
            }
        }

        if (passengerCount != 0) {
            float average = totalTimeCount / passengerCount;

            averageTimeInQueue.setText(String.valueOf(average));
            maximumTimeInQueue.setText(String.valueOf(longestPassenger.getSecondsInQueue()));
            minimumTImeInQueue.setText(String.valueOf(shortestPassenger.getSecondsInQueue()));
            numberOfPassenger.setText(String.valueOf(passengerCount));
        }

        for (Passenger passenger : trainQueue) {
            if (passenger != null) {
                passengerNameLabel[labelCount].setText(passenger.getFirstName() + " " + passenger.getLastName());
                passengerTimeLabel[labelCount].setText(String.valueOf(passenger.getSecondsInQueue()));
                labelCount++;
            }
        }

    }


    /**
     * console progress bar to demonstrate the processing time
     */
    public static void processingAnimation() {
        char[] animationChars = new char[]{'|', '/', '-', '\\'};

        for (int x = 0; x <= 100; x++) {
            System.out.print("\t\tProcessing : " + x + "% " + animationChars[x % 4] + "\r");

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //calling a method inside the fxml onClicked function
    @FXML
    public void creatingTrainQueueFile(){
        generateTrainQueueFile(shortestPassenger, longestPassenger);
    }

    /**
     * after the simulation according the name given by the passenger in GUI console a report will be generate
     */
    public void generateTrainQueueFile(Passenger shortestPassenger, Passenger longestPassenger) {

        int passengerCount = 0;
        float totalTime = 0;
        Passenger[] trainQueue = TrainStation.getTrainQueue();

        for (Passenger passenger : trainQueue) {
            if (passenger != null) {
                passengerCount++;
                totalTime = totalTime + passenger.getSecondsInQueue();
            }
        }

        String fileN = fileName.getText();
        if (fileN.equals("")) {
            fileN = "default";
        }
        try {
            FileWriter fileWriter = new FileWriter(fileN);
            fileWriter.write("* Individual Statistics\n\nSeat Number,Processing Time,Passenger Name\n\n");
            for (Passenger passenger : trainQueue) {
                if (passenger != null) {
                    fileWriter.write(passenger.getSeatNumber() + "\t" + passenger.getSecondsInQueue() + "\t" + passenger.getFirstName() + " " + passenger.getLastName() + "\n");
                }
            }
            fileWriter.write("\n\n");
            fileWriter.write("* Summery\n\n");
            fileWriter.write("Total Passengers : " + passengerCount + "\n");
            fileWriter.write("Average Time Passenger Waited in Queue : " + totalTime / passengerCount+ "\n");
            fileWriter.write("Longest Waiting Time : " +longestPassenger.getFirstName()+" "+longestPassenger.getLastName()+" - "+longestPassenger.getSecondsInQueue()+"\n");
            fileWriter.write("Shortest Waiting Time : " +shortestPassenger.getFirstName()+" "+shortestPassenger.getLastName()+" - "+shortestPassenger.getSecondsInQueue()+"\n");
            fileWriter.close();
            System.out.println("File Saved");


        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }


    /**
     * @return passenger who is removed from the train queue according to the user input
     */
    public static Passenger removePassengerFromTrainQueue() {

        Passenger[] trainQueue = TrainStation.getTrainQueue();
        String seatNumber;
        int seatNumberInt = 0;
        String userInput = "";
        boolean isPassengerFound = false;
        Passenger removedPassenger = null;

        int passengerCount = 0;
        for (Passenger passenger : trainQueue) {
            if (passenger != null) {
                passengerCount++;
            }
        }
        if (passengerCount > 0) {
            System.out.print("Please Enter the Seat Number: ");
            seatNumber = scanner.next();
            try {
                seatNumberInt = Integer.parseInt(seatNumber);
            } catch (InputMismatchException | NumberFormatException e) {
                System.out.println("Invalid Selection ");
            }
            for (int x = 0; x < trainQueue.length; x++) {
                if (trainQueue[x] != null && trainQueue[x].getSeatNumber() == seatNumberInt) {

                    System.out.print("Confirm whether Passenger Name is " + trainQueue[x].getFirstName() + " " + trainQueue[x].getLastName() + " (Y/N) : ");
                    userInput = scanner.next();

                    if (userInput.equals("Y")) {
                        isPassengerFound = true;
                        System.out.println(trainQueue[x].getFirstName() + " " + trainQueue[x].getLastName() + " is Deleted From the Train Queue");
                        removedPassenger = trainQueue[x];
                        trainQueue[x] = null;
                        DatabaseHelper.updateDB(seatNumberInt, "deleted");
                    } else {
                        System.out.println("Couldn't find any passenger\n\nTry these:-\n1. Make Sure to Load Collection\n2. Try under an another Date\n");
                    }
                }
            }

            if (!isPassengerFound) {
                System.out.println("Sorry Couldn't Find any Passenger under Given Seat Number");
            }

            //rearrange the train queue
            Passenger[] temp = new Passenger[42];
            int tempCount = 0;

            for (Passenger passenger : trainQueue) {
                if (passenger != null) {
                    temp[tempCount] = passenger;
                    tempCount++;
                }
            }
            TrainStation.setTrainQueue(temp);
        } else {
            System.out.println("No Passengers Available at Train Queue to Remove");
        }

        return removedPassenger;
    }

    public static void saveTrainQueueInformation() {
        System.out.println("Passenger Details Categorize and Auto Saving Under:");
        System.out.println("\t\t\t\tPassengers at Waiting Room\n\t\t\t\tPassengers at Train Queue\n\t\t\t\tPassengers who Boarded into the Train\n");
    }


    /**
     * load the collection from data base according to the user input when user pressed "L"
     */
    public void loadingTrainQueueInformation() throws ParseException {

        int temp;
        boolean isDayValidated = false;
        boolean isMonthValidate = false;
        boolean isYearValidate = false;

        String userInput;

        System.out.print("Please Enter the File Name in (DD,MM,YYYY) Format : ");

        userInput = scanner.next();


        String[] split = userInput.split(",");

        //validating day is integer and between 1 and 31
        try {
            temp = Integer.parseInt(split[0]);
            if (temp <= 31 && 1 <= temp) {
                isDayValidated = true;
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
        }

        //validating Month is integer and between 1 and 12
        try {
            temp = Integer.parseInt(split[1]);
            if (temp <= 12 && 1 <= temp) {
                isMonthValidate = true;
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
        }
        //validating whether the year is integer and 2020
        try {
            temp = Integer.parseInt(split[2]);
            if (temp == 2020) {
                isYearValidate = true;
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
        }

        //if the user input is correct proceed else print invalid input
        if (isDayValidated && isMonthValidate && isYearValidate) {
            DatabaseHelper.setCollectionName(userInput);

            clearQueues();

            boolean isExist = insertIntoQueues(userInput);

            //getting the number of passengers currently at the train queue
            Passenger[] trainQueue = TrainStation.getTrainQueue();
            int count = 0;
            for (Passenger passenger : trainQueue) {
                if (passenger != null) {
                    count++;
                }
            }

            trainQueueCount[0] = count;

            if (isExist) {
                System.out.println("Loaded Successfully");
            }

        } else {
            System.out.println("Invalid File Name");
        }
    }

    /**
     * When user pressed the red circle close the Stage
     */

    @FXML
    public void closeWindow(MouseEvent event) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }


}
