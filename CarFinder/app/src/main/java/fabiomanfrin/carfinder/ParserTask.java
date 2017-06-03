package fabiomanfrin.carfinder;

import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A class to parse the Google Places in JSON format
 */
public class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

    private Fragment f;
    private HomeFragment hf;
    private ArrayList<Parking> car_parkings;
    private String TAG="myTAG";
  /*  public ParserTask(Fragment f){
        this.f=f;
        hf=(HomeFragment)f;
        mMap=hf.getMap();
        car_parkings=hf.getCarParkings();


    }*/
    private GoogleMap mMap;
    /*public DownloadTask(Fragment f){
        this.f=f;
    }*/
    public ParserTask(Home home,GoogleMap map){
        mMap=map;
        car_parkings=home.getListParkings();
    }

    // Parsing the data in non-ui thread
    @Override
    protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

        JSONObject jObject;
        List<List<HashMap<String, String>>> routes = null;

        try {
            jObject = new JSONObject(jsonData[0]);
            DirectionsJSONParser parser = new DirectionsJSONParser();

            // Starts parsing data
            routes = parser.parse(jObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return routes;
    }

    // Executes in UI thread, after the parsing process
    @Override
    protected void onPostExecute(List<List<HashMap<String, String>>> result) {
        ArrayList<LatLng> points = null;
        PolylineOptions lineOptions = null;
        MarkerOptions markerOptions = new MarkerOptions();
        String distance = "";
        String duration = "";

        // Traversing through all the routes
        for (int i = 0; i < result.size(); i++) {
            points = new ArrayList<LatLng>();
            lineOptions = new PolylineOptions();

            // Fetching i-th route
            List<HashMap<String, String>> path = result.get(i);

            // Fetching all the points in i-th route
            for (int j = 0; j < path.size(); j++) {
                HashMap<String, String> point = path.get(j);

                if (j == 0) {    // Get distance from the list
                    distance = point.get("distance");
                    continue;
                } else if (j == 1) { // Get duration from the list
                    duration = point.get("duration");

                    continue;
                }



                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);

                points.add(position);
            }

            // Adding all the points in the route to LineOptions
            lineOptions.addAll(points);
            lineOptions.width(10);
            lineOptions.color(Color.CYAN);
        }



        // Drawing polyline in the Google Map for the i-th route
        //locationText.append("Distance:" + distance + ", Duration:" + duration);
        //Toast.makeText(hf.getActivity(), "Distance:" + distance + ", Duration:" + duration, Toast.LENGTH_SHORT).show();

        mMap.addPolyline(lineOptions);
        loadParkings();
    }

    private void loadParkings(){

        Double Lat;
        Double Lng;
        String title;
        for (int i=0;i<car_parkings.size();i++){

            title=car_parkings.get(i).getTitle();
            Lat=car_parkings.get(i).getLat();
            Lng=car_parkings.get(i).getLng();

            //Log.d(TAG, "onPostExecute: "+Lat+" "+Lng);
            LatLng myLatLng=new LatLng(Lat,Lng);
            mMap.addMarker(new MarkerOptions()
                    .position(myLatLng)
                    .title(title)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_car_black_24dp)));
            Log.d(TAG, "mappa parser: "+title+myLatLng.toString());
        }

    }

}
