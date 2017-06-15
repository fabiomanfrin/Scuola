package fabiomanfrin.carfinder;

/**
 * Created by Fabio on 15/06/2017.
 */

public class ParkingsPlace {
    //empty construct
    public ParkingsPlace(){}

    private String Title;
    private String Coordinates;
    private String Description;

    public ParkingsPlace(String title,String coordinates,String description){
        Title=title;
        Coordinates=coordinates;
        Description=description;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getCoordinates() {
        return Coordinates;
    }

    public void setCoordinates(String coordinates) {
        Coordinates = coordinates;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }
}
