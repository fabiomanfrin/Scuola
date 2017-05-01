package fabiomanfrin.carfinder;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private MapFragment mapFragment;
    private GoogleMap mMap;
    private long minTime=(1*60*1000)/2; //30 seconds
    private float minDistance=500;   //500 meters
    private TextView locationText;
    private Location location;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_fragment, container, false);



    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initMap();
        locationText=(TextView)getActivity().findViewById(R.id.locationText);
        loc();


     /*   Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // update TextView here!
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        t.start();*/

    }

    public void initMap() {
        if (mMap == null) {
            mapFragment=(MapFragment) getChildFragmentManager().findFragmentById(R.id.mini_map);
            mapFragment.getMapAsync(this);
        }





    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


/***at this time google play services are not initialize so get map and add what ever you want to it in onResume() or onStart() **/
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        if (location!=null){
        LatLng you=new LatLng(location.getLatitude(),location.getLongitude());
        mMap.addMarker(new MarkerOptions().position(you).title("You are here").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car_marker)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(you));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));

        mMap.addPolyline(new PolylineOptions().add(you, new LatLng(45, 12))
                .width(5)
               .color(Color.BLUE)
        );
        }



        /*    String Url = "https://maps.googleapis.com/maps/api/directions/json?origin=Toronto&destination=Montreal&key=AIzaSyDcRarWNqsbymt_SHnfwQceOrlOeJq7U1g";
        HttpURLConnection conn = null;
        JSONObject object=null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            URL url = new URL(Url);
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
            if (conn != null) {
                conn.disconnect();
            }

            object = new JSONObject(jsonResults.toString());
        }
        catch(MalformedURLException e){

        }
        catch(IOException e){

        }
        catch(JSONException e){

        }

        System.out.println(object);*/




    }

   /* public LatLng getLocation()
    {
        // Get the location manager
        myLocationListener myLocListener = new myLocationListener();
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, false);
        Double lat,lng;
        try {
            Location location = locationManager.getLastKnownLocation(bestProvider);
            locationManager.requestLocationUpdates(bestProvider, minTime, minDistance, myLocListener);
            lat = location.getLatitude ();
            lng = location.getLongitude ();
            return new LatLng(lat, lng);
        }
        catch (NullPointerException e){
            e.printStackTrace();
            return null;
        }
        catch(SecurityException sEx){
            sEx.printStackTrace();
            return null;
        }

    }*/

    public void loc(){
        myLocationListener myLocListener = new myLocationListener(locationText);
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, false);

        try {
            location = locationManager.getLastKnownLocation(bestProvider);
            locationManager.requestLocationUpdates(bestProvider, minTime, 0, myLocListener);


        }
        catch (NullPointerException e){
            e.printStackTrace();

        }
        catch(SecurityException sEx){
            sEx.printStackTrace();

        }
    }




}
