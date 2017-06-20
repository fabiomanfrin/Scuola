package fabiomanfrin.carfinder;


import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.ArrayList;


/**
 *
 * Fragment richiamato dall'edit per modificare il parcheggio scelto
 *
 */
public class ModifyFragment extends Fragment {


    private static final int MAP_CODE =101;
    private ArrayList<Parking> car_parkings;
    private Parking p;
    private EditText title;
    private EditText desc;
    private Button cancel;
    private Button modify;
    private Button remove;
    private FloatingActionButton map_button;
    private String UserId;
    private DatabaseReference ref;
    private LatLng modifiedCoord;

    public ModifyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_modify, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        car_parkings=((Home)getActivity()).getListParkings();
        Bundle b=getArguments();
        int position=b.getInt("position");
        p=car_parkings.get(position);
        UserId=((Home)getActivity()).getAuth().getCurrentUser().getUid();
        ref=((Home)getActivity()).getDB().child("Users").child(UserId).child("Parkings");

        initViewListener();

    }

    private void initViewListener() {
        title= (EditText) getActivity().findViewById(R.id.editTitle_text);
        desc= (EditText) getActivity().findViewById(R.id.editDescription_text);
        cancel= (Button) getActivity().findViewById(R.id.cancelModify_button);
        remove= (Button) getActivity().findViewById(R.id.remove_button);
        modify= (Button) getActivity().findViewById(R.id.modify_button);
        map_button= (FloatingActionButton) getActivity().findViewById(R.id.map_fab);

        title.setText(p.getTitle());
        desc.setText(p.getDescription());

        modifiedCoord=new LatLng(p.getLat(),p.getLng());

        map_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b=new Bundle();
                b.putDouble("lat",p.getLat());
                b.putDouble("lng",p.getLng());
                b.putString("title",p.getTitle());
                Intent i=new Intent(getActivity(),MapsActivity.class);
                i.putExtras(b);
                startActivityForResult(i,MAP_CODE);
            }
        });

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref.runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        mutableData.child(p.getTitle()).setValue(null);
                        ((Home)getActivity()).removeParking(p.getTitle());
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                        Toast.makeText(getActivity(), "Parking removed", Toast.LENGTH_SHORT).show();
                        ((Home)getActivity()).replacefragment(new EditFragment());
                    }
                });
               /* ref.child(p.getTitle()).removeValue();
                ((Home)getActivity()).removeParking(p.getTitle());*/
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Home)getActivity()).replacefragment(new EditFragment());
            }
        });

        modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(p.getTitle().equals(title.getText().toString()) && p.getDescription().equals(desc.getText().toString()) && modifiedCoord.latitude==p.getLat() && modifiedCoord.longitude == p.getLng()){  //niente cambia
                    ((Home)getActivity()).replacefragment(new EditFragment());
                }
                else {
                    //Toast.makeText(getActivity(), "Title changed", Toast.LENGTH_SHORT).show();

                    ref.runTransaction(new Transaction.Handler() {
                        @Override
                        public Transaction.Result doTransaction(MutableData mutableData) {
                            if(!p.getTitle().equals(title.getText().toString())) {
                                mutableData.child(p.getTitle()).setValue(null);
                                ((Home) getActivity()).removeParking(p.getTitle());
                            }
                            ((Home)getActivity()).updateParking(title.getText().toString(),desc.getText().toString(),modifiedCoord.latitude,modifiedCoord.longitude);
                            mutableData.child(title.getText().toString()).child("Description").setValue(desc.getText().toString());
                            mutableData.child(title.getText().toString()).child("Coordinates").child("Lat").setValue(modifiedCoord.latitude);
                            mutableData.child(title.getText().toString()).child("Coordinates").child("Lng").setValue(modifiedCoord.longitude);

                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                            Toast.makeText(getActivity(), "Parking modified", Toast.LENGTH_SHORT).show();
                            ((Home)getActivity()).replacefragment(new EditFragment());
                        }
                    });

                }

            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check which request we're responding to
        if (requestCode == MAP_CODE) {
            // Make sure the request was successful
            if (resultCode == getActivity().RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.

                // Do something with the contact here (bigger example below)
                Bundle bundle=data.getExtras();
                modifiedCoord=new LatLng(bundle.getDouble("lat"),bundle.getDouble("lng"));
                //Toast.makeText(getActivity(), modifiedCoord.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
