
package gr.mycities.recommendation.models;

import java.util.Date;
import java.util.Objects;

// holda the city reccomended with the timestamp when the reccomendation happened
public class Reccomendation {
    private City visit;
    private final Date when;

    public Reccomendation() {
        this.when = new Date();
    }

    public Reccomendation(City visit) {
        this.when = new Date();
        this.visit = visit;
    }

    public City getVisit() {
        return visit;
    }

    public void setVisit(City visit) {
        this.visit = visit;
    }

    public Date getWhen() {
        return when;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + Objects.hashCode(this.visit);
        hash = 19 * hash + Objects.hashCode(this.when);
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
        final Reccomendation other = (Reccomendation) obj;
        if (!Objects.equals(this.visit, other.visit)) {
            return false;
        }
        if (!Objects.equals(this.when, other.when)) {
            return false;
        }
        return true;
    }

    
}
