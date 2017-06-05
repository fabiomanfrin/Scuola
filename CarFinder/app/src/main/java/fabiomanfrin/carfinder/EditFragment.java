package fabiomanfrin.carfinder;


import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

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

        View rootView = inflater.inflate(R.layout.fragment_edit, container, false);

        ArrayList<Parking> list = ((Home)getActivity()).getListParkings();
        ListView lv = (ListView)rootView.findViewById(R.id.listView);
        lv.setAdapter(new ListviewAdapter(getActivity(), list));

        return rootView;
    }

}

 class ListviewAdapter extends BaseAdapter {
    private static ArrayList<Parking> list;

    private LayoutInflater mInflater;

    public ListviewAdapter(Context Fragment, ArrayList<Parking> results){
        list = results;
        mInflater = LayoutInflater.from(Fragment);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return list.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder holder;
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.parkinglist_item, null);
            holder = new ViewHolder();
            holder.txtTitle = (TextView) convertView.findViewById(R.id.titleItem_text);
            holder.txtDescription = (TextView) convertView.findViewById(R.id.descriptionItem_text);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtTitle.setText(list.get(position).getTitle());
        holder.txtDescription.setText(list.get(position).getDescription());

        return convertView;
    }

    static class ViewHolder{
        TextView txtTitle, txtDescription;
    }
}
/*
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

}*/
