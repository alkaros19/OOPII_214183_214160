package gr.mycities.recommendation;

import gr.mycities.recommendation.traveller.Traveler;
import java.util.List;
import java.util.Objects;
import java.util.Vector;

/**
 * the city of travel destination
*/
public class City {
    private Vector<Term> terms_vector; // the array of terms
    private Place place; // the place object

    public City(Place place) {
        this.terms_vector = new Vector<Term>(10);
        this.place = place;
    }

    // gives free tickets to more similar travellers from the list of travellers
    public Traveler giveFreeTicket(List<Traveler> travellers, double p) {
        int chosen = 0;
        double best = 0.0;
        for(int i = 0; i < travellers.size(); i++) {
            double current = travellers.get(i).calculate_similarity(this, p);
            if( current > best) {
                chosen = i;
                best = current;
            }
        }
        return travellers.get(chosen);
    }
    public int getTermRate(int i) {
        return terms_vector.get(i).getRate();
    }
    
    public Vector getTerms_vector() {
        return terms_vector;
    }

    public void setTerms_vector(Vector terms_vector) {
        this.terms_vector = terms_vector;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }
    
    // we override this method to have a better representation when we print the object
    @Override
    public String toString() {
        String terms = terms_vector.stream().map((t) -> t.getDescription() + ":" + t.getRate()).reduce("", (t, a) -> t + "," + a);
        return "City {" + place.getDescription() + " : terms_vector=" + terms + ", lat=" + place.getGeodesic_vector().get(0) + ", lon=" + place.getGeodesic_vector().get(1) + "}";
    }

    // we need it for treeset
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + Objects.hashCode(this.place);
        return hash;
    }

    // we need it for treeset
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final City other = (City) obj;
        if (!Objects.equals(this.place, other.place)) {
            return false;
        }
        return true;
    }
    
    
}
