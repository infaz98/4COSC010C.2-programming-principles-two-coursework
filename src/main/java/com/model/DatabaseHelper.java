package com.model;

import com.mongodb.BasicDBObject;
import com.mongodb.client.*;
import org.bson.Document;

import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseHelper {

    private static String collectionName;
    private static MongoDatabase database;
    private static MongoClient mongoClient;

    public static void setCollectionName(String collectionName) {
        DatabaseHelper.collectionName = collectionName;
    }

    public static String getCollectionName() {
        return collectionName;
    }

    public static MongoClient getMongoClient() {
        return mongoClient;
    }

    public static MongoDatabase getDatabase() {
        return database;
    }

    public static void setDatabase(MongoDatabase database) {
        DatabaseHelper.database = database;
    }


    /**
     * Connect to the Mongo Atlas Database
     * DataBase Name - IIT
     */
    public static void connectDB() {

        database = getDatabase();
        mongoClient = getMongoClient();

        Logger mongoLogging = Logger.getLogger("org.mongodb.driver");
        mongoLogging.setLevel(Level.SEVERE);

        mongoClient = MongoClients.create({"Insert the Mongo DB Driver"});
        database = mongoClient.getDatabase("IIT");

        setDatabase(database);

    /**
     * disconnect from the Mongo Atlas Database
     */
    public static void disconnectDB() {
        mongoClient = getMongoClient();
        mongoClient.close();
    }


    /**
     * update the status of passenger default value is "notBoard"
     * seat Number and updated result will be taken as parameters
     */
    public static void updateDB(int seatNumber, String status) {

        MongoDatabase database = DatabaseHelper.getDatabase();
        MongoCollection<Document> collection = database.getCollection(collectionName);

        BasicDBObject document = new BasicDBObject();
        document.append("$set", new BasicDBObject().append("status", status));
        BasicDBObject search = new BasicDBObject().append("seatNumber", seatNumber);
        collection.updateMany(search, document);


    }


}
