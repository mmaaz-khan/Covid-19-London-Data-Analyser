import javafx.scene.Scene;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import javafx.stage.Stage;
import javafx.geometry.*;
import java.util.Set;
import java.util.stream.*;
import javafx.scene.paint.Color;
import java.util.HashMap;
import java.util.Map;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.scene.Cursor;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import java.util.Comparator;
import java.time.LocalDate;

/**
 * Panel Two of our GUI
 * Displays geographical visual representation of London boroughs, coloured according to death rates
 * Contains clickable elements to view sortable table of data for each given borough
 *
 *
 */
public class PanelTwo extends Panel
{
    
    /**
     * Constructor for objects of class PanelTwo
     */
    public PanelTwo()
    {
        
    }

    /**
     * Method to create the panel
     *
     * @return VBox containing the panel
     */
    public VBox createPanel()
    {
        VBox rows = new VBox();
        rows.setPadding(new Insets(50, 0, 0, -50)); // top margin for whole panel
        double size = 50; // size of the hexagon
        
        // map borough codes to full names
        Map<String, String> boroughNames = new HashMap<>();
        boroughNames.put("ENFI", "Enfield");
        boroughNames.put("BARN", "Barnet");
        boroughNames.put("HRGY", "Haringey");
        boroughNames.put("WALT", "Waltham Forest");
        boroughNames.put("HRRW", "Harrow");
        boroughNames.put("BREN", "Brent");
        boroughNames.put("CAMD", "Camden");
        boroughNames.put("ISLI", "Islington");
        boroughNames.put("HACK", "Hackney");
        boroughNames.put("REDB", "Redbridge");
        boroughNames.put("HAVE", "Havering");
        boroughNames.put("HILL", "Hillingdon");
        boroughNames.put("EALI", "Ealing");
        boroughNames.put("KENS", "Kensington and Chelsea");
        boroughNames.put("WSTM", "Westminster");
        boroughNames.put("TOWH", "Tower Hamlets");
        boroughNames.put("NEWH", "Newham");
        boroughNames.put("BARK", "Barking and Dagenham");
        boroughNames.put("HOUN", "Hounslow");
        boroughNames.put("HAMM", "Hammersmith and Fulham");
        boroughNames.put("WAND", "Wandsworth");
        boroughNames.put("CITY", "City of London");
        boroughNames.put("GWCH", "Greenwich");
        boroughNames.put("BEXL", "Bexley");
        boroughNames.put("RICH", "Richmond upon Thames");
        boroughNames.put("MERT", "Merton");
        boroughNames.put("LAMB", "Lambeth");
        boroughNames.put("STHW", "Southwark");
        boroughNames.put("LEWS", "Lewisham");
        boroughNames.put("KING", "Kingston upon Thames");
        boroughNames.put("SUTT", "Sutton");
        boroughNames.put("CROY", "Croydon");
        boroughNames.put("BROM", "Bromley");
        
        // labels for hexagons
        String[][] labels = {
            {"", "ENFI"},
            {"", "BARN", "HRGY", "WALT", ""},
            {"", "HRRW", "BREN", "CAMD", "ISLI", "HACK", "REDB", "HAVE"},
            {"HILL", "EALI", "KENS", "WSTM", "TOWH", "NEWH", "BARK"},
            {"HOUN", "HAMM", "WAND", "CITY", "GWCH", "BEXL"},
            {"RICH", "MERT", "LAMB", "STHW", "LEWS"},
            {"KING", "SUTT", "CROY", "BROM"}
        };
        
        String boroughCode, borough;
        
        // to ensure related logic is only performed once, and not for each hexagon
        int maxDeaths = findHighestTotalDeaths();
    
        // build the hexagon panel
        for (int i = 0; i < labels.length; i++) {
            // calculated spacing to cope with hexagon rotation
            HBox row = new HBox(-16 + 10);
            row.setAlignment(Pos.CENTER);
        
            for (int j = 0; j < labels[i].length; j++) {
                if (!labels[i][j].isEmpty()) {
                    // create hexagon if label exists within array
                    boroughCode = labels[i][j];
                    borough = boroughNames.get(boroughCode);
                    row.getChildren().add(createHexagon(size, boroughCode, borough, maxDeaths));
                } else {
                    // placeholder hexagon space for alignment purposes
                    row.getChildren().add(createPlaceholder(size));
                }
            }
    
            rows.getChildren().add(row);
            double margin = 11 + -25 + 8;
            VBox.setMargin(row, new Insets(0, 0, margin, 0));
        }
    
        return rows;
    }
    
    /**
     * Method to create (and colour) the hexagon
     *
     * @return StackPane containing the hexagon with its label
     */
    private StackPane createHexagon(double size, String boroughCode, String borough, int maxDeaths) {
        // create hexagon shape
        Polygon hexagon = new Polygon();
        for (int i = 0; i < 6; i++) {
            hexagon.getPoints().addAll(
                size * Math.cos(i * 2 * Math.PI / 6),
                size * Math.sin(i * 2 * Math.PI / 6)
            );
        }
        hexagon.setFill(Color.WHITE);
        hexagon.setStroke(Color.BLACK);
        hexagon.setRotate(30); // rotate the hexagon by 30 degrees
        
        // define color levels for darkening aspect
        Color[] darkenedColors = new Color[5]; // array to hold darkened colors
        darkenedColors[0] = Color.GREEN; // first level of darkening
        darkenedColors[1] = darkenedColors[0].darker(); // second level of darkening
        darkenedColors[2] = darkenedColors[1].darker(); // third level of darkening
        darkenedColors[3] = darkenedColors[2].darker(); // fourth level of darkening
        darkenedColors[4] = darkenedColors[3].darker(); // fifth level of darkening
        
        // if maxDeaths is 0, then colours should be default green
        if(maxDeaths > 0){
            int boroughMaxDeaths = findBoroughTotalDeaths(borough);
            double boundary1, boundary2, boundary3, boundary4;
            
            // if less than 5 then set boundaries by default to ensure accurate even spread of colours
            if(maxDeaths <= 5){
                boundary1 = 1;
                boundary2 = 2;
                boundary3 = 3;
                boundary4 = 4;
            }else{
                boundary1 = maxDeaths / 5;
                boundary2 = maxDeaths / 5 * 2;
                boundary3 = maxDeaths / 5 * 3;
                boundary4 = maxDeaths / 5 * 4;
            }
            
            // set colours according to 1/5 (20%) sized boundaries
            if(boroughMaxDeaths < boundary1){
                hexagon.setFill(darkenedColors[0]);
            }
            if(boroughMaxDeaths < boundary2 && boroughMaxDeaths >= boundary1){
                hexagon.setFill(darkenedColors[1]);
            }
            if(boroughMaxDeaths < boundary3 && boroughMaxDeaths >= boundary2){
                hexagon.setFill(darkenedColors[2]);
            }
            if(boroughMaxDeaths < boundary4 && boroughMaxDeaths >= boundary3){
                hexagon.setFill(darkenedColors[3]);
            }
            if(boroughMaxDeaths >= boundary4){
                hexagon.setFill(darkenedColors[4]);
            }
        }else{
            hexagon.setFill(darkenedColors[0]);
        }
    
        // label for borough names
        Label label = new Label(boroughCode);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setAlignment(Pos.CENTER);
        label.setTextFill(Color.WHITE);
    
        // add the hexagon and label to a StackPane
        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(hexagon, label);
        
        // open new window onclick of stackpane
        stackPane.setOnMouseClicked(e -> showBoroughDetails(borough));
        
        // set cursor to pointer on hover of hex and label
        // not on overall stackpane as was not behaving as expected
        hexagon.setOnMouseEntered(e -> hexagon.setCursor(Cursor.HAND)); // change cursor to hand on hover
        label.setOnMouseEntered(e -> label.setCursor(Cursor.HAND)); // change cursor to hand on hover
    
        return stackPane; // return the StackPane containing the hexagon and label
    }
    
    /**
     * Method to create the placeholder hexagon
     *
     * @return StackPane containing the placeholder hexagon
     */
    private StackPane createPlaceholder(double size) {
        // create hexagon shape
        Polygon hexagon = new Polygon();
        for (int i = 0; i < 6; i++) {
            hexagon.getPoints().addAll(
                size * Math.cos(i * 2 * Math.PI / 6),
                size * Math.sin(i * 2 * Math.PI / 6)
            );
        }
        hexagon.setFill(Color.WHITE);
        hexagon.setStroke(Color.BLACK);
        hexagon.setRotate(30); // rotate the hexagon by 30 degrees
    
        // add the hexagon to a StackPane
        StackPane stackPane = new StackPane(hexagon);
        stackPane.setOpacity(0); // make invisible as it is a placeholder
    
        return stackPane; // return the StackPane containing the hexagon placeholder
    }
    
    /**
     * Method to find the value of whichever borough has the highest number of deaths
     *
     * @return int containing the highest death count
     */
    public int findHighestTotalDeaths() {
        int maxDeaths = 0;
        for (CovidData data : filteredData) {
            // check for maximum total deaths
            if (data.getTotalDeaths() > maxDeaths) {
                maxDeaths = data.getTotalDeaths();
            }
        }

        return maxDeaths;
    }
    
    /**
     * Method to find the value of the highest number of deaths, for a given borough
     *
     * @return int containing the highest death countm for a given borough
     */
    public int findBoroughTotalDeaths(String boroughName) {
        int totalDeaths = 0;
        for (CovidData data : getBoroughData(boroughName)) {
            // check for maximum total deaths for borough
            if (data.getTotalDeaths() > totalDeaths) {
                totalDeaths = data.getTotalDeaths();
            }
        }

        return totalDeaths;
    }
    
    /**
     * Method to create the window containing a table of information
     * Opened when a hexagon is clicked from the map
     */
    private void showBoroughDetails(String borough) {
        // creation of new window
        Stage detailsStage = new Stage();
        detailsStage.setWidth(1230); // adjust width of new window
        detailsStage.setTitle("COVID Details for " + borough);
        
        // convert already filtered list to an observable list for use with table
        ObservableList<CovidData> data = FXCollections.observableArrayList(getBoroughData(borough));
        TableView<CovidData> table = new TableView<>();
        table.setItems(data);
    
        // Date Column
        TableColumn<CovidData, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getDate()));
        dateColumn.setCellValueFactory(cellData -> {
            // parse date string from CovidData object into a LocalDate
            LocalDate date = LocalDate.parse(cellData.getValue().getDate(), inputFormatter);
            // return with desired date format
            return new ReadOnlyStringWrapper(date.format(formatter));
        });
        dateColumn.setSortable(false); // disable sorting
    
        // Google Mobility Data Columns
        TableColumn<CovidData, Number> retailRecreationGMRColumn = new TableColumn<>("Retail & Recreation GMR");
        retailRecreationGMRColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getRetailRecreationGMR()));
        retailRecreationGMRColumn.setSortable(false); // disable sorting
    
        TableColumn<CovidData, Number> groceryPharmacyGMRColumn = new TableColumn<>("Grocery & Pharmacy GMR");
        groceryPharmacyGMRColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getGroceryPharmacyGMR()));
        groceryPharmacyGMRColumn.setSortable(false); // disable sorting
    
        TableColumn<CovidData, Number> parksGMRColumn = new TableColumn<>("Parks GMR");
        parksGMRColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getParksGMR()));
        parksGMRColumn.setSortable(false); // disable sorting
    
        TableColumn<CovidData, Number> transitStationsGMRColumn = new TableColumn<>("Transit Stations GMR");
        transitStationsGMRColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getTransitGMR()));
        transitStationsGMRColumn.setSortable(false); // disable sorting
    
        TableColumn<CovidData, Number> workplacesGMRColumn = new TableColumn<>("Workplaces GMR");
        workplacesGMRColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getWorkplacesGMR()));
        workplacesGMRColumn.setSortable(false); // disable sorting
    
        TableColumn<CovidData, Number> residentialGMRColumn = new TableColumn<>("Residential GMR");
        residentialGMRColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getResidentialGMR()));
        residentialGMRColumn.setSortable(false); // disable sorting
    
        // New COVID Cases Column
        TableColumn<CovidData, Number> newCasesColumn = new TableColumn<>("New COVID Cases");
        newCasesColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getNewCases()));
        newCasesColumn.setSortable(false); // disable sorting
    
        // Total COVID Cases Column
        TableColumn<CovidData, Number> totalCasesColumn = new TableColumn<>("Total COVID Cases");
        totalCasesColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getTotalCases()));
        totalCasesColumn.setSortable(false); // disable sorting
    
        // New COVID Deaths Column
        TableColumn<CovidData, Number> newDeathsColumn = new TableColumn<>("New COVID Deaths");
        newDeathsColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getNewDeaths()));
        newDeathsColumn.setSortable(false); // disable sorting
        
        // Add all columns to the table
        table.getColumns().addAll(dateColumn, retailRecreationGMRColumn, groceryPharmacyGMRColumn, parksGMRColumn, transitStationsGMRColumn, workplacesGMRColumn, residentialGMRColumn, newCasesColumn, totalCasesColumn, newDeathsColumn);                        
                                  
        // table sorting ComboBox
        ComboBox<String> sortComboBox = new ComboBox<>();
        sortComboBox.getItems().addAll(
            "Date", 
            "Retail & Recreation GMR",
            "Grocery & Pharmacy GMR",
            "Parks GMR", 
            "Transit Stations GMR",
            "Workplaces GMR", 
            "Residential GMR",
            "New COVID Cases", 
            "Total COVID Cases", 
            "New COVID Deaths"
        );
        sortComboBox.setPromptText("Sort by"); // set default text
        
        // adjust how the ObservableList sorts the data according to given column
        sortComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                switch (newVal) {
                    case "Date":
                        data.sort(Comparator.comparing(CovidData::getDate));
                        break;
                    case "Retail & Recreation GMR":
                        data.sort(Comparator.comparingInt(CovidData::getRetailRecreationGMR));
                        break;
                    case "Grocery & Pharmacy GMR":
                        data.sort(Comparator.comparingInt(CovidData::getGroceryPharmacyGMR));
                        break;
                    case "Parks GMR":
                        data.sort(Comparator.comparingInt(CovidData::getParksGMR));
                        break;
                    case "Transit Stations GMR":
                        data.sort(Comparator.comparingInt(CovidData::getTransitGMR));
                        break;
                    case "Workplaces GMR":
                        data.sort(Comparator.comparingInt(CovidData::getWorkplacesGMR));
                        break;
                    case "Residential GMR":
                        data.sort(Comparator.comparingInt(CovidData::getResidentialGMR));
                        break;
                    case "New COVID Cases":
                        data.sort(Comparator.comparingInt(CovidData::getNewCases));
                        break;
                    case "Total COVID Cases":
                        data.sort(Comparator.comparingInt(CovidData::getTotalCases));
                        break;
                    case "New COVID Deaths":
                        data.sort(Comparator.comparingInt(CovidData::getNewDeaths));
                        break;
                }
                table.refresh(); // refresh the table
            }
        });
    
        // configure window elements
        VBox layout = new VBox(20);
        layout.getChildren().addAll(sortComboBox, table);
    

        Scene scene = new Scene(layout);
        detailsStage.setScene(scene);
        detailsStage.show();
    }
}
