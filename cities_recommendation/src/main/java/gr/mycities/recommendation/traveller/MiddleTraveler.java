package gr.mycities.recommendation.traveller;

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
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < getTerms().size(); i++) {
            double travellerRate = getTermRate(i);
            double cityRate = city.getTermRate(i);
            dotProduct += travellerRate * cityRate;
            normA += travellerRate * travellerRate;
            normB += cityRate * cityRate;
        }
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

}
