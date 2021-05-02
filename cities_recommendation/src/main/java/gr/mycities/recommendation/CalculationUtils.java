package gr.mycities.recommendation;

import gr.mycities.recommendation.models.City;
import gr.mycities.recommendation.traveller.Traveler;
import java.util.ArrayList;
import java.util.Random;

/**
 * 
 * contains utilities functions that helps in the calculations of the project
 */
public class CalculationUtils {

    private final static double MAX_DISTANCE = 9520; // ATHENS-TOKYO distance - 9520 km

    //non instantiable class - just for utilities methods
    private CalculationUtils() {
        throw new RuntimeException("Non instantiable class");
    }

    /**
     * Counts distinct words in the input String.
     *
     * @param str The input String.
     * @return An integer, the number of distinct words.
     */
    public static int countDistinctWords(String str) {
        String s[] = str.split(" ");
        ArrayList<String> list = new ArrayList<>();

        for (int i = 1; i < s.length; i++) {
            if (!(list.contains(s[i]))) {
                list.add(s[i]);
            }
        }
        return list.size();
    }

    /**
     * Counts all words in the input String.
     *
     * @param str The input String.
     * @return An integer, the number of all words.
     */
    public static int countTotalWords(String str) {
        String s[] = str.split(" ");
        return s.length;
    }

    /**
     * Counts the number of times a criterion occurs in the city wikipedia
     * article.
     *
     * @param cityArticle The String of the retrieved wikipedia article.
     * @param criterion The String of the criterion we are looking for.
     * @return An integer, the number of times the criterion-string occurs in
     * the wikipedia article.
     */
    public static int countCriterionfCity(String cityArticle, String criterion) {
        cityArticle = cityArticle.toLowerCase();
        int index = cityArticle.indexOf(criterion);
        int count = 0;
        while (index != -1) {
            count++;
            cityArticle = cityArticle.substring(index + 1);
            index = cityArticle.indexOf(criterion);
        }
        return count;
    }

    // calculates based on geodesic distance between the traveller's place and the travel destination
    public static double similarity_geodesic_vector(Traveler traveller, City city) {
        double latTraveller = (double) traveller.getPlace().getGeodesic_vector().get(0);
        double lonTraveller = (double) traveller.getPlace().getGeodesic_vector().get(1);
        double latCity = (double) city.getPlace().getGeodesic_vector().get(0);
        double lonCity = (double) city.getPlace().getGeodesic_vector().get(1);
        double result = 2 / (2 * (distance(latTraveller, lonTraveller, latCity, lonCity, "K") / MAX_DISTANCE));
        result = customLog(2, result);
        return result;
    }

    // just a method to generate random strings 
    public static String generateRandomString() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();
        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        return generatedString;
    }

    /*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
 /*::                                                                         :*/
 /*::  This routine calculates the distance between two points (given the     :*/
 /*::  latitude/longitude of those points). It is being used to calculate     :*/
 /*::  the distance between two locations using GeoDataSource (TM) products   :*/
 /*::                                                                         :*/
 /*::  Definitions:                                                           :*/
 /*::    Southern latitudes are negative, eastern longitudes are positive     :*/
 /*::                                                                         :*/
 /*::  Function parameters:                                                   :*/
 /*::    lat1, lon1 = Latitude and Longitude of point 1 (in decimal degrees)  :*/
 /*::    lat2, lon2 = Latitude and Longitude of point 2 (in decimal degrees)  :*/
 /*::    unit = the unit you desire for results                               :*/
 /*::           where: 'M' is statute miles (default)                         :*/
 /*::                  'K' is kilometers                                      :*/
 /*::                  'N' is nautical miles                                  :*/
 /*::  Worldwide cities and other features databases with latitude longitude  :*/
 /*::  are available at https://www.geodatasource.com                         :*/
 /*::                                                                         :*/
 /*::  For enquiries, please contact sales@geodatasource.com                  :*/
 /*::                                                                         :*/
 /*::  Official Web site: https://www.geodatasource.com                       :*/
 /*::                                                                         :*/
 /*::           GeoDataSource.com (C) All Rights Reserved 2019                :*/
 /*::                                                                         :*/
 /*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        } else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            if (unit.equals("K")) {
                dist = dist * 1.609344;
            } else if (unit.equals("N")) {
                dist = dist * 0.8684;
            }
            return (dist);
        }
    }

    //we use the known log formula loga b = log10 b / log10 a
    private static double customLog(double base, double logNumber) {
        return Math.log(logNumber) / Math.log(base);
    }

}
