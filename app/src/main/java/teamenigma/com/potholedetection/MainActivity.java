package teamenigma.com.potholedetection;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.bumptech.glide.load.engine.Resource;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import teamenigma.com.potholedetection.GeoLocation.LocationMainActivity;
import teamenigma.com.potholedetection.IntroSlider.WelcomeActivity;

import android.view.Menu;
import android.widget.Button;
import android.widget.ShareActionProvider;
import android.widget.Toast;

import teamenigma.com.potholedetection.ui.gallery.GalleryFragment;
import teamenigma.com.potholedetection.ui.home.HomeFragment;
import teamenigma.com.potholedetection.ui.send.SendFragment;
import teamenigma.com.potholedetection.ui.share.ShareFragment;
import teamenigma.com.potholedetection.ui.slideshow.SlideshowFragment;
import teamenigma.com.potholedetection.ui.tools.ToolsFragment;



//        implements NavigationView.OnNavigationItemSelectedListener {
//
//@Override
//protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//        this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
//        toggle.syncState();
//
//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);
//
//        //add this line to display menu1 when the activity is loaded
////        displaySelectedScreen(R.id.nav_home);
//        }
//
//@Override
//public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//        drawer.closeDrawer(GravityCompat.START);
//        } else {
//        super.onBackPressed();
//        }
//        }
//
//@Override
//public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//        }
//
//@Override
//public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//        return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//        }
//
//private void displaySelectedScreen(int itemId) {
//
//        //creating fragment object
//        Fragment fragment = null;
//
//        //initializing the fragment object which is selected
//        switch (itemId) {
//        case R.id.nav_home:
//        fragment = new HomeFragment();
//        break;
//        case R.id.nav_gallery:
//        fragment = new GalleryFragment();
//        break;
//        case R.id.nav_slideshow:
//        fragment = new SlideshowFragment();
//        break;
//            case R.id.nav_tools:
//                fragment = new ToolsFragment();
//                break;
//            case R.id.nav_send:
//                fragment = new SendFragment();
//                break;
//            case R.id.nav_share:
//                fragment = new ShareFragment();
//                break;
//        }
//
//        //replacing the fragment
//        if (fragment != null) {
//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        ft.replace(R.id.nav_host_fragment, fragment);
//        ft.commit();
//        }
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
//        }
//
//
//@SuppressWarnings("StatementWithEmptyBody")
//@Override
//public boolean onNavigationItemSelected(MenuItem item) {
//
//        //calling the method displayselectedscreen and passing the id of selected menu
//        displaySelectedScreen(item.getItemId());
//        //make this method blank
//        return true;
//        }
public class MainActivity extends AppCompatActivity{
    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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


}
