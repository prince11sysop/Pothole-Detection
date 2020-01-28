package teamenigma.com.potholedetection;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.util.HashMap;

public class LocationActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    PendingResult<LocationSettingsResult> pendingResult;
    final static int REQUEST_LOCATION = 199;
    TextView latTextView, lonTextView,addTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        latTextView = (TextView) findViewById(R.id.latitude_textview);
        lonTextView = (TextView) findViewById(R.id.longitude_textview);
        addTextView= (TextView) findViewById(R.id.address);

        buildGoogleApiClient();
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        mGoogleApiClient.connect();
    }


    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(10 * 1000);
        mLocationRequest.setFastestInterval(5 * 1000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);

        pendingResult = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        pendingResult.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                //final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        //...
                            updateLocation();
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(LocationActivity.this,REQUEST_LOCATION);

                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            //...
                            break;
                    }
                }
            });

        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data){
            Log.d("onActivityResult()", Integer.toString(resultCode));

            //final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
            switch (requestCode)
            {
                case REQUEST_LOCATION:
                    switch (resultCode){

                        case Activity.RESULT_OK:{
                            // All required changes were successfully made
                            Toast.makeText(LocationActivity.this, "Location enabled by user!", Toast.LENGTH_LONG).show();

                            while (new GpsTracker(LocationActivity.this).getLatitude()==0.0
                                    || new GpsTracker(LocationActivity.this).getLongitude()==0.0){
                                new GpsTracker(this).getLatitude();
                                new GpsTracker(this).getLongitude();
                            }
                            updateLocation();

                            break;
                        }
                        case Activity.RESULT_CANCELED:{
                            // The user was asked to change settings, but chose not to
                            Toast.makeText(LocationActivity.this, "Location not enabled, user cancelled.", Toast.LENGTH_LONG).show();
                            break;
                        }

                        default:{

                            break;
                        }
                    }
                    break;
            }
        }


        public void updateLocation(){
            GpsTracker gps = new GpsTracker(LocationActivity.this);
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            latTextView.setText(latitude+" ");
            lonTextView.setText(longitude+" ");
            HashMap<String,String> addressMap=gps.getCompleteAddressString(latitude,longitude);

            addTextView.setText(addressMap.get("city"));
            Toast.makeText(LocationActivity.this, "Latitude: "+latitude, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onConnectionSuspended(int i) {

        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {

        }

  }

