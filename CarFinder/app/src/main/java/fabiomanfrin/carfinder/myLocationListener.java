package fabiomanfrin.carfinder;

import android.app.Fragment;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by fabio on 4/16/17.
 */

public class myLocationListener implements LocationListener {

    private TextView t; //testo per veder le coordinate solo provvisorio
    private String TAG="myTAG";
    private HomeFragment hf;
    public myLocationListener( Fragment f){
        hf=(HomeFragment)f;
        t=hf.getLocationText();

    }


    @Override
    public void onLocationChanged(Location location) {
        GoogleMap mMap=hf.getMap();

        if (location != null)
        {

            // Do something knowing the location changed by the distance you requested
            //methodThatDoesSomethingWithNewLocation(location);
            LatLng currentLocation=new LatLng(location.getLatitude(),location.getLongitude());
            t.setText(currentLocation.toString());
            Log.d(TAG, "onLocationChanged: "+currentLocation.toString());



            if(mMap!=null && hf.getSelectedLat()!=null && hf.getSelectedLng()!=null){
                //qui ogni volta che richiede la posizione ricarica la strada sulla mappa
                //da implementare
                String url=hf.makeURL(location.getLatitude(), location.getLongitude(), hf.getSelectedLat(), hf.getSelectedLng());
                mMap.clear();

                DownloadTask downloadTask = new DownloadTask(hf);
                // Start downloading json data from Google Directions API
                downloadTask.execute(url);

               /* mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(),location.getLongitude())));
                mMap.moveCamera(CameraUpdateFactory.zoomTo(15));*/

            }

            hf.setLocation(location);

        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // Do something here if you would like to know when the provider status changes
    }

    @Override
    public void onProviderEnabled(String provider) {

        // Do something here if you would like to know when the provider is enabled by the   user
    }

    @Override
    public void onProviderDisabled(String provider) {
        // Do something here if you would like to know when the provider is disabled by the user
    }
}
