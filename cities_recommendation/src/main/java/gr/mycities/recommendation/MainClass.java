package gr.mycities.recommendation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import gr.mycities.recommendation.models.Place;
import gr.mycities.recommendation.models.City;
import gr.mycities.recommendation.models.Term;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import gr.mycities.recommendation.openWeather.OpenWeatherMap;
import gr.mycities.recommendation.exceptions.NoAcceptedAgeException;
import gr.mycities.recommendation.exceptions.NoDocumentFoundForCityInWikipedia;
import gr.mycities.recommendation.exceptions.NoPlaceFoundInWeatherAPI;
import gr.mycities.recommendation.models.Reccomendation;
import gr.mycities.recommendation.mongo.MongoDb;
import gr.mycities.recommendation.traveller.Traveler;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainClass {

    public static void main(String[] args) throws IOException, MalformedURLException, NoAcceptedAgeException, NoDocumentFoundForCityInWikipedia, NoPlaceFoundInWeatherAPI {
        // read from json file to load travelers data
        // create object mapper instance
        ObjectMapper mapper = new ObjectMapper();
        // convert JSON file to map
        TypeReference<HashMap<Traveler, Reccomendation>> typeRef
                = new TypeReference<HashMap<Traveler, Reccomendation>>() {
        };
        try {
            Map<Traveler, Reccomendation> map = mapper.readValue(Paths.get(MyConstants.JSON_FILE_NAME).toFile(), typeRef);
            // print map entries
            for (Map.Entry<Traveler, Reccomendation> entry : map.entrySet()) {
                System.out.println(entry.getKey() + "=" + entry.getValue());
                MyTravellers.addReccomendation(entry.getKey(), entry.getValue());
            }
        } catch (IOException e) {
            Logger.getLogger(MainClass.class.getName()).log(Level.SEVERE, null, e);
        }

        // initialize cities hashmap
        MyCities.init();
        System.out.println("my cities size " + MyCities.getCities().size());
        // create random travellers
        List<Traveler> travellers = new ArrayList<>();
        for (var i = 0; i < MyConstants.NUMBER_OF_TRAVELLERS; i++) {
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
                System.out.println("similarity for city:" + testCities.get(j).getPlace().getDescription() + ":" + travellers.get(i).calculate_similarity(testCities.get(j), MyConstants.PARAMETER_FOR_SIMILARITY_FUNCTION));
            }
            System.out.println("********************");
            System.out.println("Best city for traveller:" + travellers.get(i).getName());
            System.out.println(travellers.get(i).compare_cities(testCities, MyConstants.PARAMETER_FOR_SIMILARITY_FUNCTION));
            System.out.println("********************");
            City[] bestCities = travellers.get(i).compare_cities(testCities, 2, MyConstants.PARAMETER_FOR_SIMILARITY_FUNCTION);
            System.out.println("Best cities for traveller:" + travellers.get(i).getName());
            int j = 0;
            for (City city : bestCities) {
                System.out.println(++j + ": " + city);
            }
            System.out.println("********************");
        }

        // free ticket
        System.out.println("FREE TICKETS");
        testCities.forEach(city -> {
            System.out.println("City:" + city.getPlace().getDescription() + " => " + city.giveFreeTicket(travellers, MyConstants.PARAMETER_FOR_SIMILARITY_FUNCTION));
        });

        // test the same traveler - create the same traveler - should be only once in the json file
        Traveler same = createSpecificTraveler();
        same.compare_cities(testCities, MyConstants.PARAMETER_FOR_SIMILARITY_FUNCTION);
        MyTravellers.getTravelers().forEach((t, r) -> {
            if (t.getName().equals("test")) {
                System.out.println("************");
                System.out.println("Should be printed only once, but the time should be difference - the last one is kept");
                System.out.println(t);
                System.out.println(r.getWhen());
                System.out.println("************");
            }
        });

        // test same traveler with different criteria
        Traveler trWith2Reccomendations = createSpecificTraveler();
        trWith2Reccomendations.setName("2Reccomendations");
        trWith2Reccomendations.compare_cities(testCities, MyConstants.PARAMETER_FOR_SIMILARITY_FUNCTION);
        Traveler trWith2Reccomendations2 = createSpecificTraveler();
        trWith2Reccomendations2.setName("2Reccomendations");
        //changing a term rate -> new search with different criteria
        trWith2Reccomendations2.setTermRate(0, 4);
        trWith2Reccomendations2.compare_cities(testCities, MyConstants.PARAMETER_FOR_SIMILARITY_FUNCTION);
        System.out.println("************");
        System.out.println("Should be printed two times, but  once in sorted Travelers. Same reccomendation, because changed just one term");
        MyTravellers.getTravelers().forEach((t, r) -> {
            if (t.getName().equals("2Reccomendations")) {
                System.out.println(t.getName() + " " + r.getVisit() + "" + r.getWhen());
            }
        });
        System.out.println("************");
        System.out.println(MyTravellers.getTravelers().size());
        // testing sort travelers
        System.out.println("******************************");
        System.out.println("Sorted Travelers");
        MyTravellers.getSortedTravelers().forEach((t) -> System.out.println(t.getReccomendation().getWhen() + " !" + t.getTraveler().getName()+"!"));
        System.out.println("******************************");

        // write to json file
        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
        Map<Traveler, Reccomendation> travelers = new HashMap<>();
        travelers.put(travellers.get(0), MyTravellers.getTravelers().values().stream().findFirst().get());
        writer.writeValue(Paths.get(MyConstants.JSON_FILE_NAME).toFile(), MyTravellers.getTravelers());
        
        // close mongo db connection
        MongoDb.getMongoDbClient().close();
    }

    // for testing reasons - no duplicate are allowed in our hashmap travelers
    private static Traveler createSpecificTraveler() throws NoAcceptedAgeException, NoDocumentFoundForCityInWikipedia, IOException, MalformedURLException, NoPlaceFoundInWeatherAPI {
        Place place = MyCities.getCity(new Place(MyConstants.HOME_PLACES[0][0], MyConstants.HOME_PLACES[0][1])).getPlace();
        Traveler traveler = Traveler.createTraveller(25, place);
        traveler.setName("test");
        for (String Term : MyConstants.TERMS) { // create the terms for each travelelrs -> random rates
            int rate = 5;
            traveler.getTerms().add(new Term(Term, rate));
        }
        return traveler;
    }

    // creates a random traveller
    private static Traveler createRandomTraveller() throws MalformedURLException, IOException, NoAcceptedAgeException, NoPlaceFoundInWeatherAPI {
        int age = ThreadLocalRandom.current().nextInt(16, 100); // create random age
        int homePlaceSelector = ThreadLocalRandom.current().nextInt(0, MyConstants.HOME_PLACES.length); // random select a place from the places array
        Place myPlace = new Place(MyConstants.HOME_PLACES[homePlaceSelector][0], MyConstants.HOME_PLACES[homePlaceSelector][1]);
        Traveler traveller = Traveler.createTraveller(age, myPlace);
        traveller.setName(CalculationUtils.generateRandomString()); // random name 
        for (String Term : MyConstants.TERMS) { // create the terms for each travelelrs -> random rates
            int rate = ThreadLocalRandom.current().nextInt(1, 10);
            traveller.getTerms().add(new Term(Term, rate));
        }
        ObjectMapper mapper = new ObjectMapper(); // connect to weather api to get lat and lon
        OpenWeatherMap weather_obj = mapper.readValue(new URL("http://api.openweathermap.org/data/2.5/weather?q=" + traveller.getPlace().getDescription() + "," + traveller.getPlace().getCountry() + "&APPID=" + MyConstants.API_KEY + ""), OpenWeatherMap.class);
        if (weather_obj == null) {
            throw new NoPlaceFoundInWeatherAPI(traveller.getPlace().getDescription() + "," + traveller.getPlace().getCountry());
        }
        traveller.getPlace().getGeodesic_vector().add(weather_obj.getCoord().getLat());
        traveller.getPlace().getGeodesic_vector().add(weather_obj.getCoord().getLon());
        return traveller;
    }

    // creates a list of cities based on the cities array on the static fields
    private static List<City> createCities() throws MalformedURLException, IOException, NoDocumentFoundForCityInWikipedia, NoPlaceFoundInWeatherAPI {
        System.out.println("********************");
        System.out.println("Created cities");
        // create each city based on our cities array
        for (String[] cityData : MyConstants.CITIES) {
            MyCities.getCity(new Place(cityData[0], cityData[1]));
        }
        return MyCities.getCities();
    }

}
