package fabiomanfrin.carfinder;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;

import java.util.ArrayList;


/**
 *
 * Fragment che permette l'aggiunta di un parcheggio al dp
 *
 */
public class addParkingFragment extends Fragment {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private ArrayList<Parking> car_parkings;
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
                if(car_parkings!=null) {
                    if(!titleAlreadyExists(title.getText().toString())) {
                        Log.d(TAG, "onClick: parking added");
                        DatabaseReference ref = mDatabase.child("Users").child(mAuth.getCurrentUser().getUid()).child("Parkings").child(title.getText().toString());
                        ref.runTransaction(new Transaction.Handler() {
                            @Override
                            public Transaction.Result doTransaction(MutableData mutableData) {
                                mutableData.child("Description").setValue(description.getText() + "");
                                mutableData.child("Coordinates").child("Lat").setValue(lat);
                                mutableData.child("Coordinates").child("Lng").setValue(lng);
                                return Transaction.success(mutableData);
                            }

                            @Override
                            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                                ((Home) getActivity()).replacefragment(new HomeFragment());
                            }
                        });
                    }
                    else{
                        title.setTextColor(Color.RED);
                        Toast.makeText(getActivity(), "Name already exists", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Home)getActivity()).replacefragment(new HomeFragment());
            }
        });


    }

    private boolean titleAlreadyExists(String s) {
        boolean exists=false;
        for(Parking p:car_parkings){
            if(p.getTitle().equals(s)){
                exists=true;
                break;
            }
        }
        return exists;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        car_parkings=((Home)getActivity()).getListParkings();
    }
}
