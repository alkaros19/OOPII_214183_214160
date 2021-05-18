package gr.mycities.recommendation.gui;

import gr.mycities.recommendation.models.Term;
import gr.mycities.recommendation.models.TravelerWithRecommendation;
import java.util.List;
import javax.swing.table.AbstractTableModel;

// handles the travelersWithRecommendations entities representation to table of the gui
public class TravelerTableModel extends AbstractTableModel {

    private final List<TravelerWithRecommendation> travelersWithRecommendations; // data
    private final String[] columnNames = new String[]{"traveler", "age", "city", "country", "reccommended", "term", "rate", "term", "rate", "term", "rate", "term", "rate", "term", "rate", "term", "rate", "term", "rate", "term", "rate", "term", "rate", "term", "rate"}; // column names
    private final Class[] columnClass = new Class[]{String.class, Integer.class, String.class, String.class, String.class, String.class, Integer.class, String.class, Integer.class, String.class, Integer.class, String.class, Integer.class, String.class, Integer.class, String.class, Integer.class, String.class, Integer.class, String.class, Integer.class, String.class, Integer.class, String.class, Integer.class}; // column types

    public TravelerTableModel(List<TravelerWithRecommendation> cities) {
        super();
        this.travelersWithRecommendations = cities;
    }

    public List<TravelerWithRecommendation> getTravelersWithRecommendations() {
        return travelersWithRecommendations;
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
        return travelersWithRecommendations.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        // show the values of data
        TravelerWithRecommendation row = travelersWithRecommendations.get(rowIndex);
        if (0 == columnIndex) {
            return row.getTraveler().getName();
        } else if (1 == columnIndex) {
            return row.getTraveler().getAge();
        } else if (2 == columnIndex) {
            return row.getTraveler().getPlace().getDescription();
        } else if (3 == columnIndex) {
            return row.getTraveler().getPlace().getCountry();
        } else if (4 == columnIndex) {
            if (row.getReccomendation() != null) {
                return row.getReccomendation().getVisit().getPlace().getDescription();
            } else {
                return null;
            }
        } else if (columnIndex > 4 && columnIndex < (5 + 20)) { // show terms
            try {
                if (columnIndex % 2 == 1) {
                    return ((Term) row.getTraveler().getTerms().get((columnIndex - 5) / 2)).getDescription();
                } else {
                    return ((Term) row.getTraveler().getTerms().get((columnIndex - 6) / 2)).getRate();
                }
            } catch (Exception e) {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        // change the values of our data on the table
        TravelerWithRecommendation row = travelersWithRecommendations.get(rowIndex);
        if (columnIndex > 4 && columnIndex % 2 == 1) {
            row.getTraveler().getTerms().add((columnIndex - 5) / 2, new Term(aValue.toString(), 0));
        } else {
            ((Term) row.getTraveler().getTerms().get((columnIndex - 6) / 2)).setRate(Integer.parseInt(aValue.toString()));
        }
        this.fireTableCellUpdated(rowIndex, columnIndex);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        //editable only the rates
        return (columnIndex > 4 && columnIndex % 2 == 0);
    }

}
