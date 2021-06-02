package gr.mycities.recommendation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import gr.mycities.recommendation.exceptions.NoDocumentFoundForCityInWikipedia;
import gr.mycities.recommendation.exceptions.NoPlaceFoundInWeatherAPI;
import gr.mycities.recommendation.models.City;
import gr.mycities.recommendation.models.Place;
import gr.mycities.recommendation.models.Term;
import gr.mycities.recommendation.mongo.MongoDb;
import gr.mycities.recommendation.openWeather.OpenWeatherMap;
import gr.mycities.recommendation.wikipedia.MediaWiki;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bson.Document;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

public class MyCities {

    // holds all the cities in memory
    private static final Map<Place, City> cities = new HashMap<>();

    public static City getCity(Place place) throws NoDocumentFoundForCityInWikipedia, IOException, MalformedURLException, NoPlaceFoundInWeatherAPI {
        // get the city from the hashmap
        City city = cities.get(place);
        // if not exists, create it, put it database and in hashmap
        if (city == null) {
            city = createCity(place.getDescription(), place.getCountry());
            // we use the morphia odm for mongo db
            Morphia morphia = new Morphia();
            Datastore datastore = morphia.createDatastore(MongoDb.getMongoDbClient(), MyConstants.MONGO_DATABASE_NAME);
            datastore.ensureIndexes();
            datastore.save(city);
            cities.put(place, city);
            MongoDb.closeConnection();
        }
        return city;
    }

    public static void updateCity(City city) {
        Morphia morphia = new Morphia();
        Datastore datastore = morphia.createDatastore(MongoDb.getMongoDbClient(), MyConstants.MONGO_DATABASE_NAME);
        datastore.ensureIndexes();
        UpdateOperations ops = datastore
                .createUpdateOperations(City.class)
                .set("terms_vector", city.getTerms_vector());
        Query<City> updateQuery = datastore.createQuery(City.class).field("place.description").equal(city.getPlace().getDescription());
        datastore.update(updateQuery, ops, false);
        MongoDb.closeConnection();
    }

    public static void deleteCity(City city) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                MongoCollection<Document> collection = MongoDb.getMongoDbClient().getDatabase(MyConstants.MONGO_DATABASE_NAME).getCollection("cities");
                collection.deleteOne(Filters.eq("place.description", city.getPlace().getDescription()));
                MongoDb.closeConnection();
                System.out.println(cities.containsKey(city.getPlace()));
                cities.remove(city.getPlace());
            }
        };
        Thread t = new Thread(r);
        t.start();
    }
//    public static void saveCity(City city) {
//        System.out.println("try to save" + city);
//            // we use the morphia odm for mongo db
//            Morphia morphia = new Morphia();
//            Datastore datastore = morphia.createDatastore(MongoDb.getMongoDbClient(), MyConstants.MONGO_DATABASE_NAME);
//            datastore.ensureIndexes();
//            datastore.save(city);
//    }

    // finds the city details in the wikipedia and openweathermap
    private static City createCity(String cityName, String countryName) throws NoDocumentFoundForCityInWikipedia, MalformedURLException, IOException, NoPlaceFoundInWeatherAPI {
        City city = new City(new Place(cityName, countryName));
        // connect to media wiki to get the city text           
        Thread tTerms = setTerms(city);
        Thread tGeodesic = setGeodesic(new ObjectMapper(), city.getPlace());
        try {
            tTerms.join();
            tGeodesic.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(MyCities.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        // just a print to test the city created 
        System.out.println(city);
        return city;
    }

    public static Thread setTerms(City city) throws NoDocumentFoundForCityInWikipedia, MalformedURLException, IOException {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    MediaWiki mediaWiki_obj = mapper.readValue(new URL("https://en.wikipedia.org/w/api.php?action=query&prop=extracts&titles=" + city.getPlace().getDescription() + "&format=json&formatversion=2"), MediaWiki.class
                    );
                    String cityText = mediaWiki_obj.getQuery().getPages().get(0).getExtract();
                    if (cityText == null) {
                        throw new NoDocumentFoundForCityInWikipedia(city.getPlace().getDescription() + " " + city.getPlace().getCountry());
                    }
                    // search in the city text for each of our term
                    int i = 0;
                    for (String TERM : MyTerms.terms) {
                        int value = CalculationUtils.countCriterionfCity(cityText, TERM);
                        try {
                            city.getTerms_vector().set(i, new Term(TERM, value));
                        } catch (ArrayIndexOutOfBoundsException ex) {
                            city.getTerms_vector().add(i, new Term(TERM, value));
                        }
                        i++;
                    }
                    System.out.println("FINISHED TERMS");
                } catch (MalformedURLException ex) {
                    Logger.getLogger(MyCities.class
                            .getName()).log(Level.SEVERE, null, ex);
                } catch (IOException | NoDocumentFoundForCityInWikipedia ex) {
                    Logger.getLogger(MyCities.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        Thread t = new Thread(r);
        t.start();
        System.out.println("*started terms******************************************");
        return t;
    }

    public static Thread setGeodesic(ObjectMapper mapper, Place place) throws NoPlaceFoundInWeatherAPI, MalformedURLException, IOException {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    // connect to weather api to get lat and lon
                    OpenWeatherMap weather_obj = mapper.readValue(new URL("http://api.openweathermap.org/data/2.5/weather?q=" + place.getDescription() + "," + place.getCountry() + "&APPID=" + MyConstants.API_KEY + ""), OpenWeatherMap.class
                    );
                    if (weather_obj == null) {
                        try {
                            throw new NoPlaceFoundInWeatherAPI(place.getDescription() + "," + place.getCountry());
                        } catch (NoPlaceFoundInWeatherAPI ex) {
                            Logger.getLogger(MyCities.class
                                    .getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    place.getGeodesic_vector().add(weather_obj.getCoord().getLat());
                    place.getGeodesic_vector().add(weather_obj.getCoord().getLon());
                    System.out.println("FINISHED GEODESIC");
                } catch (MalformedURLException ex) {
                    Logger.getLogger(MyCities.class
                            .getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(MyCities.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        Thread t = new Thread(r);
        t.start();
        System.out.println("*started geodesic******************************************");
        return t;
    }

    // return hashmap values as a list
    public static List<City> getCities() {
        return new ArrayList<>(cities.values());
    }

    // initialize hashmap from database
    public static void init() {
        Morphia morphia = new Morphia();
        Datastore datastore = morphia.createDatastore(MongoDb.getMongoDbClient(), MyConstants.MONGO_DATABASE_NAME);
        datastore.ensureIndexes();
        datastore.find(City.class
        ).asList().forEach(city -> {
            MyCities.cities.put(city.getPlace(), city);
        });
        MongoDb.closeConnection();
    }
}
