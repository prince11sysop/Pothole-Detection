package teamenigma.com.potholedetection;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import teamenigma.com.potholedetection.GeoLocation.LocationMainActivity;
import teamenigma.com.potholedetection.GoogleAuth.Login;
import teamenigma.com.potholedetection.IntroSlider.WelcomeActivity;

import android.view.Menu;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;

    TextView mUserName,mUserEmail;
    ImageView mUserImage;
    FirebaseAuth mAuth;
    NavigationView mNavigationView;
    View mHeaderView;
    public static String userName,userEmail;
    public static Uri imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPrefManager sharedPrefManager=new SharedPrefManager(this);
        if(sharedPrefManager.isLoggedIn()){
            updateSideNavHeader();
        }
        else{
            startActivity(new Intent(MainActivity.this, Login.class));
            finish();
        }


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               startActivity(new Intent(MainActivity.this, LocationMainActivity.class));
            }
        });


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

    }


    public void updateSideNavHeader(){

        mAuth = FirebaseAuth.getInstance();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(MainActivity.this);

        // NavigationView
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);

        // NavigationView Header
        mHeaderView =  mNavigationView.getHeaderView(0);

        // View
        mUserName = (TextView) mHeaderView.findViewById(R.id.name);
        mUserEmail = (TextView) mHeaderView.findViewById(R.id.email);
        mUserImage = (ImageView) mHeaderView.findViewById(R.id.imageView);

        userName=mAuth.getCurrentUser().getDisplayName();
        userEmail=mAuth.getCurrentUser().getEmail();
        imageUrl=mAuth.getCurrentUser().getPhotoUrl();

        // Set username & email
        mUserName.setText(mAuth.getCurrentUser().getDisplayName().split(" ")[0]);
        mUserEmail.setText(mAuth.getCurrentUser().getEmail());
        Glide.with(getApplicationContext())
                .load(mAuth.getCurrentUser().getPhotoUrl())
                .apply(new RequestOptions()
                        .placeholder(R.mipmap.ic_launcher)
                        .fitCenter())
                .into(mUserImage);

        mNavigationView.setNavigationItemSelectedListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);



        return true;
    }
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if(id == R.id.menu_logout) {
//
//            Intent i=new Intent(MainActivity.this,NotificationsMainActivity.class);
//            startActivity(i);
//
////            Toast.makeText(MainActivity.this, "Signed Out successfully!", Toast.LENGTH_SHORT).show();
////            startActivity(new Intent(MainActivity.this, GoogleSignInActivity.class));
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        int id = menuItem.getItemId();
        if(id == R.id.nav_gallery) {

            Intent i=new Intent(MainActivity.this,MapsActivity.class);
            startActivity(i);

//            Toast.makeText(MainActivity.this, "Signed Out successfully!", Toast.LENGTH_SHORT).show();
//            startActivity(new Intent(MainActivity.this, GoogleSignInActivity.class));
        }

        return false;
    }
}
