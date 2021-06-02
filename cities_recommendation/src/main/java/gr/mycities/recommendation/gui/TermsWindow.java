package gr.mycities.recommendation.gui;

import gr.mycities.recommendation.MyCities;
import gr.mycities.recommendation.MyTravellers;
import gr.mycities.recommendation.exceptions.NoDocumentFoundForCityInWikipedia;
import gr.mycities.recommendation.models.City;
import gr.mycities.recommendation.models.Reccomendation;
import gr.mycities.recommendation.models.Term;
import gr.mycities.recommendation.traveller.Traveler;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
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
import javax.swing.ScrollPaneConstants;

/*
 *  handling terms window
 */
public class TermsWindow {

    public JPanel createCityPanel(JPanel mainPanel, List<String> terms) {
        JPanel cityPanel = new JPanel();  // main terms window
        JPanel northPanel = new JPanel(new BorderLayout()); // holds the lsit of terms
        JPanel southPanel = new JPanel(); // holds the button

        northPanel.setPreferredSize(new Dimension(1500, 300));
        TermsTableModel termsModel = new TermsTableModel(terms);
        JTable termsTable = new JTable(termsModel); // o pinakas me ti lista
        termsTable.setAutoResizeMode(AUTO_RESIZE_ALL_COLUMNS);
        termsTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        JScrollPane jsp = new JScrollPane(termsTable);
        northPanel.add(jsp);
        JTextArea messagesArea = new JTextArea(16, 58);
        messagesArea.setEditable(false); // set textArea non-editable
        messagesArea.setLineWrap(true);
        messagesArea.setWrapStyleWord(true);
        ActionListener cityAction;
        cityAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch (e.getActionCommand()) {
                    case "main menu": //back to main menu
                        System.out.println("main");
                        CardLayout cardLayout = (CardLayout) (mainPanel.getLayout());
                        cardLayout.show(mainPanel, "initial");
                        break;
                    case "rebuildTerms": // rebuild all data to new terms
                        int res = JOptionPane
                                .showConfirmDialog(null,
                                        "Are you sure you want to rebuild all data? Travelers terms will set to zero! It takes some time...",
                                        "File", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null);
                        if (res == 0) { // if we press yes, delete the row
                            Runnable r = new Runnable() {
                                @Override
                                public void run() {
                                    // rebuild the cities
                                    for (City city : MyCities.getCities()) {
                                        for (var i = 0; i < city.getTerms_vector().size(); i++) {
                                            Term term = (Term) city.getTerms_vector().get(i);
                                            term.setDescription(termsModel.getTerms().get(i));
                                            term.setRate(0);
                                        }
                                        try {
                                            Thread t = MyCities.setTerms(city);
                                            t.join();
                                            MyCities.updateCity(city);
                                        } catch (NoDocumentFoundForCityInWikipedia | IOException ex) {
                                            Logger.getLogger(TermsWindow.class.getName()).log(Level.SEVERE, null, ex);
                                            messagesArea.append(ex.toString());
                                        } catch (InterruptedException ex) {
                                            Logger.getLogger(TermsWindow.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                    }
                                    // rebuild the travelers
                                    Iterator it = MyTravellers.getTravelers().keySet().iterator();
                                    HashMap<Traveler, Reccomendation> newTravelers = new HashMap<>();
                                    while (it.hasNext()) {
                                        Traveler tr = (Traveler) it.next();
                                        for (var i = 0; i < tr.getTerms().size(); i++) {
                                            Term term = (Term) tr.getTerms().get(i);
                                            term.setDescription(termsModel.getTerms().get(i));
                                            term.setRate(0);
                                        }
                                        newTravelers.put(tr, null);
                                    }
                                    MyTravellers.getTravelers().clear();
                                    MyTravellers.getTravelers().putAll(newTravelers);
                                }
                            };
                            Thread t = new Thread(r);
                            t.start();
                        } else if (res == 1) {
                            // we do nothing
                        }
                    default:
                        break;
                }
            }
        };
        JButton rebuildButton = new JButton("rebuildTerms");
        rebuildButton.addActionListener(cityAction);
        southPanel.add(rebuildButton);
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

}
