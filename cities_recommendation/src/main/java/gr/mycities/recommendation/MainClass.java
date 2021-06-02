package gr.mycities.recommendation;

import com.fasterxml.jackson.core.type.TypeReference;
import gr.mycities.recommendation.models.Place;
import gr.mycities.recommendation.models.City;
import gr.mycities.recommendation.models.Term;
import com.fasterxml.jackson.databind.ObjectMapper;
import gr.mycities.recommendation.openWeather.OpenWeatherMap;
import gr.mycities.recommendation.exceptions.NoAcceptedAgeException;
import gr.mycities.recommendation.exceptions.NoDocumentFoundForCityInWikipedia;
import gr.mycities.recommendation.exceptions.NoPlaceFoundInWeatherAPI;
import gr.mycities.recommendation.gui.MainWindow;
import gr.mycities.recommendation.models.Reccomendation;
import gr.mycities.recommendation.traveller.Traveler;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
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
            map.entrySet().forEach(entry -> {
                MyTravellers.addReccomendation(entry.getKey(), entry.getValue());
            });
        } catch (IOException e) {
            Logger.getLogger(MainClass.class.getName()).log(Level.SEVERE, null, e);
        }
        String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        String appConfigPath = rootPath + "terms.properties";
        Properties appProps = new Properties();
        appProps.load(new FileInputStream(appConfigPath));
        MyTerms.terms.addAll(Arrays.asList(appProps.getProperty("terms").split(",")));
        MyCities.init();

        MainWindow mainWindow = new MainWindow();
        mainWindow.createWindow();
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
