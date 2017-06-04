package fabiomanfrin.carfinder;


import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditFragment extends Fragment {


    private static final String TAG = "myEDIT";
    private ArrayList<Parking> car_parkings;
    public EditFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView l= (ListView) getActivity().findViewById(R.id.listView);

        // ArrayAdapter<Parking> adapter=new ArrayAdapter<Parking>(getActivity(),android.R.layout.simple_list_item_1,car_parkings);
        // l.setAdapter(adapter);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        car_parkings=((Home)getActivity()).getListParkings();
        ArrayList<String> support=new ArrayList<>();
        for(Parking p: car_parkings){
            Log.d(TAG, "onViewCreated: "+p.getTitle());
            support.add(p.getTitle());
        }
        ListView l= (ListView) getActivity().findViewById(R.id.listView);
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity(),R.layout.item_list,support);
        l.setAdapter(adapter);

    }
}
