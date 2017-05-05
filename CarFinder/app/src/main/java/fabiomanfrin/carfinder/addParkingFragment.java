package fabiomanfrin.carfinder;


import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;


/**
 * A simple {@link Fragment} subclass.
 */
public class addParkingFragment extends Fragment {


    public addParkingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_addparking, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText editText=(EditText)getActivity().findViewById(R.id.ParkingName_EditText);
        TextView text=(TextView)getActivity().findViewById(R.id.coordinates_text);
        Button btnSave=(Button)getActivity().findViewById(R.id.SaveParking_button);
        Button btnCancel=(Button)getActivity().findViewById(R.id.cancel_button);
        Bundle b=getArguments();
        Double lat=b.getDouble("lat");
        Double lng=b.getDouble("lng");
        LatLng latLng=new LatLng(lat,lng);
        text.setText(latLng.toString());


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "parcheggio aggiunto", Toast.LENGTH_SHORT).show();
                ((Home)getActivity()).replacefragment(new HomeFragment());
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "cancel", Toast.LENGTH_SHORT).show();
                ((Home)getActivity()).replacefragment(new HomeFragment());
            }
        });

    }
}
