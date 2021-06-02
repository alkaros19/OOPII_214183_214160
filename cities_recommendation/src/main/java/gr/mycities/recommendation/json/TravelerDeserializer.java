package gr.mycities.recommendation.json;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import static gr.mycities.recommendation.MyConstants.SEPARATOR;
import gr.mycities.recommendation.models.Place;
import gr.mycities.recommendation.models.Term;
import gr.mycities.recommendation.traveller.Traveler;
import java.lang.reflect.InvocationTargetException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

// this class is used to read the traveller object from json file. 
// Because we used a map, jackson puts the map key object as String in the json 
// object. We have to deserialize this String and create on our own the java object
public class TravelerDeserializer extends KeyDeserializer {

    @Override
    public Object deserializeKey(String value, DeserializationContext dc) {
        String[] values = value.split("TERMS")[0].split(SEPARATOR);
        String className = values[0].substring(1).strip();
        Traveler tr = null;
        Class clazz;
        try {
            clazz = Class.forName(className);
            tr = (Traveler) clazz.getDeclaredConstructor().newInstance();
            tr.setName(values[1].strip());
            tr.setAge(Integer.parseInt(values[2].strip()));
            String[] terms = null;
            try {
                terms = value.split("TERMS")[1].split("Place")[0].split(",");
                terms[0] = terms[0].strip().substring(1);
                int lastPosition = terms.length - 1;
                terms[lastPosition] = terms[lastPosition].strip();
                terms[lastPosition] = terms[lastPosition].substring(0, terms[lastPosition].strip().length() - 1);
                Vector myTerms = new Vector();
                for (var i = 0; i < terms.length; i++) {
                    String[] v = terms[i].split(SEPARATOR);
                    myTerms.add(new Term(v[0].strip(), Integer.parseInt(v[1].strip())));
                }
                tr.setTerms(myTerms);
            } catch (Exception e) {

            }
            String[] geodesic_vector = value.split("Place")[1].split("geodesic_vector=\\[")[1].split("\\]")[0].split(",");
            Vector<Double> gv = new Vector();
            gv.add(Double.parseDouble(geodesic_vector[0].strip()));
            gv.add(Double.parseDouble(geodesic_vector[1].strip()));
            String description = value.split("description=")[1].split(",")[0].strip();
            String country = value.split("country=")[1].strip();
            country = country.substring(0, country.length() - 2);
            Place place = new Place(description, country);
            place.setGeodesic_vector(gv);
            tr.setPlace(place);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(TravelerDeserializer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return tr;
    }

}
