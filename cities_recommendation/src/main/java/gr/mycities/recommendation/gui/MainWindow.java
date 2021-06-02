package gr.mycities.recommendation.gui;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import gr.mycities.recommendation.MyCities;
import gr.mycities.recommendation.MyConstants;
import gr.mycities.recommendation.MyTerms;
import gr.mycities.recommendation.MyTravellers;
import gr.mycities.recommendation.models.TravelerWithRecommendation;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class MainWindow {

    private enum WindowNames {
        CITIES, TRAVELERS, TERMS
    };

    public void createWindow() {
        JFrame frame = new JFrame("Traveler Reccomendations");
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) { // when we close the application, we save the values
                // write to json file
                ObjectMapper mapper = new ObjectMapper();
                ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
                try {
                    System.out.println(MyTravellers.getTravelers());
                    writer.writeValue(Paths.get(MyConstants.JSON_FILE_NAME).toFile(), MyTravellers.getTravelers());
                } catch (IOException ex) {
                    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                }
                String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
                String appConfigPath = rootPath + "terms.properties";
                Properties appProps = new Properties();
                try {
                    StringBuilder sb = new StringBuilder();
                    MyTerms.terms.forEach(t -> {
                        sb.append(",").append(t);
                    }); 
                    appProps.setProperty("terms", sb.toString().substring(1));
                    appProps.store(new FileWriter(appConfigPath), "store to properties file");
                } catch (IOException ex) {
                    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.exit(0);
            }
        });
        createUI(frame);
        frame.setSize(1600, 800);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void createUI(final JFrame frame) {
        JPanel mainPanel = new JPanel();
        CardLayout cardLayout = new CardLayout();
        mainPanel.setLayout(cardLayout);
        JPanel initialPanel = new JPanel();

        // the buttons of the first menu
        String[] myButtons = {"Cities", "Travelers", "Terms"};
        for (int i = 0; i < myButtons.length; i++) {
            JButton menuButton = new JButton();
            menuButton.setText(myButtons[i]);
            final Integer innerI = i;  // variables must be final inside the listener
            menuButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // we put the card we want to go on every button
                    CardLayout cardLayout = (CardLayout) (mainPanel.getLayout());
                    cardLayout.show(mainPanel, WindowNames.values()[innerI].name());
                }
            });
            // add every button
            initialPanel.add(menuButton);
        }
        mainPanel.add(initialPanel, "initial");
        CityWindow cityWindow = new CityWindow();
        mainPanel.add(cityWindow.createCityPanel(mainPanel, MyCities.getCities()), WindowNames.CITIES.name());
        TravelerWindow travelerWindow = new TravelerWindow();
        List<TravelerWithRecommendation> myData = new ArrayList<>();
        myData.addAll(MyTravellers.getTravelersReccomendations());
        mainPanel.add(travelerWindow.createTravelerPanel(mainPanel, myData), WindowNames.TRAVELERS.name());
        TermsWindow termsWindow = new TermsWindow();

        mainPanel.add(termsWindow.createCityPanel(mainPanel, MyTerms.terms), WindowNames.TERMS.name());
        frame.getContentPane().add(mainPanel, BorderLayout.CENTER);
    }

}
