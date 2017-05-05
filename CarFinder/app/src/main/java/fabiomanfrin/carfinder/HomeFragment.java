package fabiomanfrin.carfinder;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private MapFragment mapFragment;
    private GoogleMap mMap;
    private long minTime = (1 * 60 * 1000) / 4; //15 seconds
    private float minDistance = 500;   //500 meters
    private TextView locationText;
    private Location location;
    private LocationManager locationManager;
    private String bestProvider;

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
        locationText = (TextView) getActivity().findViewById(R.id.locationText);
        Button b = (Button) getActivity().findViewById(R.id.refresh_button);
        loc();

        if (location != null) {
            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
            locationText.setText(currentLocation.toString());
        } else {
            locationText.setText("Location not found");
        }
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.addParking_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                Bundle b = new Bundle();
                if (location == null) {
                    b.putDouble("lat", 0);
                    b.putDouble("lat", 0);
                } else {
                    b.putDouble("lat", location.getLatitude());
                    b.putDouble("lng", location.getLongitude());
                }
                addParkingFragment ap = new addParkingFragment();
                ap.setArguments(b);
                ((Home) getActivity()).replacefragment(ap);  // replace fragment in fragment layout with a addParking Fragment

            }
        });

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }

                location = locationManager.getLastKnownLocation(bestProvider);
                locationText.setText(new LatLng(location.getLatitude(),location.getLongitude()).toString());
                Log.d("refresh", new LatLng(location.getLatitude(),location.getLongitude()).toString());
            }
        });

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
        mMap.addMarker(new MarkerOptions().position(you).title("You are here").icon(BitmapDescriptorFactory.fromResource(R.drawable.car_marker80x80)));
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
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        bestProvider = locationManager.getBestProvider(criteria, false);

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
