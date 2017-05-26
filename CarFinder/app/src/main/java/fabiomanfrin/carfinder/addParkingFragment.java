package fabiomanfrin.carfinder;


import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * A simple {@link Fragment} subclass.
 */
public class addParkingFragment extends Fragment {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private static final String TAG="myTAG";

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

        TextView text=(TextView)getActivity().findViewById(R.id.coordinates_text);
        Button btnSave=(Button)getActivity().findViewById(R.id.SaveParking_button);
        Button btnCancel=(Button)getActivity().findViewById(R.id.cancel_button);
        Bundle b=getArguments();
        final Double lat=b.getDouble("lat");
        final Double lng=b.getDouble("lng");
        LatLng latLng=new LatLng(lat,lng);
        text.setText(latLng.toString());

        mAuth=((Home)getActivity()).getAuth();
        mDatabase=((Home)getActivity()).getDB();

        final EditText title= (EditText) getActivity().findViewById(R.id.ParkingName_EditText);
        final EditText description= (EditText) getActivity().findViewById(R.id.Description_EditText);





        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mDatabase.child("Users").child(mAuth.getCurrentUser().getUid()).child("Parkings").child(title.getText().toString()).child("Description").setValue(description.getText().toString());
                Log.d(TAG, "onClick: parking added");
                mDatabase.child("Users").child(mAuth.getCurrentUser().getUid()).child("Parkings").child(title.getText().toString()).child("Description").setValue(description.getText()+"");
                mDatabase.child("Users").child(mAuth.getCurrentUser().getUid()).child("Parkings").child(title.getText().toString()).child("Coordinates").child("Lat").setValue(lat);
                mDatabase.child("Users").child(mAuth.getCurrentUser().getUid()).child("Parkings").child(title.getText().toString()).child("Coordinates").child("Lng").setValue(lng);

                ((Home)getActivity()).replacefragment(new HomeFragment());
                //Toast.makeText(getActivity(), "parcheggio aggiunto", Toast.LENGTH_SHORT).show();
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
