package gr.mycities.recommendation.exceptions;

public class NoPlaceFoundInWeatherAPI extends Exception {

    /**
     * Creates a new instance of <code>NoPlaceFoundInWeatherAPI</code> without
     * detail message.
     */
    public NoPlaceFoundInWeatherAPI() {
    }

    /**
     * Constructs an instance of <code>NoPlaceFoundInWeatherAPI</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public NoPlaceFoundInWeatherAPI(String msg) {
        super(msg);
    }
}
