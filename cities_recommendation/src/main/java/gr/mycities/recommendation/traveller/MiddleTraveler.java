package gr.mycities.recommendation.traveller;

import gr.mycities.recommendation.CalculationUtils;
import gr.mycities.recommendation.models.City;
import gr.mycities.recommendation.models.Place;

public class MiddleTraveler extends Traveler {

    // jackson needs the defauls constructor
    public MiddleTraveler() {
    }
    
    // check YoungTraveller comment
    MiddleTraveler(int age, Place place) {
        setAge(age);
        setPlace(place);
    }

    // calculation based on cosine similarity
    @Override
    public double similarity_terms_vector(City city) {
        return CalculationUtils.cosineSimilarity(getTerms(), city.getTerms_vector());
    }   
}
