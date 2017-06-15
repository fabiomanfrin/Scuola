package fabiomanfrin.carfinder;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

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
        car_parkings = ((Home)getActivity()).getListParkings();
        ListView lv = (ListView)rootView.findViewById(R.id.listView);
        lv.setAdapter(new ListviewAdapter(getActivity(), car_parkings));
        setItemListener(lv);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(((Home)getActivity()).getAuth().getCurrentUser()!=null) {
                    ModifyFragment m = new ModifyFragment();
                    Bundle b = new Bundle();
                    b.putInt("position", position);
                    m.setArguments(b);
                    ((Home) getActivity()).replacefragment(m);
                }
            }
        });

        return rootView;
    }


    public void setItemListener(ListView itemListener) {
        itemListener.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(((Home)getActivity()).getAuth()!=null) {
                    Toast.makeText(getActivity(), car_parkings.get(position).getTitle(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}

class ListviewAdapter extends ArrayAdapter<Parking> {

    private final Activity context;
    private ArrayList car_parkings;
    private static final String TAG = "ViewHolder";


    static class ViewHolder {
        public TextView title;
        public TextView description;
    }

    public ListviewAdapter(Activity context, ArrayList car_parkings) {
        super(context, R.layout.parkinglist_item, car_parkings);
        this.context = context;
        this.car_parkings = car_parkings;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        ViewHolder viewHolder;

        Parking p = (Parking) car_parkings.get(position);

        // reuse view: ViewHolder pattern
        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.parkinglist_item, null);
            // configure view holder
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) rowView.findViewById(R.id.titleItem_text);
            viewHolder.description = (TextView) rowView.findViewById(R.id.descriptionItem_text);
            // take memory of the view
            rowView.setTag(viewHolder);
            // Log steTag()
        } else {
            // reuse the object
            viewHolder = (ViewHolder) rowView.getTag();
            // Log steTag()
        }

        viewHolder.title.setText(p.getTitle());
        viewHolder.description.setText(p.getDescription().length()<40?p.getDescription():p.getDescription().substring(0,39)+"...");
        return rowView;
    }
}

