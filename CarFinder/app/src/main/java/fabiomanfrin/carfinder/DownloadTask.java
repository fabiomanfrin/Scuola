package fabiomanfrin.carfinder;

import android.app.Fragment;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Fabio on 11/05/2017.
 */

public class DownloadTask extends AsyncTask<String, Void, String> {

    String TAG="download";
    Fragment f;
    private Home h;
    private GoogleMap mMap;
    private ProgressDialogFragment dialog;
    /*public DownloadTask(Fragment f){
        this.f=f;
    }*/
    public DownloadTask(Home home,GoogleMap map){
        mMap=map;
        h=home;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog=new ProgressDialogFragment();
        dialog.show(h.getFragmentManager(),"TAG");

    }

    // Downloading data in non-ui thread
    @Override
    protected String doInBackground(String... url) {

        // For storing data from web service
        String data = "";

        try {
            // Fetching the data from web service
            data = downloadUrl(url[0]);
            Log.d(TAG, "doInBackground: "+data);
        } catch (Exception e) {
            Log.d("TAG", e.toString());
        }
        return data;
    }

    // Executes in UI thread, after the execution of
    // doInBackground()
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        ParserTask parserTask = new ParserTask(h,mMap);

        // Invokes the thread for parsing the JSON data
        parserTask.execute(result);

        Double distance=getDistanceInfo(result);
        String duration=getDurationInfo(result);
        Toast.makeText(h, "onPostExecute: "+distance+" "+duration, Toast.LENGTH_SHORT).show();
    }

    /**
     * A method to download json data from url
     */
    public String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("TAG", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }

        dialog.dismiss();
        return data;
    }


    private double getDistanceInfo(String result) {
        Double dist = 0.0;
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject = new JSONObject(result);

            JSONArray array = jsonObject.getJSONArray("routes");

            JSONObject routes = array.getJSONObject(0);

            JSONArray legs = routes.getJSONArray("legs");

            JSONObject steps = legs.getJSONObject(0);

            JSONObject distance = steps.getJSONObject("distance");

            Log.i("Distance", distance.toString());
            dist = Double.parseDouble(distance.getString("text").replaceAll("[^\\.0123456789]","") );

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return dist;
    }

    private String getDurationInfo(String result) {
        String dur = "";
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject = new JSONObject(result);

            JSONArray array = jsonObject.getJSONArray("routes");

            JSONObject routes = array.getJSONObject(0);

            JSONArray legs = routes.getJSONArray("legs");

            JSONObject steps = legs.getJSONObject(0);

            JSONObject distance = steps.getJSONObject("duration");

            Log.i("Distance", distance.toString());
            dur = distance.getString("text").replaceAll("[^\\.0123456789]","");

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return dur;
    }


}



