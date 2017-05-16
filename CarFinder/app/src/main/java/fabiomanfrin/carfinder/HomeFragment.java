package fabiomanfrin.carfinder;


import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.Settings;
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
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements OnMapReadyCallback {

    String TAG = "myTAG";
    private MapFragment mapFragment;
    private GoogleMap mMap;
    private long minTime =1 * 30 * 1000; //30 seconds
    private float minDistance = 0;   //0 meters
    private TextView locationText;
    private Location location;
    private LocationManager locationManager;
    private String bestProvider;
    private myLocationListener myLocListener;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String userId;

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

        beginAuth();
        locationText = (TextView) getActivity().findViewById(R.id.locationText);
        getLocation();
        initMap();

        Button b = (Button) getActivity().findViewById(R.id.refresh_button);





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
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Unable to add a new car parking cause current location missing")
                            .setCancelable(false)
                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();


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
                if (location != null && mMap != null) {
                    String url = makeURL(location.getLatitude(), location.getLongitude(), 45.4871763, 12.291384);   //google json from current location to chiesa di campalto
                    Log.d(TAG, url);


                    mMap.clear();

                    DownloadTask downloadTask = new DownloadTask(HomeFragment.this);
                    // Start downloading json data from Google Directions API
                    downloadTask.execute(url);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(15));

                }
            }
        });
    }

    private void beginAuth() {

        //g;tting current account data
        mAuth=((Home)getActivity()).getAuth();
        if(mAuth.getCurrentUser()!= null) {
            userId = mAuth.getCurrentUser().getUid();
            //getting databse from activity
            mDatabase = ((Home) getActivity()).getDB();
            // mDatabase.child("Parking").child(userId+"ciaoooooooooo").child("test").setValue("scritta222");

            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String s = dataSnapshot.child("Parking").child(userId).child("test").getValue().toString();
                    Log.d(TAG, "onDataChange: " + s);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

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
        mMap.setMapType(mMap.MAP_TYPE_HYBRID);

        if (location!=null){
            LatLng you=new LatLng(location.getLatitude(),location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(you));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        }
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
    }



    public void getLocation(){
        myLocListener = new myLocationListener(HomeFragment.this);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        //check if the gps is enabled

        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            AlertDialog.Builder builder =new AlertDialog.Builder(getContext());
            builder.setMessage("GPS down, do you want to enable it?")
                    .setCancelable(false)
                    .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            Criteria criteria = new Criteria();
                            bestProvider = locationManager.getBestProvider(criteria, false);

                            try {
                                location = locationManager.getLastKnownLocation(bestProvider);
                                locationManager.requestLocationUpdates(bestProvider, minTime, minDistance, myLocListener);


                            }
                            catch (NullPointerException e){
                                e.printStackTrace();

                            }
                            catch(SecurityException sEx){
                                sEx.printStackTrace();

                            }
                        }
                    })
                    .setNegativeButton("no", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            getActivity().finish();
                        }
                    });
            AlertDialog alert=builder.create();
            alert.show();
        }else{

            Criteria criteria = new Criteria();
            bestProvider = locationManager.getBestProvider(criteria, false);

            try {
                location = locationManager.getLastKnownLocation(bestProvider);
                locationManager.requestLocationUpdates(bestProvider, minTime, minDistance, myLocListener);


            }
            catch (NullPointerException e){
                e.printStackTrace();

            }
            catch(SecurityException sEx){
                sEx.printStackTrace();

            }

        }




    }



    public String makeURL (double sourcelat, double sourcelog, double destlat, double destlog ){
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append(Double.toString(sourcelat));
        urlString.append(",");
        urlString
                .append(Double.toString( sourcelog));
        urlString.append("&destination=");// to
        urlString
                .append(Double.toString( destlat));
        urlString.append(",");
        urlString.append(Double.toString(destlog));
        urlString.append("&sensor=false&mode=walking&alternatives=true");
        String apiKey="AIzaSyDcRarWNqsbymt_SHnfwQceOrlOeJq7U1g";
        urlString.append("&key="+apiKey);
        return urlString.toString();
    }

    public GoogleMap getMap(){
        return mMap;
    }
    public TextView getLocationText(){return locationText;}
    public void setLocation(Location location) {
        this.location = location;
    }
}
