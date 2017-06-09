package fabiomanfrin.carfinder;

import android.app.Fragment;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
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
    private HomeFragment hf;
    private MapsFragment mf;
    private Home h;
    private GoogleMap mMap;
    private ProgressDialogFragment dialog;
    private String Distance;
    private String Duration;
    /*public DownloadTask(Fragment f){
        this.f=f;
    }*/
    public DownloadTask(Home home,GoogleMap map){
        mMap=map;
        h=home;
    }
    public DownloadTask(Home home,GoogleMap map,HomeFragment hf){
        h=home;
        mMap=map;
        this.hf=hf;
    }
    public DownloadTask(Home home,GoogleMap map,MapsFragment mf){
        h=home;
        mMap=map;
        this.mf=mf;
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
        getPathInfo(result);

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


    private void getPathInfo(String result) {
        String dur = "";
        String dis = "";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(result);
            JSONArray array = jsonObject.getJSONArray("routes");
            JSONObject routes = array.getJSONObject(0);
            JSONArray legs = routes.getJSONArray("legs");
            JSONObject steps = legs.getJSONObject(0);
            JSONObject distance = steps.getJSONObject("distance");
            JSONObject duration = steps.getJSONObject("duration");
            dis= distance.getString("text");
            dur = duration.getString("text");
            Distance=dis;
            Duration=dur;

            if(hf!=null){
                TextView t= (TextView) hf.getActivity().findViewById(R.id.infoPath_text);
                t.setText(Duration+", "+Distance);
            }else if(mf!=null){
                TextView t= (TextView) mf.getActivity().findViewById(R.id.infoPathMaps_text);
                t.setText(Duration+", "+Distance);
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }


}



