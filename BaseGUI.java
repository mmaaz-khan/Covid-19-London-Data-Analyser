import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import javafx.stage.Stage;
import javafx.geometry.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.*;
import javafx.scene.paint.Color;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import javafx.scene.Cursor;

/**
 * Base GUI / wrapper for the panels
 *
 * @author Jamil Karabaev, Shafkat Khan
 */
public class BaseGUI extends Application
{
    /**/
    private Stage primaryStage; 
    
    private ComboBox<String> dateDropDownOne = new ComboBox<>();
    private ComboBox<String> dateDropDownTwo = new ComboBox<>();
    private Button bottomLeftButton = new Button("<");
    private Button bottomRightButton = new Button(">");
    private Label dateRangeLabel = new Label("");
    private int activePanelId = 0;
    
    // declare static variables
    public static String selectedStartDate;
    public static String selectedEndDate;
    public static DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    // initialize the data loader and load the data
    public static CovidDataLoader loader = new CovidDataLoader();
    public static List<CovidData> allData = loader.load();

    public static List<CovidData> filteredData;
    /**/
    
    /**
     * Start method
     */
    @Override
    public void start(Stage stage) throws java.text.ParseException
    {
        /**/
        BorderPane root = new BorderPane();
        this.primaryStage = stage; // store the primary stage
        
        // fetch the sorted, unique, set of dates for the dropdowns from the covidData
        ArrayList<String> sortedDatesString = fetchSortedUniqueDates();
        
        
        // elements for top of borderpane
        dateDropDownOne.setPromptText("-");
        dateDropDownTwo.setPromptText("-");
        dateDropDownOne.getItems().addAll(sortedDatesString);
        dateDropDownTwo.getItems().addAll(sortedDatesString);
        dateDropDownOne.setOnAction(e -> processDateDropDowns(e));
        dateDropDownTwo.setOnAction(e -> processDateDropDowns(e));
        
        Label fromLabel = new Label("From: ");
        Label toLabel = new Label("To: ");
        HBox dateDropDowns = new HBox(fromLabel, dateDropDownOne, toLabel, dateDropDownTwo);
        dateDropDowns.setAlignment(Pos.TOP_RIGHT);
        dateDropDowns.setMargin(dateDropDownOne, new Insets(10));
        dateDropDowns.setMargin(dateDropDownTwo, new Insets(10));
        dateDropDowns.setMargin(fromLabel, new Insets(10));
        dateDropDowns.setMargin(toLabel, new Insets(10));
        
        Line topSectionLine = new Line();
        topSectionLine.endXProperty().bind(root.widthProperty());
        VBox topSection = new VBox(dateDropDowns, topSectionLine);
        
        Line bottomSectionLine = new Line();
        bottomSectionLine.endXProperty().bind(root.widthProperty());
        
        
        bottomLeftButton.setOnMouseEntered(e -> bottomLeftButton.setCursor(Cursor.HAND)); // change cursor to hand on hover
        bottomRightButton.setOnMouseEntered(e -> bottomRightButton.setCursor(Cursor.HAND)); // change cursor to hand on hover
        
        // initially disable panel switching buttons
        bottomLeftButton.setDisable(true);
        bottomRightButton.setDisable(true);
        
        bottomLeftButton.setOnAction(e -> switchPanel(false, false)); // panel navigate backwards
        bottomRightButton.setOnAction(e -> switchPanel(true, false)); // panel navigate forwards
        
        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);
        HBox bottomButtons = new HBox(bottomLeftButton, region, bottomRightButton);
        bottomButtons.setMargin(bottomLeftButton, new Insets(10));
        bottomButtons.setMargin(bottomRightButton, new Insets(10));
        VBox bottomSection = new VBox(bottomSectionLine, bottomButtons);
        
        root.setTop(topSection);
        root.setAlignment(dateDropDowns, Pos.TOP_RIGHT);
        root.setBottom(bottomSection);
        
        
        // initalise panel 1 (ie. welcome panel)
        root.setCenter(createPanel(1));
        
        root.setAlignment(dateDropDowns, Pos.CENTER);

        Scene scene = new Scene(root, 1000,800);
        stage.setTitle("CW4: Covid Data Analyser");
        stage.setScene(scene);
        
        // Show the Stage (window)
        stage.show();
    }
    
    /**
     * Method called on action of the date dropdowns
     * Reloads data and calls method to switch panel
     */
    private void processDateDropDowns(ActionEvent event)
    {
        if(dateDropDownOne.getValue() != null && dateDropDownTwo.getValue() != null){
            // set values of static date variables
            selectedStartDate = dateDropDownOne.getValue();
            selectedEndDate = dateDropDownTwo.getValue();
            
            LocalDate dateOne = LocalDate.parse(selectedStartDate, formatter);
            LocalDate dateTwo = LocalDate.parse(selectedEndDate, formatter);
            
            if(dateTwo.isAfter(dateOne) || selectedStartDate.equals(selectedEndDate) ) {
                // initially disable buttons before recreating current panel 
                bottomLeftButton.setDisable(false);
                bottomRightButton.setDisable(false);
                
                // refresh CovidData list which is filtered by date
                filteredData = getFilteredList();
                
                // refresh current panel
                switchPanel(true, true);
            } else {
                // disable panel switching buttons
                bottomLeftButton.setDisable(true);
                bottomRightButton.setDisable(true);
                
                // make panel 1 (welcome panel) active
                BorderPane root = (BorderPane)primaryStage.getScene().getRoot();
                root.setCenter(createPanel(1));
                activePanelId = 0;
            }
        }
    }
    
    /**
     * Fetch the unique set of dates from the CSV file
     */
    private ArrayList<String> fetchSortedUniqueDates() throws java.text.ParseException {
        // retrieve dates in yyyy-mm-dd, but return as dd/mm/yyyy
        List<LocalDate> sortedDates = allData.stream()
                        .map(row -> LocalDate.parse(row.getDate(), inputFormatter))
                        .distinct()
                        .sorted() // sort as LocalDate objects
                        .collect(Collectors.toList());
                        
        ArrayList<String> sortedDatesString = new ArrayList<>();

        for(LocalDate localDate : sortedDates) {
            sortedDatesString.add(localDate.format(formatter));
        }
        
        return sortedDatesString;
    }
    
    /**
     * Creates objects of each panel and creates each one's related VBox
     */
    private VBox createPanel(int panelNumber) {
        if(panelNumber == 1){
            PanelOne panel1 = new PanelOne();
            VBox panel = panel1.createPanel();
            return panel;
        }else if(panelNumber == 2){
            PanelTwo panel2 = new PanelTwo();
            VBox panel = panel2.createPanel();
            return panel;
        }else if(panelNumber == 3){
            PanelThree panel3 = new PanelThree();
            VBox panel = panel3.createPanel();
            return panel;
        } else{
            PanelFour panelFour = new PanelFour();
            VBox panel = panelFour.getCenterPanelFour();
            return panel;
        }
    }
    
    /**
     * Switch active panel
     */
    private void switchPanel(boolean forward, boolean refresh) {
        // increment/decrement panel if not refreshing current panel
        if (!refresh) {
            if (forward) {
                activePanelId = (activePanelId + 1) % 4; // cycle forward through panel IDs
            } else {
                activePanelId = (activePanelId - 1 + 4) % 4; // cycle backward through panel IDs
            }
        }
    
        // set new panel as active
        BorderPane root = (BorderPane)primaryStage.getScene().getRoot();
        root.setCenter(createPanel(activePanelId + 1));
                
        // enable/disable panel switching buttons accordingly
        bottomLeftButton.setDisable(activePanelId == 0);
        bottomRightButton.setDisable(activePanelId == 3);
    }
    
    /**
     * Filter the CovidData list according to the date range
     */
    public static List<CovidData> getFilteredList() {
        // format dates to correct format
        LocalDate startDate = LocalDate.parse(selectedStartDate, formatter);
        LocalDate endDate = LocalDate.parse(selectedEndDate, formatter);
        
        // filter CovidData based on date range
        List<CovidData> filteredData = allData.stream()
            .filter(data -> {
                LocalDate dataDate = LocalDate.parse(data.getDate(), inputFormatter);
                return !dataDate.isBefore(startDate) && !dataDate.isAfter(endDate);
            })
            .collect(Collectors.toList());

        // return filtered list
        return filteredData;
    }
}
