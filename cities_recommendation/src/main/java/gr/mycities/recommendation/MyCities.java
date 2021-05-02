package gr.mycities.recommendation;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

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
        }
        return city;
    }

    // finds the city details in the wikipedia and openweathermap
    private static City createCity(String cityName, String countryName) throws NoDocumentFoundForCityInWikipedia, MalformedURLException, IOException, NoPlaceFoundInWeatherAPI {
        City city = new City(new Place(cityName, countryName));
        // connect to media wiki to get the city text           
        ObjectMapper mapper = new ObjectMapper();
        MediaWiki mediaWiki_obj = mapper.readValue(new URL("https://en.wikipedia.org/w/api.php?action=query&prop=extracts&titles=" + city.getPlace().getDescription() + "&format=json&formatversion=2"), MediaWiki.class);
        String cityText = mediaWiki_obj.getQuery().getPages().get(0).getExtract();
        if (cityText == null) {
            throw new NoDocumentFoundForCityInWikipedia(city.getPlace().getDescription() + " " + city.getPlace().getCountry());
        }
        // search in the city text for each of our term
        for (String TERM : MyConstants.TERMS) {
            int value = CalculationUtils.countCriterionfCity(cityText, TERM);
            city.getTerms_vector().add(new Term(TERM, value));
        }
        // connect to weather api to get lat and lon
        OpenWeatherMap weather_obj = mapper.readValue(new URL("http://api.openweathermap.org/data/2.5/weather?q=" + city.getPlace().getDescription() + "," + city.getPlace().getCountry() + "&APPID=" + MyConstants.API_KEY + ""), OpenWeatherMap.class);
        if (weather_obj == null) {
            throw new NoPlaceFoundInWeatherAPI(city.getPlace().getDescription() + "," + city.getPlace().getCountry());
        }
        city.getPlace().getGeodesic_vector().add(weather_obj.getCoord().getLat());
        city.getPlace().getGeodesic_vector().add(weather_obj.getCoord().getLon());
        // just a print to test the city created 
        System.out.println(city);
        return city;
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
        datastore.find(City.class).asList().forEach(city -> {
            MyCities.cities.put(city.getPlace(), city);
        });
    }
}
