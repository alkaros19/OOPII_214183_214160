package gr.mycities.recommendation.gui;

import gr.mycities.recommendation.models.City;
import gr.mycities.recommendation.models.Term;
import java.util.List;
import javax.swing.table.AbstractTableModel;

// handles the cities entities representation to table of the gui
public class CityTableModel extends AbstractTableModel {

    private final List<City> cities; // data
    private final String[] columnNames = new String[]{"description", "country", "term", "rate", "term", "rate", "term", "rate", "term", "rate", "term", "rate", "term", "rate", "term", "rate", "term", "rate", "term", "rate", "term", "rate"}; // column names
    private final Class[] columnClass = new Class[]{String.class, String.class, String.class, Integer.class, String.class, Integer.class, String.class, Integer.class, String.class, Integer.class, String.class, Integer.class, String.class, Integer.class, String.class, Integer.class, String.class, Integer.class, String.class, Integer.class, String.class, Integer.class}; // column types

    public CityTableModel(List<City> cities) {
        super();
        this.cities = cities;
    }

    public List<City> getCities() {
        return cities;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnClass[columnIndex];
    }

    @Override
    public int getRowCount() {
        return cities.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        // show the values of data
        City row = cities.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> row.getPlace().getDescription();
            case 1 -> row.getPlace().getCountry();
            case 2 -> ((Term)row.getTerms_vector().get(0)).getDescription();
            case 3 -> ((Term)row.getTerms_vector().get(0)).getRate();
            case 4 -> ((Term)row.getTerms_vector().get(1)).getDescription();
            case 5 -> ((Term)row.getTerms_vector().get(1)).getRate();
            case 6 -> ((Term)row.getTerms_vector().get(2)).getDescription();
            case 7 -> ((Term)row.getTerms_vector().get(2)).getRate();
            case 8 -> ((Term)row.getTerms_vector().get(3)).getDescription();
            case 9 -> ((Term)row.getTerms_vector().get(3)).getRate();
            case 10 -> ((Term)row.getTerms_vector().get(4)).getDescription();
            case 11 -> ((Term)row.getTerms_vector().get(4)).getRate();
            case 12 -> ((Term)row.getTerms_vector().get(5)).getDescription();
            case 13 -> ((Term)row.getTerms_vector().get(5)).getRate();
            case 14 -> ((Term)row.getTerms_vector().get(6)).getDescription();
            case 15 -> ((Term)row.getTerms_vector().get(6)).getRate();
            case 16 -> ((Term)row.getTerms_vector().get(7)).getDescription();
            case 17 -> ((Term)row.getTerms_vector().get(7)).getRate();
            case 18 -> ((Term)row.getTerms_vector().get(8)).getDescription();
            case 19 -> ((Term)row.getTerms_vector().get(8)).getRate();
            case 20 -> ((Term)row.getTerms_vector().get(9)).getDescription();
            case 21 -> ((Term)row.getTerms_vector().get(9)).getRate();
            default -> null;
        };
            
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        // change the values of our data on the table
        City row = cities.get(rowIndex);
        if (0 == columnIndex) {
            row.getPlace().setDescription(aValue.toString());
        }
        this.fireTableCellUpdated(rowIndex, columnIndex);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

}
