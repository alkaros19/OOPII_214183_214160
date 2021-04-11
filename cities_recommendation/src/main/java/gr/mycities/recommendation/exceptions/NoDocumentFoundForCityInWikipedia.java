package gr.mycities.recommendation.exceptions;

public class NoDocumentFoundForCityInWikipedia extends Exception {

    /**
     * Creates a new instance of <code>NoDocumentFoundForCityInWikipedia</code>
     * without detail message.
     */
    public NoDocumentFoundForCityInWikipedia() {
    }

    /**
     * Constructs an instance of <code>NoDocumentFoundForCityInWikipedia</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public NoDocumentFoundForCityInWikipedia(String msg) {
        super(msg);
    }
}
