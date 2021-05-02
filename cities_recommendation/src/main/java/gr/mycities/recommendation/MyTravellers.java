
package gr.mycities.recommendation;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.MapSerializer;
import gr.mycities.recommendation.models.Reccomendation;
import gr.mycities.recommendation.models.TravelerWithRecommendation;
import gr.mycities.recommendation.traveller.Traveler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

// keeps the map of travellers and reccomendations in memory - use jackson to save them
public class MyTravellers {
   
    @JsonSerialize(keyUsing = MapSerializer.class)
    private static final Map<Traveler, Reccomendation> travelers = new HashMap<>();
    
    // adds to the hashmap
    public static void addReccomendation(Traveler traveler, Reccomendation reccomendation) {
        travelers.put(traveler, reccomendation);
    }
    
    // create a sorted set from hashmap with distinct travelers based on their name
    public static Set<TravelerWithRecommendation> getSortedTravelers() {
        List<TravelerWithRecommendation> tr = new ArrayList<>();
        Iterator it = travelers.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry<Traveler, Reccomendation> pair = (Map.Entry)it.next();
            tr.add(new TravelerWithRecommendation(pair.getKey(), pair.getValue()));
        }
        // we sort the list
        Collections.sort(tr);
        // we use a linkedHashSet to retain order and no duplicates
        return new LinkedHashSet<>(tr); 
    }

    // returns the hashmap
    public static Map<Traveler, Reccomendation> getTravelers() {
        return travelers;
    }
    
}
