package fabiomanfrin.carfinder;

import android.*;
import android.Manifest;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.Polyline;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

/**
 *
 * Activity principale
 *
 */

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private FragmentManager fm;
    private boolean doubleBackToExitPressedOnce = false;
    private static final String TAG="myHOME";
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;

    private String email;
    private String name;
    private String userid;
    private Uri imageUri;

    private TextView username_text;
    private TextView email_text;
    private Button signIn_button;
    private ImageView google_image;
    private ImageView image;
    private CardView card;

    private ArrayList<Parking> car_parkings;
    private ArrayList<ParkingsPlace> listParkings;

    //variables Maps
    private Polyline polyline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        car_parkings=new ArrayList<>();
        listParkings=new ArrayList<>();
        polyline=null;

        //get authentication data
        mAuth=FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            email=mAuth.getCurrentUser().getEmail();
            name=mAuth.getCurrentUser().getDisplayName();
            userid=mAuth.getCurrentUser().getUid();
            imageUri=mAuth.getCurrentUser().getPhotoUrl();
            Log.d(TAG, "onCreate: "+email+" "+userid+" "+name);
            //get firebase database
            mDatabase = FirebaseDatabase.getInstance().getReference();
        }



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        // get view from header bar
        View header=navigationView.getHeaderView(0);
        username_text = (TextView)header.findViewById(R.id.username_text);
        email_text = (TextView)header.findViewById(R.id.email_text);
        signIn_button= (Button) header.findViewById(R.id.signIn_button);
        google_image= (ImageView) header.findViewById(R.id.google_image);
        card= (CardView) header.findViewById(R.id.card);



        if(currentUser!=null) {
            //download image from google
            image = new ImageView(this);
            card.addView(image);
            Picasso.with(this).load(imageUri).into(image);
            username_text.setText(name);
            email_text.setText(email);
        }

        if(FirebaseAuth.getInstance().getCurrentUser()==null){
            signIn_button.setVisibility(View.VISIBLE);
            google_image.setVisibility(View.VISIBLE);
            signIn_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Home.this,SignInActivity.class));
                }
            });
        }

        if(checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "premission granted");
            fm=getFragmentManager();
            fm.beginTransaction().add(R.id.fragment,new HomeFragment()).commit();
        }
        else
        {
            getPermission();

        }

        mAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(FirebaseAuth.getInstance().getCurrentUser()==null){
                    //iView.setImageAlpha(0);
                    card.setVisibility(View.GONE);
                    username_text.setText("");
                    email_text.setText("");
                    signIn_button.setVisibility(View.VISIBLE);
                    google_image.setVisibility(View.VISIBLE);

                    car_parkings.clear();
                    listParkings.clear();

                    signIn_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(Home.this,SignInActivity.class));
                        }
                    });

                    replacefragment(new HomeFragment());
                }
            }
        };


        Query cars=mDatabase.child("Users").child(currentUser.getUid()).child("Parkings");
        cars.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildAdded: "+dataSnapshot.getKey());

                if(dataSnapshot.getValue()!=null ) {
                    String title=dataSnapshot.getKey();
                    Map singleParking = (Map) dataSnapshot.getValue();
                    Object description = singleParking.get("Description");
                    Map coordinates = (Map) singleParking.get("Coordinates");
                    car_parkings.add(new Parking(title, Double.parseDouble(coordinates.get("Lat").toString()), Double.parseDouble(coordinates.get("Lng").toString()), description.toString()));

                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildRemoved: "+dataSnapshot.getKey());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved: "+dataSnapshot.getKey());


            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        final Query parkings=mDatabase.child("Parkings");
        parkings.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
               // Log.d(TAG, "onChildAdded: "+dataSnapshot.getKey());

                if(dataSnapshot.getValue()!=null ) {
                    ParkingsPlace p=dataSnapshot.getValue(ParkingsPlace.class);
                    listParkings.add(p);
                    for(ParkingsPlace pp:listParkings){
                        Log.d(TAG, "onChildAddedParkingsPlace: "+pp.getTitle()+","+pp.getCoordinates()+","+pp.getDescription());
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildRemoved: "+dataSnapshot.getKey());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved: "+dataSnapshot.getKey());


            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public ArrayList<Parking> getListParkings(){
        return car_parkings;
    }
    public ArrayList<ParkingsPlace> getListParkingsPlace(){
        return listParkings;
    }

    public String makeURL (double sourcelat, double sourcelog, double destlat, double destlog ){
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append(Double.toString(sourcelat));
        urlString.append(",");
        urlString
                .append(Double.toString( sourcelog));
        urlString.append("&destination=");// to
        urlString
                .append(Double.toString( destlat));
        urlString.append(",");
        urlString.append(Double.toString(destlog));
        urlString.append("&sensor=false&mode=walking&alternatives=true");
        String apiKey="AIzaSyDcRarWNqsbymt_SHnfwQceOrlOeJq7U1g";
        urlString.append("&key="+apiKey);
        Log.d(TAG, "makeURL: "+urlString.toString());
        return urlString.toString();
    }

    private static final int PERMISSIONS_REQUEST = 1;

    public void getPermission() {

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

            }
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST);
        }


    }

    // Callback with the request from calling requestPermissions(...)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
                Log.d(TAG, "Permission granted");
                fm=getFragmentManager();
                fm.beginTransaction().add(R.id.fragment,new HomeFragment()).commit();
            } else {
                Log.d(TAG, "Permission denied");
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
        }
        else {
            doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        Log.d(TAG, "onStart: sono dentro onstart");
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        int id = item.getItemId();
        changeOptionSideBar(id);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void changeOptionSideBar(int id){
        switch (id){
            case R.id.home_menu:
                replacefragment(new HomeFragment());
                break;
            case R.id.editCarPark_menu:
                replacefragment(new EditFragment());
                break;
            case R.id.map_full:
                replacefragment(new MapsFragment());
                break;
            case R.id.notes_menu:
                replacefragment(new NotesFragment());
                break;
            case R.id.info_menu:
                replacefragment(new InfoFragment());
                break;
            case R.id.logout:
                mAuth.signOut();
                Log.d(TAG, "changeOptionSideBar: signoutoption");

                break;

        }
    }

    public void replacefragment(android.app.Fragment f){
                fm.beginTransaction().replace(R.id.fragment,f).commit();
    }


    public DatabaseReference getDB(){
        return mDatabase;
    }

    public FirebaseAuth getAuth(){
        return mAuth;
    }

    public void removeParking(String title){
        for(int i=0;i<car_parkings.size();i++){
            if(car_parkings.get(i).getTitle().equals(title)){
                car_parkings.remove(i);
                break;
            }
        }
    }

    public void updateParking(String title, String Description, Double lat,Double lng){
        for(int i=0;i<car_parkings.size();i++){
            if(car_parkings.get(i).getTitle().equals(title)){
                car_parkings.remove(i);
                car_parkings.add(new Parking(title,lat,lng,Description));
                break;
            }
        }

    }

    public Polyline getPreviousPoly() {
        return polyline;
    }

    public void setPolyline(Polyline polyline) {
        this.polyline = polyline;
    }
}
