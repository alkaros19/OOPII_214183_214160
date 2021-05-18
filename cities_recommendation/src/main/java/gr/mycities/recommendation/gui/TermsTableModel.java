package gr.mycities.recommendation.gui;

import java.util.List;
import javax.swing.table.AbstractTableModel;

// handles the cities entities representation to table of the gui
public class TermsTableModel extends AbstractTableModel {

    private final List<String> terms; // data
    
    private final String[] columnNames = new String[]{"term"}; // column names
    private final Class[] columnClass = new Class[]{String.class}; // column types

    public TermsTableModel(List<String> terms) {
        super();
        this.terms = terms;
    }

    public List<String> getTerms() {
        return terms;
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
        return terms.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        // show the values of data

        return switch (columnIndex) {
            case 0 -> terms.get(rowIndex);
            default -> null;
        };
            
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        // change the values of our data on the table
        if (0 == columnIndex) {
             terms.set(rowIndex, aValue.toString());
        }
        this.fireTableCellUpdated(rowIndex, columnIndex);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

}
