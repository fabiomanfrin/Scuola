package fabiomanfrin.carfinder;


import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ModifyFragment extends Fragment {


    private ArrayList<Parking> car_parkings;
    private Parking p;
    private TextView title;
    private TextView desc;
    private Button cancel;
    private Button modify;
    private Button remove;
    private String UserId;
    private DatabaseReference ref;

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
        title= (TextView) getActivity().findViewById(R.id.title_text);
        desc= (TextView) getActivity().findViewById(R.id.description_text);
        cancel= (Button) getActivity().findViewById(R.id.cancelModify_button);
        remove= (Button) getActivity().findViewById(R.id.remove_button);
        modify= (Button) getActivity().findViewById(R.id.modify_button);

        title.setText(p.getTitle());
        desc.setText(p.getDescription());

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
    }
}
