
package gr.mycities.recommendation;

/**
 * we use a Term object that holds the description and the rate
 */
public class Term {
    private String description; // the name of the term
    private int rate; // the value

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
    
}
