
package gr.mycities.recommendation;


public class MyConstants {
    public static final String API_KEY = "fdad7e2bc06fbcbee2745a7428f14d39"; // the key for the weather api
    public static final String[] TERMS = {"sea", "mountain", "museum", "cafe", "restaurant", "music", "theater", "bar", "art", "sport"}; // our terms
    public static final String[][] CITIES = {{"London", "England"}, {"Rome", "Italy"}, {"Corfu", "Greece"}}; // cities - we need also the country in order to find the lat lon
    public static final double PARAMETER_FOR_SIMILARITY_FUNCTION = 0.25; // needed for the similarity calculation
    public static final String[][] HOME_PLACES = {{"Athens", "Greece"}, {"Milan", "Italy"}, {"Kalamata", "Greece"}, {"Paris", "France"}}; // random home places for travellers
    public static final int NUMBER_OF_TRAVELLERS = 5; // max numbers of tested travellers
    public static final String MONGO_URI = "mongodb+srv://dbLord:ecZi6advOXGwSDWN@cluster0.i9pdr.mongodb.net/travelers?retryWrites=true&w=majority";
    public static final String SEPARATOR = " _and_ ";
    public static final String JSON_FILE_NAME = "travelers.json";
    public static final String MONGO_DATABASE_NAME = "travelers";
}
