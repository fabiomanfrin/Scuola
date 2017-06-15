package fabiomanfrin.carfinder;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
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
    private long minTime =1 * 10 * 1000; //30 seconds
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
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String userId;
    private Double selectedLat;
    private Double selectedLng;
    private ArrayList<Parking> car_parkings;
    private ArrayList<ParkingsPlace> listParkings;
    private ArrayList<String> spinnerItem;
    private ArrayAdapter<String> arrayAdapter;
    private TextView descriptionText;



    //view
    private boolean isSpinnerTouched = false;

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
        startLocationUpdates();
        initMap();
        if (location != null) {
            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        }
       Button add_button= (Button) getActivity().findViewById(R.id.addParking_button);
       add_button.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Bundle b = new Bundle();
               if (location == null) {
                   Snackbar.make(v, "Unable to add a new car parking cause current location missing", Snackbar.LENGTH_LONG).setAction("Action", null).show();

               } else {
                   b.putDouble("lat", location.getLatitude());
                   b.putDouble("lng", location.getLongitude());
                   addParkingFragment ap = new addParkingFragment();
                   ap.setArguments(b);
                   ((Home) getActivity()).replacefragment(ap);  // replace fragment in fragment layout with a addParking Fragment

               }
           }
       });


        Button path_button= (Button) getActivity().findViewById(R.id.path_button);
        path_button.setOnClickListener(new View.OnClickListener() {
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


                    String selectedP = spinner.getSelectedItem().toString();
                    Log.d(TAG, "onItemSelected: " + selectedP);
                    Parking p;
                    for (Parking parking : car_parkings) {

                        if (parking.getTitle().equals(selectedP)) {
                            p = parking;

                            selectedLat = p.getLat();
                            selectedLng = p.getLng();
                            LatLng selectedLatLng = new LatLng(selectedLat, selectedLng);
                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                    .target(selectedLatLng)
                                    .zoom(13)                   // Sets the zoom
                                    .build();                   // Creates a CameraPosition from the builder
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                            String description = p.getDescription();
                            String subDes = description.length() < 50 ? description : description.substring(0, 49) + "...";
                            descriptionText.setText(subDes);
                            break;
                        }

                    }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        mAuthListener=new FirebaseAuth.AuthStateListener(){


            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(FirebaseAuth.getInstance().getCurrentUser()==null) {
                    spinnerItem.clear();
                    spinnerItem.add("log in to get this function");
                    arrayAdapter.notifyDataSetChanged();
                    descriptionText.setText("");
                    if(mMap!=null){
                        mMap.clear();
                    }
                }
            }
        };




    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    private void beginAuth() {

        //getting current account data
        mAuth=((Home)getActivity()).getAuth();
        if(mAuth.getCurrentUser()!= null) { 
            userId = mAuth.getCurrentUser().getUid();
            //getting databse from activity


            mDatabase = ((Home) getActivity()).getDB().child("Users");
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final DataSnapshot parkingsSnap=dataSnapshot.child(userId).child("Parkings");
                    if(parkingsSnap.getValue()==null) {
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

                    final DataSnapshot parkingsPlaceSnap=dataSnapshot.child("ParkingsPlace");
                    if(parkingsPlaceSnap.getValue()!=null) {
                        listParkings=((Home)getActivity()).getListParkingsPlace();
                        if (mMap != null) {
                            loadParkingsPlace();
                        }
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


    private void loadParkings() {

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
    private void loadParkingsPlace() {
        Log.d(TAG, "loadParkingsPlace: dentro place");
        Double Lat;
        Double Lng;
        String title;
        for (int i = 0; i < listParkings.size(); i++) {
            Log.d(TAG, "loadParkingsPlace: dentro for place");
            title = listParkings.get(i).getTitle();
            String coord = listParkings.get(i).getCoordinates();
            String[] parts = coord.split(",");
            String part1 = parts[0];
            String part2 = parts[1];
            Lat = Double.parseDouble(part1);
            Lng = Double.parseDouble(part2);
            LatLng myLatLng = new LatLng(Lat, Lng);
            mMap.addMarker(new MarkerOptions()
                    .position(myLatLng)
                    .title(title)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_local_parking_black_24dp))
                    );
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
        listParkings=((Home)getActivity()).getListParkingsPlace();
        Log.d(TAG, "onMapReady: listParking con oggetti"+car_parkings.size());


    }



    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        if (location!=null){
            LatLng you=new LatLng(location.getLatitude(),location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(you));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(13));
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
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLatitude, currentLongitude), 15));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
            }


        if(car_parkings.size()!=0) {
            loadParkings();
        }
        if(listParkings.size()!=0) {
            Log.d(TAG, "onMapReady: listParking con oggetti");
            loadParkingsPlace();
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
                for(int i=0;i<listParkings.size();i++){
                    if(listParkings.get(i).getTitle().equals(marker.getTitle())){
                        d=listParkings.get(i).getDescription();
                        break;
                    }
                }
                desc.setText(d);


                return v;

            }
        });

    }


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
                    ImageView locIcon= (ImageView) getActivity().findViewById(R.id.location_icon);
                    locIcon.setImageResource(R.drawable.ic_gps_fixed_black_24dp);
                    setLocation(location);

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
            DownloadTask downloadTask = new DownloadTask((Home)getActivity(),mMap,this);
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
