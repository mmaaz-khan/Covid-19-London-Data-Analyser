import javafx.scene.layout.*;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToggleButton;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.text.Font;
import java.util.List;
import java.util.Arrays;
import javafx.geometry.*;
import java.util.Comparator;
import java.util.stream.*; 
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import javafx.scene.paint.Paint;

//
import java.time.LocalDate;
import javafx.scene.paint.Color;
import javafx.scene.Cursor;

/**
 * Panel four of our GUI
 * Displays the total and daily deaths and cases in chart form
 * Displays the total change of people from visiting a given area
 * 
 * Done by Adel 
 */
public class PanelFour extends Panel
{
    //Dates selected by user
    LocalDate currentStartDate;
    LocalDate currentEndDate;
    
    //Date range label to tell users to continue between the dates selected
    Label dateRangeLabel = new Label("");

    
    //VBox with labels of the changes of people in each area. One label per area
    VBox changeInPeople = new VBox();
    
    // Create the ComboBox that stores the names of all the boroughs
    ComboBox<String> boroughNames = new ComboBox<>();
    
    //Create the buttons and a toggleGroup so that only one button can be pressed at a time
    ToggleButton btnCases = new ToggleButton("Cases");
    ToggleButton btnDeaths = new ToggleButton("Deaths");
    ToggleGroup toggleGroup = new ToggleGroup();
    
    
    // Creating the bar chart and its axis
    CategoryAxis xAxisBar = new CategoryAxis();
    NumberAxis yAxisBar = new NumberAxis();
    BarChart<String, Number> barChart = new BarChart<>(xAxisBar, yAxisBar);
    
    
    //creating the line chart and its axis
    CategoryAxis xAxisLine = new CategoryAxis();
    NumberAxis yAxisLine = new NumberAxis();
    LineChart lineChart = new LineChart<>(xAxisLine, yAxisLine);
    
    //Create the center of the root borderpane
    VBox centerPanelFour;
    

    /**
     * Constructor for objects of class PanelFour
     */
    public PanelFour()
    {
        btnCases.setOnMouseEntered(e -> btnCases.setCursor(Cursor.HAND)); // change cursor to hand on hover
        btnDeaths.setOnMouseEntered(e -> btnDeaths.setCursor(Cursor.HAND)); // change cursor to hand on hover
        createCenterPanelFour();
    }
    
    /**
     * Update the text and color of the date range label to show the dates the user selected
     */
    public void updateDateRangeLabel(String text, Paint color){
        dateRangeLabel.setText(text);
        dateRangeLabel.setTextFill(color);
    }
    
    /**
     * Return center panel four vBox
     */
    public VBox getCenterPanelFour() {
        return centerPanelFour;
    }

    /**
     * Create the fourth panel. 
     * Create all the elements of the fourth panel: title label, date range label, boroughNames comboBox, Cases and Deaths buttons,
     * line and bar chart (in chartsHBox), and labels of total change of people in areas. 
     * Then add all of these elements to the VBox centerPanelFour.
     */
    public void createCenterPanelFour()
    {
        //Elements for center of borderpane 2
        Label introductoryText1 = new Label("Charts");
        introductoryText1.setFont(new Font("Arial", 24.00));
        
        
        //Set text of date range label, set color, and set font
        dateRangeLabel.setText("USE THE BUTTONS TO PROCEED WITH DATA BETWEEN " + selectedStartDate 
                    + " and " + selectedEndDate);
        dateRangeLabel.setTextFill(Color.color(0, 0, 1));
        dateRangeLabel.setFont(new Font("Arial", 16.00));
        
        
        //Creating a list of Strings of each boroughs name
        List<String> boroughs = Arrays.asList(
            "Barking And Dagenham", "Barnet", "Bexley", "Brent", "Bromley",
            "Camden", "City Of London", "Croydon", "Ealing", "Enfield", "Greenwich",
            "Hackney", "Hammersmith And Fulham", "Haringey", "Harrow",
            "Havering", "Hillingdon", "Hounslow", "Islington", "Kensington And Chelsea",
            "Kingston Upon Thames", "Lambeth", "Lewisham", "Merton", "Newham",
            "Redbridge", "Richmond Upon Thames", "Southwark", "Sutton", "Tower Hamlets",
            "Waltham Forest", "Wandsworth", "Westminster"
        );

        //Adding all borough names into the comboBox boroughNames
        boroughNames.getItems().addAll(boroughs);
        boroughNames.setPromptText("Select a borough");
        
        // Event handler for selection change
        boroughNames.setOnAction(e -> {
            String selectedBorough = boroughNames.getValue();
            updateAppropriateChart();
        });
        
        
        //Add both ToggleButtons to the ToggleGroup so that only one button can be pressed at a time
        btnCases.setToggleGroup(toggleGroup);
        btnDeaths.setToggleGroup(toggleGroup);
        HBox buttonsHBox = new HBox(50);
        buttonsHBox.getChildren().addAll(btnCases, btnDeaths);
        btnCases.setSelected(true);
        btnCases.setOnAction(e -> {
            updateAppropriateChart();
        });
        btnDeaths.setOnAction(e -> {
            updateAppropriateChart();
        });
        
        
        //Add label to x-axis  
        xAxisBar.setLabel("Dates");
        //Add label to x-axis  
        xAxisLine.setLabel("Dates");
        
        //Creating a new HBox for charts 
        HBox chartsHBox = new HBox(barChart, lineChart);
        chartsHBox.layout();
        xAxisBar.layout();
        yAxisBar.layout();
        

        // Set the bounds of x-axis and y-axis to match the bounds of the chart
        xAxisBar.setAnimated(false);
        xAxisLine.setAnimated(false);
        yAxisBar.setAnimated(false);
        yAxisLine.setAnimated(false);
        
        
        //Creating the main panel 4 VBox and adding all the elements
        centerPanelFour = new VBox(15);
        centerPanelFour.getChildren().addAll(introductoryText1, dateRangeLabel, boroughNames, buttonsHBox, chartsHBox, changeInPeople);
        
        //positioning elements in panel 4
        centerPanelFour.setAlignment(Pos.CENTER);
        changeInPeople.setAlignment(Pos.CENTER);
        buttonsHBox.setAlignment(Pos.CENTER);
    }
    
    /**
     * Update the charts based on the selections of the user. 
     * The selections include the dates, borough, and to view either cases or deaths
     * The appropriate cases or deaths charts will be displayed for the selected borough during the selected dates
     */
    public void updateAppropriateChart() {
        //Get filtered Data
        List<CovidData> boroughFilteredData = getBoroughData(boroughNames.getValue());
        
        // Update Labels
        updateChangeLabels(boroughFilteredData);
        
        // clear char chart and line chart
        barChart.getData().clear();
        lineChart.getData().clear();
        
        // Group data by date and calculate aggregate values
        Map<String, Integer> newCasesMap = new HashMap<>();
        Map<String, Integer> newDeathsMap = new HashMap<>();
        
        // Iterate through the filtered data and aggregate new cases and new deaths by date
        for (CovidData data : boroughFilteredData) {
            newCasesMap.merge(data.getDate(), data.getNewCases(), Integer::sum);
            newDeathsMap.merge(data.getDate(), data.getNewDeaths(), Integer::sum);
        }
        
        // Sort the dates
        List<String> sortedDates = new ArrayList<>(newCasesMap.keySet());
        sortedDates.sort(Comparator.naturalOrder());
        
        // Create series for new cases and new deaths
        XYChart.Series<String, Number> barSeries = new XYChart.Series<>();        
        
        if (btnCases.isSelected()) 
        //if the user selected to view cases
        {
            // Add data to the series
            for (String date : sortedDates) {
                barSeries.getData().add(new XYChart.Data<>(date, newCasesMap.get(date)));
            }
            //set names of chart elements
            barChart.setTitle("New Cases Every Day");
            barSeries.setName("Cases");
            yAxisBar.setLabel("Number of Cases");
        } else if (btnDeaths.isSelected()) 
        //if the user selected to view deaths
        {
            // Add data to the series
            for (String date : sortedDates) {
                barSeries.getData().add(new XYChart.Data<>(date, newDeathsMap.get(date)));
            }
            //set names of chart elements
            barChart.setTitle("New Deaths Every Day");
            barSeries.setName("Deaths");
            yAxisBar.setLabel("Number of Deaths");
        }
        // Add series to the bar chart
        barChart.getData().add(barSeries);
        
        //create a new series for the line chart
        XYChart.Series<String, Number> lineSeries = new XYChart.Series<>();
        
        // Sort the filtered data by date to ensure the line graph is in order
        boroughFilteredData.sort(Comparator.comparing(CovidData::getDate));
        
        if (btnCases.isSelected()) 
        //if the user selected to view cases
        {
            // Populate series with new data
            for (CovidData data : boroughFilteredData) {
                String formattedDate = data.getDate(); 
                lineSeries.getData().add(new XYChart.Data<>(formattedDate, data.getTotalCases()));
            } 
            //set names of chart elements
            lineChart.setTitle("Total Cases");
            lineSeries.setName("Cases");
            yAxisLine.setLabel("Number of Cases");
        } else if (btnDeaths.isSelected()) 
        //if the user selected to view deaths
        {
            // Populate series with new data
            for (CovidData data : boroughFilteredData) {
                String formattedDate = data.getDate();
                lineSeries.getData().add(new XYChart.Data<>(formattedDate, data.getTotalDeaths()));
            }
            //set names of chart elements
            lineChart.setTitle("Total Deaths");
            lineSeries.setName("Deaths");
            yAxisLine.setLabel("Number of Deaths");
        }
        
        //Add series to line chart
        lineChart.getData().add(lineSeries);
    }

    /**
     * Create the labels for the VBox changeInPeople to display the total change in people visiting each area mentioned. 
     * This method calculates the sum of the change in people everyday in each area and then creates a label for to display this. 
     * All the labels are added to the VBox changeInPeople. 
     */
    private void updateChangeLabels(List<CovidData> boroughFilteredData) {
        //clear existing changeInPeople
        changeInPeople.getChildren().clear();
        
        //default font for labels
        Font fontForLabels = new Font("Arial", 16.00);
        
        //create title label and add font
        Label peopleChangeTitle = new Label("Total change of number of people visiting an area");
        peopleChangeTitle.setFont(new Font("Arial", 20.00));
        
        // Calculate aggregate values and create labels
        Integer totalRetailRecreation = boroughFilteredData.stream().mapToInt(CovidData::getRetailRecreationGMR).sum();
        Label totalRetailRecreationLBL = new Label("Retail and Recreation: " + totalRetailRecreation.toString());
        totalRetailRecreationLBL.setFont(fontForLabels);
        
        Integer totalGroceryPharmacy = boroughFilteredData.stream().mapToInt(CovidData::getGroceryPharmacyGMR).sum();
        Label totalGroceryPharmacyLBL = new Label("Grocery and Pharmacy: " + totalGroceryPharmacy.toString());
        totalGroceryPharmacyLBL.setFont(fontForLabels);
        
        Integer totalParks = boroughFilteredData.stream().mapToInt(CovidData::getParksGMR).sum();
        Label totalParksLBL = new Label("Parks: " + totalParks.toString());
        totalParksLBL.setFont(fontForLabels);
        
        Integer totalWorkplaces = boroughFilteredData.stream().mapToInt(CovidData::getWorkplacesGMR).sum();
        Label totalWorkplacesLBL = new Label("Workplaces: " + totalWorkplaces.toString());
        totalWorkplacesLBL.setFont(fontForLabels);
        
        Integer totalResidential = boroughFilteredData.stream().mapToInt(CovidData::getResidentialGMR).sum();
        Label totalResidentialLBL = new Label("Residential: " + totalResidential.toString());
        totalResidentialLBL.setFont(fontForLabels);
        
        //Add all labels to changeInPeople
        changeInPeople.getChildren().addAll(peopleChangeTitle, totalRetailRecreationLBL, totalGroceryPharmacyLBL, totalParksLBL, totalWorkplacesLBL, totalResidentialLBL);        
    }
}