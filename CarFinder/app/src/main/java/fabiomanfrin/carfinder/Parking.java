package fabiomanfrin.carfinder;

import com.google.firebase.database.DataSnapshot;

/**
 * Created by fabio on 5/18/17.
 */

public class Parking {
    private String title;
    private Double Lat;
    private Double Lng;


    public Parking(String title, Double lat, Double lng) {
        this.title = title;
        Lat = lat;
        Lng = lng;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getLat() {
        return Lat;
    }

    public void setLat(Double lat) {
        Lat = lat;
    }

    public Double getLng() {
        return Lng;
    }

    public void setLng(Double lng) {
        Lng = lng;
    }


}
