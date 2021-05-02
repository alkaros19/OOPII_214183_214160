package gr.mycities.recommendation.traveller;

import gr.mycities.recommendation.models.City;
import gr.mycities.recommendation.models.Place;
import gr.mycities.recommendation.models.Term;
import java.util.HashSet;
import java.util.Set;

public class ElderTraveller extends Traveler {

    // jackson needs the defauls constructor
    public ElderTraveller() {
    }
    
// check YoungTraveller comment
    ElderTraveller(int age, Place place) {
        setAge(age);
        setPlace(place);
    }

    // based on Jaccard distance
    @Override
    public double similarity_terms_vector(City city) {
        Set<String> travellerTerms = new HashSet<>();
        Set<String> cityTerms = new HashSet<>();
        for (int i = 0; i < getTerms().size(); i++) {
            if (getTermRate(i) > 0) {
                travellerTerms.add(((Term) getTerms().get(i)).getDescription());
            }
            if (city.getTermRate(i) > 0) {
                cityTerms.add(((Term)city.getTerms_vector().get(i)).getDescription());
            }
        }
        final int sa = travellerTerms.size();
        final int sb = cityTerms.size();
        travellerTerms.retainAll(cityTerms);
        final int intersection = travellerTerms.size();
        return 1.0 / ((sa + sb - intersection) * intersection);
    }

}
