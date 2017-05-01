package fabiomanfrin.carfinder;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by fabio on 4/16/17.
 */

public class myLocationListener implements LocationListener {

    private TextView t; //testo per veder le coordinate solo provvisorio

    public myLocationListener(TextView t){
        this.t=t;

    }


    @Override
    public void onLocationChanged(Location location) {
        if (location != null)
        {

            // Do something knowing the location changed by the distance you requested
            //methodThatDoesSomethingWithNewLocation(location);
            LatLng currentLocation=new LatLng(location.getLatitude(),location.getLongitude());
            t.setText(currentLocation.toString());

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
