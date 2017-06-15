package fabiomanfrin.carfinder;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotesFragment extends Fragment {

    private static final String TAG = "NotesFragment";
    private ArrayList<String> notes;
    private DatabaseReference ref;
    private String userID;
    private boolean removable = false;
    private FirebaseAuth mAuth;

    public NotesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notes, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = ((Home) getActivity()).getAuth();
        if (mAuth.getCurrentUser() != null) {
            ref = ((Home) getActivity()).getDB();
            userID = ((Home) getActivity()).getAuth().getCurrentUser().getUid();
            notes = new ArrayList<>();
            CheckBox removeCheck = (CheckBox) getActivity().findViewById(R.id.remove_checkBox);
            removeCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        removable = true;
                    } else {
                        removable = false;
                    }
                }
            });


            ListView l = (ListView) getActivity().findViewById(R.id.listView);
            final ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(getActivity(), R.layout.item_list, notes); // android.R.layout.simple_list_item_1
            l.setAdapter(itemsAdapter);

            Query query = ref.child("Users").child(userID).child("Notes");
            query.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if (dataSnapshot.getValue() != null) {
                        String note = dataSnapshot.getValue(String.class);
                        notes.add(note);
                        itemsAdapter.notifyDataSetChanged();

                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        String note = dataSnapshot.getValue(String.class);
                        notes.remove(note);
                        itemsAdapter.notifyDataSetChanged();

                    }
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            Button add_note = (Button) getActivity().findViewById(R.id.addNote_button);
            add_note.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(FirebaseAuth.getInstance().getCurrentUser()==null){
                        Toast.makeText(getActivity(), "Log in to get this function", Toast.LENGTH_SHORT).show();
                    }else {
                        ((Home) getActivity()).replacefragment(new addNoteFragment());
                    }
                }
            });

            l.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (removable) {
                        final String note = notes.get(position);
                        Query noteQuery = ref.child("Users").child(userID).child("Notes");

                        noteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                                    //singleSnapshot.getRef().removeValue();
                                    if (singleSnapshot.getValue().equals(note)) {
                                        singleSnapshot.getRef().setValue(null);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e(TAG, "onCancelled", databaseError.toException());
                            }
                        });
                    }
                }
            });


        }
        else
        {
            Button add_note = (Button) getActivity().findViewById(R.id.addNote_button);
            add_note.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), "Log in to get this function", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}


