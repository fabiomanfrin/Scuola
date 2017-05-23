package fabiomanfrin.carfinder;

import com.google.firebase.database.DataSnapshot;

/**
 * Created by fabio on 5/18/17.
 */

public class Parking {
    private String title;
    private Coordinates c;



    public Parking(String title, Double lat, Double lng) {
        this.title = title;
        c=new Coordinates(lat,lng);

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Coordinates getCoordinates() {
        return c;
    }

    public Double getLat() {
        return c.getLat();
    }

    public Double getLng() {
        return c.getLng();
    }


    private class Coordinates {
        private Double Lat;
        private Double Lng;

        public Coordinates(Double Lat,Double Lng){
            this.Lat=Lat;
            this.Lng=Lng;
        }

        public Double getLat() {
            return Lat;
        }

        public Double getLng() {
            return Lng;
        }
    }
}
