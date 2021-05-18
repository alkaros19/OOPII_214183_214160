package gr.mycities.recommendation.gui;

import gr.mycities.recommendation.MyCities;
import gr.mycities.recommendation.MyConstants;
import gr.mycities.recommendation.MyTravellers;
import gr.mycities.recommendation.exceptions.NoAcceptedAgeException;
import gr.mycities.recommendation.exceptions.NoPlaceFoundInWeatherAPI;
import gr.mycities.recommendation.models.City;
import gr.mycities.recommendation.models.Place;
import gr.mycities.recommendation.models.Reccomendation;
import gr.mycities.recommendation.models.Term;
import gr.mycities.recommendation.models.TravelerWithRecommendation;
import gr.mycities.recommendation.traveller.Traveler;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
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
 *  cities windows
 */
public class TravelerWindow {

    private TableRowSorter<TravelerTableModel> sorter; // we need it for the search

    public JPanel createTravelerPanel(JPanel mainPanel, List<TravelerWithRecommendation> travelersWithRecommendations) {
        JPanel travelerPanel = new JPanel();  // main window of the4 city
        JPanel northPanel = new JPanel(new BorderLayout()); // holds the list of the cities
        JPanel southPanel = new JPanel(); // holds the buttons and the messages area

        northPanel.setPreferredSize(new Dimension(1500, 300));
        TravelerTableModel travelerModel = new TravelerTableModel(travelersWithRecommendations);
        JTable cityTable = new JTable(travelerModel);
        cityTable.setAutoResizeMode(AUTO_RESIZE_ALL_COLUMNS);
        sorter = new TableRowSorter<>(travelerModel); // init table
        cityTable.setRowSorter(sorter);
        cityTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        JScrollPane jsp = new JScrollPane(cityTable);
        northPanel.add(jsp);
        JTextField tfTravelerName = new PromptTextField("traveler Name");
        southPanel.add(tfTravelerName);
        JTextField tfAge = new PromptTextField("traveler Age");
        southPanel.add(tfAge);
        JTextField tfCityName = new PromptTextField("city Name");
        southPanel.add(tfCityName);
        JTextField tfCountryName = new PromptTextField("country Name");
        southPanel.add(tfCountryName);
        JButton searchButton = new JButton("searchByTravelerName");
        JTextArea messagesArea = new JTextArea(16, 58);
        messagesArea.setEditable(false); // set textArea non-editable
        messagesArea.setLineWrap(true);
        messagesArea.setWrapStyleWord(true);
        Integer[] numbers = {1, 2, 3, 4, 5};
        JComboBox numberOfCitiesCombo = new JComboBox(numbers);
        ActionListener travelerAction = new ActionListener() { // a listener handles all the buttons 
            @Override
            public void actionPerformed(ActionEvent e) {
                tfTravelerName.setBackground(Color.WHITE);
                tfAge.setBackground(Color.WHITE);
                tfCityName.setBackground(Color.WHITE);
                tfCountryName.setBackground(Color.WHITE);
                switch (e.getActionCommand()) {
                    case "searchByTravelerName": // search by travelers name
                        newFilter(tfTravelerName.getText());
                        break;
                    case "delete": // delete the selected row
                        int selectedRow = cityTable.getSelectedRow();
                        if (selectedRow > -1) { // if not row chosen the value is -1
                            // minima gia epivevaiosi diagrafis
                            selectedRow = cityTable.convertRowIndexToModel(cityTable.getSelectedRow());
                            int res = JOptionPane
                                    .showConfirmDialog(null,
                                            "Do you want to delete "
                                            + travelerModel.getTravelersWithRecommendations().get(selectedRow).getTraveler().getName() + "?",
                                            "File", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null);
                            if (res == 0) { // if we press yes, delete the row
                                MyTravellers.deleteByTravelerName(travelerModel.getTravelersWithRecommendations().get(selectedRow).getTraveler().getName());
                                travelerModel.getTravelersWithRecommendations().remove(selectedRow);
                                travelerModel.fireTableDataChanged();
                            } else if (res == 1) {
                                //if we press cancel on the alert box
                            }
                        } else { //if not row is chosen inform the user
                            JOptionPane.showMessageDialog(null, "Please select a row to delete!");
                        }
                        break;
                    case "clear search": // clear the search filter 
                        sorter.setRowFilter(null);
                        break;
                    case "insert":
                        TravelerWithRecommendation newTravelerWithRecommendation = null;
                        try {
                            // new traveler
                            Place newPlace = new Place(tfCityName.getText(), tfCountryName.getText());
                            newPlace.calculateGeodesic();
                            Traveler newTraveler = Traveler.createTraveller(Integer.parseInt(tfAge.getText()), newPlace);
                            newTraveler.setName(tfTravelerName.getText());
                            for (String Term : MyConstants.TERMS) { // create the terms for each travelers -> 0 rates, we can edit from the table
                                int rate = 0;
                                newTraveler.getTerms().add(new Term(Term, rate));
                            }
                            newTravelerWithRecommendation = new TravelerWithRecommendation(newTraveler, null);
                            travelerModel.getTravelersWithRecommendations().add(newTravelerWithRecommendation);
                            MyTravellers.addReccomendation(newTraveler, null);
                            messagesArea.append("new traveler added : " + newTraveler + "\n");
                        } catch (NoAcceptedAgeException | NumberFormatException naae) {
                            messagesArea.append("error on insertion : " + naae.getMessage() + "\n");
                            tfAge.setBackground(Color.RED);
                            Logger.getLogger(TravelerWindow.class.getName()).log(Level.SEVERE, null, naae);
                        } catch (IOException | NoPlaceFoundInWeatherAPI ex) {
                            tfCityName.setBackground(Color.RED);
                            tfCountryName.setBackground(Color.RED);
                            messagesArea.append("error on insertion : " + ex.getMessage() + "\n");
                            Logger.getLogger(TravelerWindow.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        travelerModel.fireTableDataChanged();
                        break;
                    case "main menu": //return to main menu
                        System.out.println("main");
                        CardLayout cardLayout = (CardLayout) (mainPanel.getLayout());
                        cardLayout.show(mainPanel, "initial");
                        break;
                    case "recommendCities":
                        selectedRow = cityTable.getSelectedRow();
                        if (selectedRow > -1) { // if not row chosen the value is -1 
                            selectedRow = cityTable.convertRowIndexToModel(cityTable.getSelectedRow());
                            TravelerWithRecommendation tr = travelerModel.getTravelersWithRecommendations().get(selectedRow);
                            City[] compareCities = tr.getTraveler().compare_cities(MyCities.getCities(), (int) numberOfCitiesCombo.getSelectedItem(), MyConstants.PARAMETER_FOR_SIMILARITY_FUNCTION);
                            messagesArea.append("for traveler " + tr.getTraveler() + " the recommended cities to visit are: \n");
                            int i = 1;
                            for (City city : compareCities) {
                                messagesArea.append((i++) + ":" + city.getPlace() + "\n");
                            }
                            messagesArea.append("******************\n");
                            Reccomendation rc = new Reccomendation(compareCities[0]);
                            tr.setReccomendation(rc);
                            MyTravellers.addReccomendation(tr.getTraveler(), rc);
                            travelerModel.fireTableDataChanged();
                        } else { //just inform the user to select a row
                            JOptionPane.showMessageDialog(null, "Please select a row to recommend a city!");
                        }
                        break;
                    default:
                        break;
                }
            }
        };
        searchButton.addActionListener(travelerAction);
        southPanel.add(searchButton);
        JButton deleteButton = new JButton("delete");
        deleteButton.addActionListener(travelerAction);
        southPanel.add(deleteButton);
        JButton clearSearchButton = new JButton("clear search");
        southPanel.add(clearSearchButton);
        clearSearchButton.addActionListener(travelerAction);
        JButton insertButton = new JButton("insert");
        insertButton.addActionListener(travelerAction);
        southPanel.add(insertButton);
        JLabel numberofCitiesLabel = new JLabel("numberOfRecommendedCities");
        southPanel.add(numberofCitiesLabel);
        southPanel.add(numberOfCitiesCombo);
        JButton recommendCitiesButton = new JButton("recommendCities");
        recommendCitiesButton.addActionListener(travelerAction);
        southPanel.add(recommendCitiesButton);
        JButton mainMenuButton = new JButton("main menu");
        mainMenuButton.addActionListener(travelerAction);
        southPanel.add(mainMenuButton);
        travelerPanel.add(northPanel);
        travelerPanel.add(southPanel);
        JScrollPane scrollMessagesArea = new JScrollPane(messagesArea);
        scrollMessagesArea.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        travelerPanel.add(scrollMessagesArea);
        return travelerPanel;
    }

    // filter for the search by travelers name
    private void newFilter(String filterText) {
        RowFilter<TravelerTableModel, Object> rf;
        try { // regex for the first field, the travelers name
            rf = RowFilter.regexFilter(filterText, 0);
        } catch (java.util.regex.PatternSyntaxException e) {
            return;
        }
        sorter.setRowFilter(rf);
    }

}
