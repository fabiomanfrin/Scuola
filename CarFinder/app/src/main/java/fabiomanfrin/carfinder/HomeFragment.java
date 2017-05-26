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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
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
import java.util.Map;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements OnMapReadyCallback {

    String TAG = "myTAG";
    private MapFragment mapFragment;
    private GoogleMap mMap;
    private long minTime =1 * 10 * 1000; //10 seconds
    private float minDistance = 0;   //0 meters
    private Spinner spinner;
    private TextView locationText;
    private Location location;
    private LocationManager locationManager;
    private String bestProvider;
    private myLocationListener myLocListener;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String userId;
    private Double selectedLat;
    private Double selectedLng;
    private ArrayList<Parking> car_parkings;
    private ArrayList<String> spinnerItem;
    private ArrayAdapter<String> arrayAdapter;
    private TextView descriptionText;

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


        descriptionText= (TextView) getActivity().findViewById(R.id.descriptionText);
        spinner= (Spinner) getActivity().findViewById(R.id.spinner);
        car_parkings=new ArrayList<>();
        spinnerItem=new ArrayList<>();
        arrayAdapter= new ArrayAdapter<String>(getContext(),R.layout.spinner_item, spinnerItem);
        spinner.setAdapter(arrayAdapter);
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
                    Toast.makeText(getContext(), "Unable to add a new car parking cause current location missing", Toast.LENGTH_SHORT).show();


                } else {
                    b.putDouble("lat", location.getLatitude());
                    b.putDouble("lng", location.getLongitude());

                   /* TextView Coord=(TextView)getActivity().findViewById(R.id.coordinates_text);
                    Button btnSave=(Button)getActivity().findViewById(R.id.SaveParking_button);
                    Button btnCancel=(Button)getActivity().findViewById(R.id.cancel_button);

                    final Double lat=location.getLatitude();
                    final Double lng=location.getLongitude();
                    LatLng latLng=new LatLng(lat,lng);
                    Coord.setText(latLng.toString());

                    //mAuth=((Home)getActivity()).getAuth();
                    //mDatabase=((Home)getActivity()).getDB();
                    final EditText title= (EditText) getActivity().findViewById(R.id.ParkingName_EditText);
                    final EditText description= (EditText) getActivity().findViewById(R.id.Description_EditText);





                    btnSave.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //mDatabase.child("Users").child(mAuth.getCurrentUser().getUid()).child("Parkings").child(title.getText().toString()).child("Description").setValue(description.getText().toString());
                            mDatabase.child("Users").child(mAuth.getCurrentUser().getUid()).child("Parkings").child(title.getText().toString()).child("Coordinates").child("Lat").setValue(lat);
                            mDatabase.child("Users").child(mAuth.getCurrentUser().getUid()).child("Parkings").child(title.getText().toString()).child("Coordinates").child("Lng").setValue(lng);
                            Toast.makeText(getContext(), "parcheggio aggiunto", Toast.LENGTH_SHORT).show();
                            //((Home)getActivity()).replacefragment(new HomeFragment());
                        }
                    });
                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getActivity(), "cancel", Toast.LENGTH_SHORT).show();
                            ((Home)getActivity()).replacefragment(new HomeFragment());
                        }
                    });
                    */


                    addParkingFragment ap = new addParkingFragment();
                    ap.setArguments(b);
                    ((Home) getActivity()).replacefragment(ap);  // replace fragment in fragment layout with a addParking Fragment

                }

            }
        });

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             /*   if (location != null && mMap != null) {

                    String url = makeURL(location.getLatitude(), location.getLongitude(), selectedLat, selectedLng);   //google json from current location to chiesa di campalto
                    Log.d(TAG, url);


                    mMap.clear();

                    DownloadTask downloadTask = new DownloadTask(HomeFragment.this);
                    // Start downloading json data from Google Directions API
                    downloadTask.execute(url);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(15));

                }*/
             updatePath();
            }
        });






    }




    private void beginAuth() {

        //g;tting current account data
        mAuth=((Home)getActivity()).getAuth();
        //mAuth.getCurrentUser();
        if(mAuth.getCurrentUser()!= null) { 
            userId = mAuth.getCurrentUser().getUid();
            //getting databse from activity
            mDatabase = ((Home) getActivity()).getDB();
            //mDatabase.child("Users").child(userId).child("Parkings").child("park1").child("Coordinates").child("Lat").setValue("45");
            //mDatabase.child("Users").child(userId).child("Parkings").child("park1").child("Coordinates").child("Lng").setValue("12");
            Query parkings=mDatabase.child("Users").child(userId).child("Parkings");
            //spinner.setAdapter(arrayAdapter);


            
            
            
            //DatabaseReference parkingRef= FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("Parkings");
            parkings.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //selectedLat=Double.parseDouble(dataSnapshot.child("2").child("Coordinates").child("Lat").getValue().toString());
                    //selectedLng=Double.parseDouble(dataSnapshot.child("2").child("Coordinates").child("Lng").getValue().toString());
                    //String  p=dataSnapshot.child("Users").child(userId).child("Parkings").child("1").getKey();


                    final DataSnapshot mySnap=dataSnapshot;
                    if(dataSnapshot.getValue()==null){
                        //String [] empty=new String[]{"No car parking available"};
                        //title_parkings=new ArrayList<>();
                        spinnerItem.add("No car parkings available");
                        arrayAdapter.notifyDataSetChanged();
                        //ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, empty);
                        //spinner.setAdapter(arrayAdapter);
                        Log.d(TAG, "onDataChange: torna null0");
                    }else {
                        Log.d(TAG, "onDataChange: sto per fare getParkings");
                        getParkings((Map<String, Object>) dataSnapshot.getValue());

                        arrayAdapter.notifyDataSetChanged();
                        Log.d(TAG, "onDataChange: ok ha superato getpark");
                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                String selectedP=spinner.getSelectedItem().toString();
                                selectedLat=Double.parseDouble(mySnap.child(selectedP).child("Coordinates").child("Lat").getValue().toString());
                                selectedLng=Double.parseDouble(mySnap.child(selectedP).child("Coordinates").child("Lng").getValue().toString());
                                String description=mySnap.child(selectedP).child("Description").getValue().toString();
                                descriptionText.setText(description);
                                //Toast.makeText(getContext(), selectedLat.toString(), Toast.LGTH_SHORT).show();
                                updatePath();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                        Log.d(TAG, "onDataChange: " + spinnerItem.toString());

                        //selectedLat=Double.parseDouble(dataSnapshot.child("2").child("Coordinates").child("Lat").getValue().toString());
                        //selectedLng=Double.parseDouble(dataSnapshot.child("2").child("Coordinates").child("Lng").getValue().toString());

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else{
            spinnerItem.add("log in to get this function");
            arrayAdapter.notifyDataSetChanged();
        }

    }

    private void getParkings(Map<String,Object> parkings) {

        //car_parkings = new ArrayList<>();
        //spinnerItem=new ArrayList<>();
        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : parkings.entrySet()){

            Map singleParking = (Map) entry.getValue();
            Object description=singleParking.get("Description");
            Map coordinates=(Map)singleParking.get("Coordinates");
            String title=entry.getKey();

            if(coordinates==null || description==null){
                Log.d(TAG, "getParkings: coordinates or description null");
            }

            //Log.d(TAG, "getParkings: entarto quiiiiiiiiii"+singleParking.get("Description"));
            //Double Lat=Double.parseDouble(coordinates.get("Lat")+"");
            //car_parkings.add(new Parking(title,coordinates.get("Lat").toString(),coordinates.get("Lng").toString()));
            //Log.d(TAG, "getParkings: "+coordinates.get("Lat"));
            else if(coordinates.get("Lat")!=null && coordinates.get("Lng")!=null) {
                Log.d(TAG, "description: "+description);
                car_parkings.add(new Parking(title, Double.parseDouble(coordinates.get("Lat").toString()), Double.parseDouble(coordinates.get("Lng").toString()),description.toString()));
            }
            //Parking p=new Parking(title,Double.parseDouble(coordinates..get("Lat").toString()),Double.parseDouble(coordinates.get("Lng").toString()));
            //car_parkings.add(p);

            spinnerItem.add(title);
            Log.d(TAG, "getParkings: entarto quiiiiiiiiii1212121212121");

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
        //mMap.setMapType(mMap.MAP_TYPE_HYBRID);


        /*mMap.addMarker(new MarkerOptions()
                .position(new LatLng(45.4839915,12.2836396))
                .title("marker caso"));*/



        if (location!=null){
            LatLng you=new LatLng(location.getLatitude(),location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(you));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        }
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);


       /* for (int i=0;i<car_parkings.size();i++){

            String title=car_parkings.get(i).getTitle();
            Double Lat=car_parkings.get(i).getLat();
            Double Lng=car_parkings.get(i).getLng();
            LatLng myLatLng=new LatLng(Lat,Lng);
            mMap.addMarker(new MarkerOptions()
                    .position(myLatLng)
                    .title(title));
            Log.d(TAG, "mappa: "+title+myLatLng.toString());
        }*/
        

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

    public void updatePath(){
        if (location != null && mMap != null) {

            String url = makeURL(location.getLatitude(), location.getLongitude(), selectedLat, selectedLng);   //google json from current location to chiesa di campalto
            Log.d(TAG, url);


            mMap.clear();

            DownloadTask downloadTask = new DownloadTask(HomeFragment.this);
            // Start downloading json data from Google Directions API
            downloadTask.execute(url);
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
            //mMap.moveCamera(CameraUpdateFactory.zoomTo(15));

        }
    }

    public GoogleMap getMap(){
        return mMap;
    }
    public TextView getLocationText(){return locationText;}
    public void setLocation(Location location) {
        this.location = location;
    }

    public Double getSelectedLat() {
        return selectedLat;
    }

    public Double getSelectedLng() {
        return selectedLng;
    }

    public ArrayList<Parking> getCarParkings() {
        return car_parkings;
    }
}
