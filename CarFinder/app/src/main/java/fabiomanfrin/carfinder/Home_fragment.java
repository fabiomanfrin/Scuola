package fabiomanfrin.carfinder;


import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * A simple {@link Fragment} subclass.
 */
public class Home_fragment extends Fragment implements OnMapReadyCallback{

    private SupportMapFragment sMapFragment;
    private GoogleMap mMap;

    public Home_fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragmen
        return inflater.inflate(R.layout.fragment_home_fragment, container, false);

       // MapFragment mapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        sMapFragment=sMapFragment.newInstance();
        sMapFragment.getMapAsync(this);
        getChildFragmentManager().beginTransaction().add(R.id.map,sMapFragment).commit();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        android.app.FragmentManager fm= getChildFragmentManager();
        //sMapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
        if (sMapFragment == null) {
            sMapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map,(Fragment) sMapFragment).commit();
        }


/***at this time google play services are not initialize so get map and add what ever you want to it in onResume() or onStart() **/
    }

    @Override
    public void onResume() {
        super.onResume();
        if (map == null) {
            map = fragment.getMap();
            map.addMarker(new MarkerOptions().position(new LatLng(0, 0)));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car_marker)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

    }
}
