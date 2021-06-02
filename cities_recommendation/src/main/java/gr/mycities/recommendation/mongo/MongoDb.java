package gr.mycities.recommendation.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import gr.mycities.recommendation.MyConstants;

// returns the mongo db client, which gives us access to the database
// singleton pattern
public class MongoDb {

    private static MongoClient mongoClient;

    private MongoDb() {
    }

    public static MongoClient getMongoDbClient() {
        if (mongoClient == null) {
            mongoClient = new MongoClient(new MongoClientURI(MyConstants.MONGO_URI));
        }
        return mongoClient;
    }
    
    public static void closeConnection() {
        mongoClient.close();
        mongoClient = null;
    }

}
