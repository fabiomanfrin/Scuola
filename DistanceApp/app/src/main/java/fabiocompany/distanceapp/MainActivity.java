package fabiocompany.distanceapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DownloadTask t=new DownloadTask(this);
        t.execute("http://maps.googleapis.com/maps/api/directions/json?origin=-20.291825,57.448668&destination=-20.179724,57.613463&sensor=false&mode=%22DRIVING%22");
    }

}
