package fabiomanfrin.carfinder;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

/**
 * Created by fabio on 4/16/17.
 */

public class myLocationListener implements LocationListener {
    @Override
    public void onLocationChanged(Location location) {
        if (location != null)
        {
            // Do something knowing the location changed by the distance you requested
            //methodThatDoesSomethingWithNewLocation(location);
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
