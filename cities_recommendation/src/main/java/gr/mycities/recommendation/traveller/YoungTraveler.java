
package gr.mycities.recommendation.traveller;

import gr.mycities.recommendation.City;
import gr.mycities.recommendation.Place;

public class YoungTraveler extends Traveler {
    
    // visibility only in the package - we cannot instantiate the object with the constructor outside the package
    // we will use the static method createTraveller from the abstract class
    YoungTraveler(int age, Place place) {
        setAge(age);
        setPlace(place);
    }
    
    // based on Euclidean distance
    @Override
    public double similarity_terms_vector(City city) {
        double sum = 0.0;
        for(int i = 0; i < getTerms().size(); i++) {
            double sub = getTermRate(i) - city.getTermRate(i);
            sum += sub * sub;
        }
        return 1/(1 + Math.sqrt(sum));
    }

}
