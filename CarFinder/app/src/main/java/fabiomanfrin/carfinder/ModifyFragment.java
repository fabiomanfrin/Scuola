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

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Home)getActivity()).replacefragment(new EditFragment());
            }
        });
    }
}