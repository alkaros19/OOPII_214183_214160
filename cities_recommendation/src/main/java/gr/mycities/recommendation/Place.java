package gr.mycities.recommendation;

import java.util.Objects;
import java.util.Vector;

/**
 * the place object - we use it for city and traveler
*/
public class Place {

    private Vector<Double> geodesic_vector;
    private String description;
    private String country;

    public Place(String description, String country) {
        this.geodesic_vector = new Vector<>(2);
        this.description = description;
        this.country = country;
    }

    
    public Vector<Double> getGeodesic_vector() {
        return geodesic_vector;
    }

    public void setGeodesic_vector(Vector<Double> geodesic_vector) {
        this.geodesic_vector = geodesic_vector;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    // we need for treeset
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.description);
        hash = 53 * hash + Objects.hashCode(this.country);
        return hash;
    }

    // we need for treeset
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
        final Place other = (Place) obj;
        if (!Objects.equals(this.description, other.description)) {
            return false;
        }
        if (!Objects.equals(this.country, other.country)) {
            return false;
        }
        return true;
    }
    
    
}
