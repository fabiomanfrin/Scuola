package fabiomanfrin.carfinder;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * Created by fabio on 5/18/17.
 */

public class InternetConnessionChecker {
    private boolean isAvailable;
    private Context context;

    public InternetConnessionChecker(Context context) {

        this.context = context;
    }

    private void haveNetworkConnection(Context context) {
        isAvailable = false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                isAvailable = true;
                Toast.makeText(context, activeNetwork.getTypeName(), Toast.LENGTH_SHORT).show();
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                isAvailable = true;
                Toast.makeText(context, activeNetwork.getTypeName(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean isConnectionAvailable() {
        haveNetworkConnection(context);
        return isAvailable;
    }

}
