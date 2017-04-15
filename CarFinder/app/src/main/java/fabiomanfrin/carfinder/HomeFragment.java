package fabiomanfrin.carfinder;


import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private SupportMapFragment sMapFragment;
    private GoogleMap mMap;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_fragment, container, false);



    }

    public void initMap() {

        sMapFragment=SupportMapFragment.newInstance();
        //sMapFragment=(SupportMapFragment) sMapFragment.getChildFragmentManager().findFragmentById(R.id.map);
        sMapFragment.getMapAsync(this);

        /*if(mMap==null){
            sMapFragment=(SupportMapFragment) sMapFragment.getChildFragmentManager().findFragmentById(R.id.map);
            sMapFragment.getMapAsync(this);
        }*/

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initMap();


/***at this time google play services are not initialize so get map and add what ever you want to it in onResume() or onStart() **/
    }

    @Override
    public void onStart() {
        super.onStart();
        android.app.FragmentManager fm=getFragmentManager();
        fm.beginTransaction().add(R.id.map,sMapFragment).commit();
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
