import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.*; 


/**
 * Parent class to the panels
 *
 *
 */
public class Panel
{
    protected String selectedStartDate;
    protected String selectedEndDate;
    protected List<CovidData> filteredData;
    protected DateTimeFormatter formatter;
    protected DateTimeFormatter inputFormatter;

    /**
     * Constructor for objects of class Panel
     */
    public Panel()
    {
        // retrieve static data from BaseGUI
        selectedStartDate = BaseGUI.selectedStartDate;
        selectedEndDate = BaseGUI.selectedEndDate;
        filteredData = BaseGUI.filteredData; // retrieve data already filtered according to date range
        formatter = BaseGUI.formatter; // formatter in dd/MM/yyyy
        inputFormatter = BaseGUI.inputFormatter; // formatter in yyyy-MM-dd
    }
    
    /**
     * Fetching data for a specific borough
     */
    public List<CovidData> getBoroughData(String selectedBorough) {
        // Filter CovidData based on selected borough and date range
        List<CovidData> boroughFilteredData = filteredData.stream()
            .filter(data -> data.getBorough().equalsIgnoreCase(selectedBorough))
            .collect(Collectors.toList());
        
        return boroughFilteredData;
    }
}
