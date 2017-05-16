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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.net.URL;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private FragmentManager fm;
    private boolean doubleBackToExitPressedOnce = false;
    private static final String TAG="myTAG";
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
    private TextView login_text;
    private ImageView iView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


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
        login_text=(TextView)header.findViewById(R.id.login_text);
        iView=(ImageView)header.findViewById(R.id.imageView);


        if(currentUser!=null) {
            //download image from google
            Picasso.with(this).load(imageUri).into(iView);
            username_text.setText(name);
            email_text.setText(email);
        }

        if(FirebaseAuth.getInstance().getCurrentUser()==null){
            login_text.setText("Do you want to sign in? do it NOW");
            login_text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Home.this,SignInActivity.class));
                }
            });
        }


        

        ////////////////////////////////
        if(checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "premission granted");
            fm=getFragmentManager();
            fm.beginTransaction().add(R.id.fragment,new HomeFragment()).commit();
        }
        else
        {
            getPermission();

        }
        ////////////////////////////////



        mAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(FirebaseAuth.getInstance().getCurrentUser()==null){
                    iView.setImageAlpha(0);
                    username_text.setText("");
                    email_text.setText("");
                    login_text.setText("Do you want to sign in? do it NOW");
                    login_text.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(Home.this,SignInActivity.class));
                        }
                    });
                }
            }
        };


    }


    /////////////////////////////////////
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

    ////////////////////////////////////




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
                break;
            case R.id.map_full:
                Intent map_full=new Intent(Home.this,MapsActivity.class);
                startActivity(map_full);
                break;
            case R.id.settings_menu:
                break;
            case R.id.info_menu:
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

}
