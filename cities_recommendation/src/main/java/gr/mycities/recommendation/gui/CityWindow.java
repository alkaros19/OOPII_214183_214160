package gr.mycities.recommendation.gui;

import gr.mycities.recommendation.MyCities;
import gr.mycities.recommendation.MyTravellers;
import gr.mycities.recommendation.exceptions.NoDocumentFoundForCityInWikipedia;
import gr.mycities.recommendation.exceptions.NoPlaceFoundInWeatherAPI;
import gr.mycities.recommendation.models.City;
import gr.mycities.recommendation.models.Place;
import gr.mycities.recommendation.traveller.Traveler;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import static javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.TableRowSorter;

/*
 *  window for handling the cities
 */
public class CityWindow {

    private TableRowSorter<CityTableModel> sorter; // we need it for search

    public JPanel createCityPanel(JPanel mainPanel, List<City> cities) {
        JPanel cityPanel = new JPanel();  // main windows of the city
        JPanel northPanel = new JPanel(new BorderLayout()); // holds the list of the cities
        JPanel southPanel = new JPanel(); // holds the buttons and the message area

        northPanel.setPreferredSize(new Dimension(1500, 300));
        CityTableModel cityModel = new CityTableModel(cities);
        JTable cityTable = new JTable(cityModel);
        cityTable.setAutoResizeMode(AUTO_RESIZE_ALL_COLUMNS);
        sorter = new TableRowSorter<CityTableModel>(cityModel); // initialize seaarch
        cityTable.setRowSorter(sorter);
        cityTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        JScrollPane jsp = new JScrollPane(cityTable);
        northPanel.add(jsp);
        JTextField tfCityName = new PromptTextField("city Name");
        southPanel.add(tfCityName);
        JTextField tfCountryName = new PromptTextField("country Name");
        southPanel.add(tfCountryName);
        JButton searchButton = new JButton("searchByCityName");
        JTextArea messagesArea = new JTextArea(16, 58);
        messagesArea.setEditable(false); // set textArea non-editable
        messagesArea.setLineWrap(true);
        messagesArea.setWrapStyleWord(true);
        ActionListener cityAction;
        cityAction = new ActionListener() { // one button listener for all actions
            @Override
            public void actionPerformed(ActionEvent e) {
                tfCityName.setBackground(Color.WHITE);
                tfCountryName.setBackground(Color.WHITE);
                switch (e.getActionCommand()) {
                    case "searchByCityName": //filter the list based on the city name
                        newFilter(tfCityName.getText());
                        break;
                    case "delete": // delete the chosen row
                        int selectedRow = cityTable.getSelectedRow();
                        if (selectedRow > -1) { // if not row selected the value is -1
                            // message for confirmation
                            selectedRow = cityTable.convertRowIndexToModel(cityTable.getSelectedRow());
                            City selectedCity = cityModel.getCities().get(selectedRow);
                            int res = JOptionPane
                                    .showConfirmDialog(null,
                                            "Do you want to delete "
                                                    + selectedCity.getPlace().getDescription() + "?",
                                            "File", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null);
                            if (res == 0) { // pressing yes on the confirmation message -> delete
                                cityModel.getCities().remove(selectedRow);
                                MyCities.deleteCity(selectedCity);
                                cityModel.fireTableDataChanged();
                            } else if (res == 1) {
                                //if we press no -> cancel delete
                            }
                        } else { //inform the user to select a row
                            JOptionPane.showMessageDialog(null, "Please select a row to delete!");
                        }
                        break;
                    case "clear search": // clear the filter
                        sorter.setRowFilter(null);
                        break;
                    case "insert":
                        City newCity = null;
                        try {
                            // new city
                            newCity = MyCities.getCity(new Place(tfCityName.getText(), tfCountryName.getText()));
                            cityModel.getCities().add(newCity);
                        } catch (NoDocumentFoundForCityInWikipedia | IOException | NoPlaceFoundInWeatherAPI ex) {
                            tfCityName.setBackground(Color.RED);
                            tfCountryName.setBackground(Color.RED);
                            messagesArea.append("Problem on insert a new city, please try again!\n");
                            messagesArea.append(ex.getMessage()+ "\n");
                            Logger.getLogger(CityWindow.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        cityModel.fireTableDataChanged();
                        break;
                    case "freeTicket":
                        selectedRow = cityTable.getSelectedRow();
                        if (selectedRow > -1) { // if not row selected the value is -1
                            selectedRow = cityTable.convertRowIndexToModel(cityTable.getSelectedRow());
                            City selectedCity = cityModel.getCities().get(selectedRow);
                            Traveler luckyTraveler = selectedCity.giveFreeTicket(new ArrayList<>(MyTravellers.getTravelers().keySet()), selectedRow);
                            messagesArea.append("The lucky traveler that wins a free ticket for "+ selectedCity.getPlace() + " is " + luckyTraveler +"\n" );
                        } else { //inform the user to select a row
                            JOptionPane.showMessageDialog(null, "Please select a row to give free ticket for selected city!");
                        }
                        break;
                    case "main menu": //back to main menu
                        System.out.println("main");
                        CardLayout cardLayout = (CardLayout) (mainPanel.getLayout());
                        cardLayout.show(mainPanel, "initial");
                        break;
                    default:
                        break;
                }
            }
        };
        searchButton.addActionListener(cityAction);
        southPanel.add(searchButton);
        JButton deleteButton = new JButton("delete");
        deleteButton.addActionListener(cityAction);
        southPanel.add(deleteButton);
        JButton clearSearchButton = new JButton("clear search");
        southPanel.add(clearSearchButton);
        clearSearchButton.addActionListener(cityAction);
        JButton insertButton = new JButton("insert");
        insertButton.addActionListener(cityAction);
        southPanel.add(insertButton);
        JButton freeTicketButton = new JButton("freeTicket");
        freeTicketButton.addActionListener(cityAction);
        southPanel.add(freeTicketButton);
        JButton mainMenuButton = new JButton("main menu");
        mainMenuButton.addActionListener(cityAction);
        southPanel.add(mainMenuButton);
        cityPanel.add(northPanel);
        cityPanel.add(southPanel);
        JScrollPane scrollMessagesArea = new JScrollPane(messagesArea);
        scrollMessagesArea.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        cityPanel.add(scrollMessagesArea);
        return cityPanel;
    }

    // search filter based on city name
    private void newFilter(String filterText) {
        RowFilter<CityTableModel, Object> rf;
        try { // filter with regex on city name
            rf = RowFilter.regexFilter(filterText, 0);
        } catch (java.util.regex.PatternSyntaxException e) {
            return;
        }
        sorter.setRowFilter(rf);
    }

}
