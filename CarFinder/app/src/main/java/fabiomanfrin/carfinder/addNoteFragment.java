package fabiomanfrin.carfinder;


import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;


/**
 *
 * Fragment che permette l'aggiunta di una nuova nota sul db
 *
 */
public class addNoteFragment extends Fragment {

    private DatabaseReference ref;
    private String userID;

    public addNoteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_addnote, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ref=((Home)getActivity()).getDB();
        userID=((Home)getActivity()).getAuth().getCurrentUser().getUid();
        final EditText note= (EditText) getActivity().findViewById(R.id.note_editText);
        Button save= (Button) getActivity().findViewById(R.id.save_button);
        Button cancel= (Button) getActivity().findViewById(R.id.cancel_button);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference noteRef = ref.child("Users").child(userID).child("Notes").push();
                noteRef.runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        mutableData.setValue(note.getText().toString());
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                        Toast.makeText(getActivity(), "Note added", Toast.LENGTH_SHORT).show();
                        ((Home)getActivity()).replacefragment(new NotesFragment());
                    }
                });
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Home)getActivity()).replacefragment(new NotesFragment());
            }
        });
    }
}
