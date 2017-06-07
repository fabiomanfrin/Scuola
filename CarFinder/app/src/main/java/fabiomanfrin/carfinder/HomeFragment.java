package fabiomanfrin.carfinder;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

import static android.content.Context.LOCATION_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements OnMapReadyCallback {

    String TAG = "myHFragment";
    private MapFragment mapFragment;
    private GoogleMap mMap;
    private long minTime =1 * 30 * 1000; //30 seconds
    private float minDistance = 25;   //25 meters
    private Spinner spinner;

    //location variables
    private TextView locationText;
    private Location location;
    private LocationManager locationManager;
    private LocationListener locListener;
    private String bestProvider;
    private Criteria mCriteria;

    //firebase variables
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
    public void onPause() {
        locationManager.removeUpdates(locListener);
        super.onPause();

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
        startLocationUpdates();
        //getLocation();
        initMap();


        Button b = (Button) getActivity().findViewById(R.id.refresh_button);





        if (location != null) {
            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        }
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.addParking_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle b = new Bundle();
                if (location == null) {
                    //Toast.makeText(getContext(), "Unable to add a new car parking cause current location missing", Toast.LENGTH_SHORT).show();
                    Snackbar.make(view, "Unable to add a new car parking cause current location missing", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                } else {
                    b.putDouble("lat", location.getLatitude());
                    b.putDouble("lng", location.getLongitude());
                    addParkingFragment ap = new addParkingFragment();
                    ap.setArguments(b);
                    ((Home) getActivity()).replacefragment(ap);  // replace fragment in fragment layout with a addParking Fragment

                }

            }
        });

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 Snackbar.make(v, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        FloatingActionButton drawPath_fab = (FloatingActionButton) getActivity().findViewById(R.id.drawPath_fab);
        drawPath_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spinnerItem.size()!=0 && selectedLat!=null && selectedLng!=null) {
                    updatePath();
                }
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedP=spinner.getSelectedItem().toString();
                Log.d(TAG, "onItemSelected: "+selectedP);
                Parking p;
                for (Parking parking:car_parkings){

                    if(parking.getTitle().equals(selectedP)){
                        p=parking;
                        selectedLat=p.getLat();
                        selectedLng=p.getLng();
                        String description=p.getDescription();
                        String subDes=description.length()<50?description:description.substring(0,49)+"...";
                        descriptionText.setText(subDes);
                        break;
                    }

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }




    private void beginAuth() {

        //getting current account data
        mAuth=((Home)getActivity()).getAuth();
        //mAuth.getCurrentUser();
        if(mAuth.getCurrentUser()!= null) { 
            userId = mAuth.getCurrentUser().getUid();
            //getting databse from activity
            mDatabase = ((Home) getActivity()).getDB();


            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final DataSnapshot mySnap=dataSnapshot;
                    if(dataSnapshot.getValue()==null) {
                        spinnerItem.add("No car parkings available");
                        arrayAdapter.notifyDataSetChanged();
                        Log.d(TAG, " torna null0");
                    }
                    else{
                        Log.d(TAG, " sto per fare getParkings");
                        for (Parking p : car_parkings) {
                            spinnerItem.add(p.getTitle());
                        }

                        if (mMap != null) {
                            loadParkings();
                        }

                        arrayAdapter.notifyDataSetChanged();

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

          /*  mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //spinnerItem=new ArrayList<>();
                    //car_parkings=((Home)getActivity()).getListParkings();
                    final DataSnapshot mySnap=dataSnapshot;
                    if(dataSnapshot.getValue()==null) {
                        spinnerItem.add("No car parkings available");
                        arrayAdapter.notifyDataSetChanged();
                        Log.d(TAG, " torna null0");
                    }
                    else{
                        Log.d(TAG, " sto per fare getParkings");
                        for (Parking p : car_parkings) {
                            spinnerItem.add(p.getTitle());
                        }

                        if (mMap != null) {
                            loadParkings();
                        }

                        arrayAdapter.notifyDataSetChanged();

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });*/


          //////////////////////////////////////////////////////////////////////////////////

           /* Query parkings=mDatabase.child("Users").child(userId).child("Parkings");
            parkings.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final DataSnapshot mySnap=dataSnapshot;
                    if(dataSnapshot.getValue()==null){
                        spinnerItem.add("No car parkings available");
                        arrayAdapter.notifyDataSetChanged();
                        Log.d(TAG, "onDataChange: torna null0");
                    }else {
                        Log.d(TAG, "onDataChange: sto per fare getParkings");
                        //getParkings((Map<String, Object>) dataSnapshot.getValue());
                        car_parkings=((Home)getActivity()).getListParkings();
                        if(car_parkings!=null) {
                            for (Parking p : car_parkings) {
                                spinnerItem.add(p.getTitle());
                            }

                            if (mMap != null) {
                                loadParkings();
                            }
                        }else
                        {
                            Log.d(TAG, "onDataChange: car_p è nullo");
                        }
                        arrayAdapter.notifyDataSetChanged();
                        Log.d(TAG, "onDataChange: ok ha superato getpark");
                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                String selectedP=spinner.getSelectedItem().toString();
                                selectedLat=Double.parseDouble(mySnap.child(selectedP).child("Coordinates").child("Lat").getValue().toString());
                                selectedLng=Double.parseDouble(mySnap.child(selectedP).child("Coordinates").child("Lng").getValue().toString());
                                String description=mySnap.child(selectedP).child("Description").getValue().toString();
                                String subDes=description.length()<20?description:description.substring(0,19)+"...";
                                descriptionText.setText(subDes);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                        Log.d(TAG, "onDataChange: " + spinnerItem.toString());

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });*/
        }
        else{
            spinnerItem.add("log in to get this function");
            arrayAdapter.notifyDataSetChanged();
        }

    }


    private void getParkings(Map<String,Object> parkings) {

        for (Map.Entry<String, Object> entry : parkings.entrySet()){

            Map singleParking = (Map) entry.getValue();
            Object description=singleParking.get("Description");
            Map coordinates=(Map)singleParking.get("Coordinates");
            String title=entry.getKey();

            if(coordinates==null || description==null){
                Log.d(TAG, "getParkings: coordinates or description null");
            }

            else if(coordinates.get("Lat")!=null && coordinates.get("Lng")!=null) {
                Log.d(TAG, "description: "+description);
                car_parkings.add(new Parking(title, Double.parseDouble(coordinates.get("Lat").toString()), Double.parseDouble(coordinates.get("Lng").toString()),description.toString()));
            }
            spinnerItem.add(title);
            Log.d(TAG, "getParkings: entarto quiiiiiiiiii1212121212121");


        }





    }

    private void loadParkings() {

        mMap.clear();
        Double Lat;
        Double Lng;
        String title;
        for (int i = 0; i < car_parkings.size(); i++) {

            title = car_parkings.get(i).getTitle();
            Lat = car_parkings.get(i).getLat();
            Lng = car_parkings.get(i).getLng();
            LatLng myLatLng = new LatLng(Lat, Lng);
            mMap.addMarker(new MarkerOptions()
                    .position(myLatLng)
                    .title(title)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_car_black_24dp)));
            Log.d(TAG, "mappa parser: " + title + myLatLng.toString());
        }
    }



    public void initMap() {
        if (mMap == null) {
            mapFragment=(MapFragment) getChildFragmentManager().findFragmentById(R.id.map_full);
            mapFragment.getMapAsync(this);
        }





    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


/***at this time google play services are not initialize so get map and add what ever you want to it in onResume() or onStart() **/

        car_parkings=((Home)getActivity()).getListParkings();


    }



    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        if (location!=null){
            LatLng you=new LatLng(location.getLatitude(),location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(you));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        }
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);

            if (location != null) {
               // Log.e("TAG", "GPS is on");
                final double currentLatitude = location.getLatitude();
                final double currentLongitude = location.getLongitude();
                LatLng loc1 = new LatLng(currentLatitude, currentLongitude);
                //mMap.addMarker(new MarkerOptions().position(loc1).title("Your Current Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLatitude, currentLongitude), 15));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
            }


        if(car_parkings.size()!=0) {
            loadParkings();
        }

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {

                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View v=getActivity().getLayoutInflater().inflate(R.layout.infowindow,null);
                TextView title= (TextView) v.findViewById(R.id.title_window);
                TextView desc= (TextView) v.findViewById(R.id.description_window);
                title.setText(marker.getTitle());
                String d="";
                for(int i=0;i<car_parkings.size();i++){
                    if(car_parkings.get(i).getTitle().equals(marker.getTitle())){
                        d=car_parkings.get(i).getDescription();
                        break;
                    }
                }
                desc.setText(d);


                return v;

            }
        });

    }



    /*public void getLocation(){
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




    }*/


    public void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        //getting the last known location
        mCriteria = new Criteria();
        bestProvider = String.valueOf(locationManager.getBestProvider(mCriteria, true));
        location = locationManager.getLastKnownLocation(bestProvider);

        if (locationManager.isProviderEnabled(locationManager.GPS_PROVIDER)) {
            locListener=new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                    LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    TextView t = (TextView) getActivity().findViewById(R.id.locationText);
                    //t.setText(currentLocation.toString());
                    t.setText("location");
                    t.setTextColor(Color.GREEN);
                    setLocation(location);
                    //Toast.makeText(MapsActivity.this, currentLocation.toString(), Toast.LENGTH_SHORT).show();
                    // mMap.addMarker(new MarkerOptions().position(currentLocation).title("You, "+currentLocation));
                    //mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };
            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, minTime, minDistance, locListener);
        }
    }





    public void updatePath(){
        if (location != null && mMap != null) {

            String url = ((Home)getActivity()).makeURL(location.getLatitude(), location.getLongitude(), selectedLat, selectedLng);   //google json from current location to chiesa di campalto
            Log.d(TAG, url);
            mMap.clear();
            DownloadTask downloadTask = new DownloadTask((Home)getActivity(),mMap);
            // Start downloading json data from Google Directions API
            downloadTask.execute(url);

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
