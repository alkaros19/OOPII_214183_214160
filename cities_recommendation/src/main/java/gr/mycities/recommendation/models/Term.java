
package gr.mycities.recommendation.models;

import static gr.mycities.recommendation.MyConstants.SEPARATOR;
import java.util.Objects;

/**
 * we use a Term object that holds the description and the rate
 */
public class Term {
    private String description; // the name of the term
    private int rate; // the value

    public Term() {
    }

    public Term(String term, int rate) {
        this.description = term;
        this.rate = rate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.description);
        hash = 83 * hash + this.rate;
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
        final Term other = (Term) obj;
        if (this.rate != other.rate) {
            return false;
        }
        if (!Objects.equals(this.description, other.description)) {
            return false;
        }
        return true;
    }

    
    @Override
    // need to override for jackson
    public String toString() {
        return description + SEPARATOR + rate;
    }
    
    
}
