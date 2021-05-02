package gr.mycities.recommendation.models;

import gr.mycities.recommendation.traveller.Traveler;
import java.util.Objects;

// makes a pair object with the traveler and the reccomendation - we use it for
// the sortable collection based on timestamp of search. We don't weant in this 
// distinct travelers based on their name, so we override equals based on their
// name.
public class TravelerWithRecommendation implements Comparable<TravelerWithRecommendation> {

    private Traveler traveler;
    private Reccomendation reccomendation;

    public TravelerWithRecommendation() {
    }

    public TravelerWithRecommendation(Traveler traveler, Reccomendation reccomendation) {
        this.traveler = traveler;
        this.reccomendation = reccomendation;
    }

    public Traveler getTraveler() {
        return traveler;
    }

    public void setTraveler(Traveler traveler) {
        this.traveler = traveler;
    }

    public Reccomendation getReccomendation() {
        return reccomendation;
    }

    public void setReccomendation(Reccomendation reccomendation) {
        this.reccomendation = reccomendation;
    }

    // we override hashCode and equals so that objects are distinct only by name
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.traveler.getName());
        return hash;
    }

    // we override hashCode and equals so that objects are distinct only by name
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
        final TravelerWithRecommendation other = (TravelerWithRecommendation) obj;
        if (!Objects.equals(this.traveler.getName(), other.traveler.getName())) {
            return false;
        }
        return true;
    }

    @Override
    // sorting by the timestamp when the search was done
    public int compareTo(TravelerWithRecommendation travelerWithRecommendation) {
        return travelerWithRecommendation.getReccomendation().getWhen().compareTo(this.getReccomendation().getWhen());
    }

}
