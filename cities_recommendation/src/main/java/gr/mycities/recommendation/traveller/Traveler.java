package gr.mycities.recommendation.traveller;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import gr.mycities.recommendation.exceptions.NoAcceptedAgeException;
import gr.mycities.recommendation.CalculationUtils;
import static gr.mycities.recommendation.MyConstants.SEPARATOR;
import gr.mycities.recommendation.json.TravelerDeserializer;
import gr.mycities.recommendation.json.TravelerSerializer;
import gr.mycities.recommendation.models.City;
import gr.mycities.recommendation.models.Place;
import gr.mycities.recommendation.models.Term;
import java.util.List;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;
import java.util.stream.Collectors;

@JsonSerialize(keyUsing = TravelerSerializer.class)
@JsonDeserialize(keyUsing = TravelerDeserializer.class)
public abstract class Traveler {

    private Vector<Term> terms = new Vector<Term>(10); // terms
    private int age; // age of the traveller
    private Place place; // the place
    private String name; // just a name to distinct travellers when we test

    // finds the most suitable city for the traveller
    public City compare_cities(List cities, double p) {
        return compare_cities(cities, 1, p)[0];
    }

    // finds the most suitable cities for the traveller -> number is given in the second argument of how many cities
    public City[] compare_cities(List cities, int size, double p) {
        // we sort the list based on the similarity with the traveller - 
        // we use a sorted set - only distinct values and we pass a comparator, because we want a custom sort
        SortedSet<City> sortedCities = new TreeSet<>((City c1, City c2) -> {
            double result = calculate_similarity(c1, p) - calculate_similarity(c2, p);
            return (result >= 0) ? -1 : 1;
        });
        sortedCities.addAll(cities);
        // make the set to an array
        return sortedCities.stream().limit(size).collect(Collectors.toList()).toArray(City[]::new);
    }

    // we did not make this method abstract, because it's body is the same for all chlidren
    public double calculate_similarity(City city, double p) {
//        MyTravellers.addReccomendation(this, new Reccomendation(city));
        return p * similarity_terms_vector(city) + (1 - p) * CalculationUtils.similarity_geodesic_vector(this, city);
    }

    // instead we made this method abstract and with polymporphism java will select the apropriate one
    public abstract double similarity_terms_vector(City city);

    // method to create a travellers bawsed on age an the place
    public static Traveler createTraveller(int age, Place place) throws NoAcceptedAgeException {
        if (age >= 16 && age <= 25) {
            return new YoungTraveler(age, place);
        } else if (age >= 26 && age <= 60) {
            return new MiddleTraveler(age, place);
        } else if (age > 60) {
            return new ElderTraveller(age, place);
        } else {
            // we don't accept an age smaller than 16
            throw new NoAcceptedAgeException("The given age is not greater than 15");
        }
    }

    // helps to find the term rate in computations
    public double getTermRate(int i) {
        return terms.get(i).getRate();
    }

    public void setTermRate(int position, int value) {
        terms.get(position).setRate(value);
    }
    public Vector getTerms() {
        return terms;
    }

    public void setTerms(Vector terms) {
        this.terms = terms;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // jackson uses this method for the serialization for the json
    @Override
    @JsonValue
    public String toString() {
        return this.getClass().getName() + SEPARATOR + name + SEPARATOR + age + " TERMS " + terms + " " + place;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.terms);
        hash = 59 * hash + this.age;
        hash = 59 * hash + Objects.hashCode(this.place);
        hash = 59 * hash + Objects.hashCode(this.name);
        return hash;
    }

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
        final Traveler other = (Traveler) obj;
        if (this.age != other.age) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.terms, other.terms)) {
            return false;
        }
        if (!Objects.equals(this.place, other.place)) {
            return false;
        }
        return true;
    }

}
