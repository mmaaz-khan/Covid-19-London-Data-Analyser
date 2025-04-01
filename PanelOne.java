import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import javafx.geometry.*;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Panel One of our GUI
 * Displays welcome messages
 * Displays messages based on selected date range
 *
 * @author Shafkat Khan, Jamil Karabaev
 */
public class PanelOne extends Panel
{
    /**
     * Constructor for objects of class PanelOne
     */
    public PanelOne()
    {
        
    }

    /**
     * Method to create the panel
     *
     * @return VBox containing the panel
     */
    public VBox createPanel()
    {
        // create the contents
        Label dateRangeLabel = new Label("");
        Label introductoryTextOne = new Label("Welcome, select a date range, and after you can use the buttons to navigate through panels.");
        Label introductoryTextTwo = new Label("This application will help you to visualize covid data for boroughs of london over a date range.");
        Font fontForLabels = new Font("Arial", 16.00);
        Font fontForDateRangeLabel = new Font("Arial", 16.00);
        introductoryTextOne.setFont(fontForLabels);
        introductoryTextTwo.setFont(fontForLabels);
        dateRangeLabel.setFont(fontForDateRangeLabel);
        
        // display message idf date range is selected
        if(selectedStartDate != null && selectedEndDate != null){
            // parse dates to correct format
            LocalDate dateOne = LocalDate.parse(selectedStartDate, formatter);
            LocalDate dateTwo = LocalDate.parse(selectedEndDate, formatter);

            if(dateTwo.isAfter(dateOne) || selectedStartDate.equals(selectedEndDate)){
                dateRangeLabel.setText("USE THE BUTTONS TO PROCEED WITH DATA BETWEEN " + selectedStartDate 
                + " and " + selectedEndDate);
                dateRangeLabel.setTextFill(Color.color(0, 0, 1));
            }else{
                dateRangeLabel.setText("DATE RANGE SELECTED IS INVALID! PLEASE TRY AGAIN");
                dateRangeLabel.setTextFill(Color.color(1, 0, 0));
            }
        }
        
        VBox introductoryTexts = new VBox(introductoryTextOne, introductoryTextTwo, dateRangeLabel);
        introductoryTexts.setAlignment(Pos.CENTER);
        
        return introductoryTexts;
    }
}