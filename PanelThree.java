import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import javafx.geometry.*;
import java.util.stream.*;
import javafx.scene.Cursor;

/**
 * Panel three of our GUI
 * Displays the total deaths in the period
 * Displays the number of new cases in the period
 * Displays the change in number of people going to the park and for leisure
 * Displays the change in number of people going to the workplaces and commuting
 * 
 * Done by Maaz
 */
public class PanelThree extends Panel
{
    // Declaring instance variables
    private int deaths, cases, avgCases, gmr1, gmr2, currentPanel, numRecords;
    VBox label; // Vertical box for labels
    HBox hPanel; // Horizontal box for holding buttons and labels
    Label titleLabel, statsLabel; // Labels for displaying statistics

    /**
     * Constructor for objects of class PanelThree
     */
    public PanelThree()
    {
        // Initializing instance variables
        deaths = 0;
        cases = 0;
        avgCases = 0;
        currentPanel = 0;
        gmr1 = 0;
        gmr2 = 0;
        numRecords = 0;
    }

    /**
     * Method to create the panel
     *
     * @return VBox containing the panel
     */
    public VBox createPanel(){
        // Creating left button to navigate to previous statistic
        Button leftButton = new Button("<");
        leftButton.setOnMouseEntered(e -> leftButton.setCursor(Cursor.HAND)); // Change cursor to hand on hover
        leftButton.setMinWidth(100);
        
        // Creating right button to navigate to next statistic
        Button rightButton = new Button(">");
        rightButton.setOnMouseEntered(e -> rightButton.setCursor(Cursor.HAND)); // Change cursor to hand on hover
        rightButton.setMinWidth(100);
        
        // Increasing font size for the buttons
        leftButton.setStyle("-fx-font-size: 24px;");
        rightButton.setStyle("-fx-font-size: 24px;");
        
        // Creating horizontal panel to hold buttons and labels
        hPanel = new HBox();
        label = new VBox(); // Creating vertical panel to hold labels
        titleLabel = new Label("This is the statistics panel"); // Initial title label
        titleLabel.setStyle("-fx-font-size: 20px; -fx-text-alignment: center; margin-bottom: 50px;"); // Styling
        titleLabel.setWrapText(true); // Allowing text to wrap
        VBox.setMargin(titleLabel, new Insets(0, 0, 30, 0)); // Setting margin
        
        statsLabel = new Label("Use the arrows to navigate between stats"); // Initial stats label
        statsLabel.setStyle("-fx-font-size: 18px; -fx-text-alignment: center;"); // Styling
        statsLabel.setWrapText(true); // Allowing text to wrap
        
        // Adding initial labels to the label VBox
        label.getChildren().addAll(titleLabel, statsLabel);
        
        // Adding buttons and labels to the hPanel
        hPanel.getChildren().addAll(leftButton, label, rightButton);
        
        // Setting event handlers for left and right buttons
        leftButton.setOnAction(e->{
            if(currentPanel == 0){ // If on panel 0, go to panel 4
                currentPanel = 4;
                System.out.println("Panel no: " + currentPanel); //Unit test prompt
            } else { // Otherwise, decrement panel
                currentPanel -= 1;
                System.out.println("Panel no: " + currentPanel); //Unit test prompt
            }
            loadLabel(); // Load updated panel
            hPanel.getChildren().clear(); // Clear existing children
            hPanel.getChildren().addAll(leftButton, label, rightButton); // Add updated children
        });
        
        rightButton.setOnAction(e->{
            if(currentPanel == 4){ // If on panel 4, go to panel 0
                currentPanel = 0;
                System.out.println("Panel no: " + currentPanel); //Unit test prompt
            } else { // Otherwise, increment panel
                currentPanel += 1;
                System.out.println("Panel no: " + currentPanel); //Unit test prompt
            }
            loadLabel(); // Load updated panel
            hPanel.getChildren().clear(); // Clear existing children
            hPanel.getChildren().addAll(leftButton, label, rightButton); // Add updated children
        });
        
        // Setting layout properties
        hPanel.setHgrow(label, Priority.ALWAYS);
        hPanel.setMargin(leftButton, new Insets(10));
        hPanel.setMargin(rightButton, new Insets(10));
        hPanel.setAlignment(Pos.CENTER);
        label.setAlignment(Pos.CENTER);
        
        // Creating a VBox to hold the hPanel
        VBox panel = new VBox();
        panel.setAlignment(Pos.CENTER);
        panel.getChildren().add(hPanel);
        
        // Binding buttons' height properties to VBox's height property
        leftButton.prefHeightProperty().bind(panel.heightProperty());
        rightButton.prefHeightProperty().bind(panel.heightProperty());
        
        try {
            loadData(); // Load data for the selected date range
        } catch (java.text.ParseException pe) {
            System.out.println("Parse Error");
            pe.printStackTrace();
        }    
        
        return panel; // Return the panel
    }
    
    /**
     * Method to load the current panel's statistics
     */
    private void loadLabel(){
        // Displaying different statistics based on the current panel
        if(currentPanel == 0){
            titleLabel.setText("This is the statistics panel");
            statsLabel.setText("Use the arrows to navigate between stats");
        } else if(currentPanel == 1){
            titleLabel.setText("The total number of deaths in this period were:");
            statsLabel.setText(""+deaths);
        } else if(currentPanel == 2){
            titleLabel.setText("The average number of cases a day in this period were:");
            statsLabel.setText(""+avgCases);
        } else if(currentPanel == 3){
            int changeGMR1 = getAvgGMR1();
            titleLabel.setText("The average number of people at retail and recreation establishments and parks daily has changed by :");
            statsLabel.setText(""+changeGMR1);
        } else if(currentPanel == 4){
            int changeGMR2 = getAvgGMR2();
            titleLabel.setText("The average number of people transitting and going to workplaces daily has changed by :");
            statsLabel.setText(""+changeGMR2);
        }
        label.getChildren().clear(); // Clearing existing labels
        label.getChildren().addAll(titleLabel, statsLabel); // Adding updated labels
    }
    
    /**
     * Method to load data for the selected date range
     *
     * @throws java.text.ParseException if there is an error parsing dates
     */
    private void loadData() throws java.text.ParseException{
        System.out.println("Bound 1: " + selectedStartDate); // Unit test prompt to ensure correct start date being used
        System.out.println("Bound 2: " + selectedEndDate); // Unit test prompt to ensure correct start date being used
        
        // Looping through filtered data and calculating statistics
        for (CovidData data : filteredData) {
            //String thisDate = data.getDate(); // Extracting the date from the CovidData object
            numRecords++; // Incrementing the count of records processed
            
            // Accumulating the total number of deaths
            getDeaths(data);
            
            // Accumulating the total number of new cases
            getNewCases(data);
            
            // Accumulating the GMR1 which includes retail and recreation GMR and parks GMR
            getGMR1(data);
            
            // Accumulating the GMR2 which includes transit GMR and workplaces GMR
            getGMR2(data);
        }
        
        // Calculating the average number of cases per day in the period
        avgCases = cases / numRecords;
        
        // Unit test prompt printing out the total number of deaths
        System.out.println("Deaths Total: " + deaths);
        
        // Unit test prompt printing out the average daily cases
        System.out.println("Average Daily Cases: " + avgCases);
        
        // Unit test prompt printing out the average GMR1
        System.out.println("GMR1: " + getAvgGMR1());
        
        // Unit test prompt printing out the average GMR2
        System.out.println("GMR2: " + getAvgGMR2());

    }
    
    // Method to accumulate the total number of deaths from the given CovidData record
    private void getDeaths(CovidData thisRecord){
        deaths += thisRecord.getNewDeaths();
    }
    
    // Method to accumulate the total number of new cases from the given CovidData record
    private void getNewCases(CovidData thisRecord){
        cases += thisRecord.getNewCases();
    }
    
    // Method to accumulate the GMR data from the given CovidData record
    private void getGMR1(CovidData thisRecord){
        // Accumulating the retail and recreation GMR
        gmr1 += thisRecord.getRetailRecreationGMR();
        
        // Accumulating the parks GMR
        gmr1 += thisRecord.getParksGMR();
    }
    
    // Method to calculate the average GMR
    private int getAvgGMR1(){
        // Calculating the average GMR by dividing the accumulated GMR by the number of records
        int avgGMR1 = gmr1 / numRecords;
        return avgGMR1;
    }
    
    // Method to accumulate the GMR data from the given CovidData record
    private void getGMR2(CovidData thisRecord){
        // Accumulating the transit GMR
        gmr2 += thisRecord.getTransitGMR();
        
        // Accumulating the workplaces GMR
        gmr2 += thisRecord.getWorkplacesGMR();
    }
    
    // Method to calculate the average GMR
    private int getAvgGMR2(){
        // Calculating the average GMR by dividing the accumulated GMR by the number of records
        int avgGMR2 = gmr2 / numRecords;
        return avgGMR2;
    }

}
