package fabiomanfrin.carfinder;


import android.*;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import static android.content.Context.LOCATION_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapsFragment extends Fragment implements OnMapReadyCallback{

    private static final String TAG = "myMAP";
    private MapFragment mapFragment;
    private GoogleMap mMap;
    private ArrayList<Parking> car_parkings;
    
    //location variables
    private LocationListener locListener;
    private LocationManager locationManager;
    private String bestProvider;
    private Criteria mCriteria;
    private Location location;
    private Double selectedLat;
    private Double selectedLng;


    public MapsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initMap();
        car_parkings=((Home)getActivity()).getListParkings();
        getLocation();

    }

    private void initMap() {
        if (mMap == null) {
            mapFragment=(MapFragment) getChildFragmentManager().findFragmentById(R.id.map_full);
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        locationManager.removeUpdates(locListener);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        /*if (location!=null){
            LatLng you=new LatLng(location.getLatitude(),location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(you));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        }*/
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);

        //move the camera to the last known location
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

        //setMarkerListener();
        setInfoWindowListener();


    }

    public void setInfoWindowListener(){
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                LatLng position=marker.getPosition();
                selectedLat=position.latitude;
                selectedLng=position.longitude;
                updatePath();
            }
        });
    }

    public void setInfoWindow(){
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View v=getActivity().getLayoutInflater().inflate(R.layout.info_window,null);
                TextView title= (TextView) getActivity().findViewById(R.id.titleMarker_text);
                TextView draw= (TextView) getActivity().findViewById(R.id.startDraw_text);
                title.setText(marker.getTitle());
                draw.setText("draw path");

                return v;
            }
        });
    }

    private void setMarkerListener() {
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                LatLng position=marker.getPosition();
                selectedLat=position.latitude;
                selectedLng=position.longitude;
                updatePath();
                return true;
            }
        });
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
            Log.d(TAG, "load parkings: " + title + myLatLng.toString());
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

    public void getLocation() {
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
        locationManager = (LocationManager)getActivity().getSystemService(LOCATION_SERVICE);

        //getting the last known location
        mCriteria = new Criteria();
        bestProvider = String.valueOf(locationManager.getBestProvider(mCriteria, true));
        location = locationManager.getLastKnownLocation(bestProvider);
        if (locationManager.isProviderEnabled(locationManager.GPS_PROVIDER)) {
            locListener=new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                    LatLng currentLocation=new LatLng(location.getLatitude(),location.getLongitude());
                    TextView t= (TextView) getActivity().findViewById(R.id.coord_text);
                    t.setText(currentLocation.toString());
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
            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 0, 0, locListener);
        }
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
