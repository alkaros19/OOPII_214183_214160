package gr.mycities.recommendation;

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.mycities.recommendation.openWeather.OpenWeatherMap;
import gr.mycities.recommendation.exceptions.NoAcceptedAgeException;
import gr.mycities.recommendation.exceptions.NoDocumentFoundForCityInWikipedia;
import gr.mycities.recommendation.exceptions.NoPlaceFoundInWeatherAPI;
import gr.mycities.recommendation.traveller.Traveler;
import gr.mycities.recommendation.wikipedia.MediaWiki;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MainClass {

    private static final String API_KEY = "fdad7e2bc06fbcbee2745a7428f14d39"; // the key for the weather api
    private static final String[] TERMS = {"sea", "mountain", "museum", "cafe", "restaurant", "music", "theater", "bar", "art", "sport"}; // our terms
    private static final String[][] CITIES = {{"London", "England"}, {"Rome", "Italy"}, {"Corfu", "Greece"}}; // cities - we need also the country in order to find the lat lon
    private static final double PARAMETER_FOR_SIMILARITY_FUNCTION = 0.25; // needed for the similarity calculation
    private static final String[][] HOME_PLACES = {{"Athens", "Greece"}, {"Milam", "Italy"}, {"Kalamata", "Greece"}, {"Paris", "France"}}; // random home places for travellers
    private static final int NUMBER_OF_TRAVELLERS = 5; // max numbers of tested travellers

    public static void main(String[] args) throws IOException, MalformedURLException, NoAcceptedAgeException, NoDocumentFoundForCityInWikipedia, NoPlaceFoundInWeatherAPI {
        // create random travellers
        List<Traveler> travellers = new ArrayList<>();
        for (var i = 0; i < NUMBER_OF_TRAVELLERS; i++) {
            try {
                travellers.add(createRandomTraveller());
            } catch (NoAcceptedAgeException e) {
                // we inform the user and just continue witth the next traveller
                System.out.println(e.getMessage());
            }
        }
        // create cities
        List<City> testCities = createCities();

        // test each traveller with city
        // we use travellers.size instead of max_travellers because in case of exception the max_travellers is wrong as a number
        for (var i = 0; i < travellers.size(); i++) {
            System.out.println("********************");
            System.out.println("Traveller:" + travellers.get(i));
            for (var j = 0; j < testCities.size(); j++) {
                System.out.println("similarity for city:" + testCities.get(j).getPlace().getDescription() + ":" + travellers.get(i).calculate_similarity(testCities.get(j), PARAMETER_FOR_SIMILARITY_FUNCTION));
            }
            System.out.println("********************");
            System.out.println("Best city for traveller:" + travellers.get(i).getName());
            System.out.println(travellers.get(i).compare_cities(testCities, PARAMETER_FOR_SIMILARITY_FUNCTION));
            System.out.println("********************");
            City[] bestCities = travellers.get(i).compare_cities(testCities, 2, PARAMETER_FOR_SIMILARITY_FUNCTION);
            System.out.println("Best cities for traveller:" + travellers.get(i).getName());
            int j = 0;
            for (City city : bestCities) {
                System.out.println(++j + ": " + city);
            }
            System.out.println("********************");
        }

        // free ticket
        System.out.println("FREE TICKETS");
        for (City city : testCities) {
            System.out.println("City:" + city.getPlace().getDescription() + " => " + city.giveFreeTicket(travellers, PARAMETER_FOR_SIMILARITY_FUNCTION));
        }
    }

    // creates a random traveller
    private static Traveler createRandomTraveller() throws MalformedURLException, IOException, NoAcceptedAgeException, NoPlaceFoundInWeatherAPI {
        int age = ThreadLocalRandom.current().nextInt(16, 100); // create random age
        int homePlaceSelector = ThreadLocalRandom.current().nextInt(0, HOME_PLACES.length); // random select a place from the places array
        Place myPlace = new Place(HOME_PLACES[homePlaceSelector][0], HOME_PLACES[homePlaceSelector][1]);
        Traveler traveller = Traveler.createTraveller(age, myPlace);
        traveller.setName(CalculationUtils.generateRandomString()); // random name 
        int i = 0;
        for (String Term : TERMS) { // create the terms for each travelelrs -> random rates
            int rate = ThreadLocalRandom.current().nextInt(1, 10);
            traveller.getTerms().add(new Term(Term, rate));
        }
        ObjectMapper mapper = new ObjectMapper(); // connect to weather api to get lat and lon
        OpenWeatherMap weather_obj = mapper.readValue(new URL("http://api.openweathermap.org/data/2.5/weather?q=" + traveller.getPlace().getDescription() + "," + traveller.getPlace().getCountry() + "&APPID=" + API_KEY + ""), OpenWeatherMap.class);
        if (weather_obj == null) {
            throw new NoPlaceFoundInWeatherAPI(traveller.getPlace().getDescription() + "," + traveller.getPlace().getCountry());
        }
        traveller.getPlace().getGeodesic_vector().add(weather_obj.getCoord().getLat());
        traveller.getPlace().getGeodesic_vector().add(weather_obj.getCoord().getLon());
        return traveller;
    }

    // creates a list of cities based on the cities array on the static fields
    private static List<City> createCities() throws MalformedURLException, IOException, NoDocumentFoundForCityInWikipedia, NoPlaceFoundInWeatherAPI {
        ObjectMapper mapper = new ObjectMapper();
        List<City> testCities = new ArrayList<>();
        System.out.println("********************");
        System.out.println("Created cities");
        // create each city based on our cities array
        for (String[] cityData : CITIES) {
            City city = new City(new Place(cityData[0], cityData[1]));
            // connect to media wiki to get the city text           
            MediaWiki mediaWiki_obj = mapper.readValue(new URL("https://en.wikipedia.org/w/api.php?action=query&prop=extracts&titles=" + city.getPlace().getDescription() + "&format=json&formatversion=2"), MediaWiki.class);
            String cityText = mediaWiki_obj.getQuery().getPages().get(0).getExtract();
            if (cityText == null) {
                throw new NoDocumentFoundForCityInWikipedia(city.getPlace().getDescription() + " " + city.getPlace().getCountry());
            }
            // search in the city text for each of our term
            for (String TERM : TERMS) {
                int value = CalculationUtils.countCriterionfCity(cityText, TERM);
                city.getTerms_vector().add(new Term(TERM, value));
            }
            // connect to weather api to get lat and lon
            OpenWeatherMap weather_obj = mapper.readValue(new URL("http://api.openweathermap.org/data/2.5/weather?q=" + city.getPlace().getDescription() + "," + city.getPlace().getCountry() + "&APPID=" + API_KEY + ""), OpenWeatherMap.class);
            if (weather_obj == null) {
                throw new NoPlaceFoundInWeatherAPI(city.getPlace().getDescription() + "," + city.getPlace().getCountry());
            }
            city.getPlace().getGeodesic_vector().add(weather_obj.getCoord().getLat());
            city.getPlace().getGeodesic_vector().add(weather_obj.getCoord().getLon());
            // just a print to test the city created 
            System.out.println(city);
            testCities.add(city);
        }
        return testCities;
    }

}
