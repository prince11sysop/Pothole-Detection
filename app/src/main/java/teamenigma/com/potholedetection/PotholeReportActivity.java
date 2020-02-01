package teamenigma.com.potholedetection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class PotholeReportActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {


    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    PendingResult<LocationSettingsResult> pendingResult;
    final static int REQUEST_LOCATION = 199;


    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 100;
    public static final String ALLOW_KEY = "ALLOWED";
    public static final String CAMERA_PREF = "camera_pref";

    Spinner sevSpinner,trafficSpinner;
    Button uploadImageButton,reportButton;
    ImageView img;
    public String severinity , traffic="", uid = " " , postalCode = " ", state = " ";
    public String URL=" ",status = "Pending";
    public String potholeAddress="",potholeLatitude="",potholeLongitude="";
    public String numOfTimesPotholeReported="1";

    UserData userData;
    StorageReference mStorageRef;
    DatabaseReference dfref;
    Task<Uri> result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pothole_report);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        dfref = FirebaseDatabase.getInstance().getReference();

        uploadImageButton = (Button) findViewById(R.id.takeImageButton);
        reportButton = (Button) findViewById(R.id.reportButton);
        img = (ImageView) findViewById(R.id.potholeImage);


        buildGoogleApiClient();

        //get the spinner from the xml.
        sevSpinner = findViewById(R.id.severinitySpinner);
        trafficSpinner = findViewById(R.id.trafficSpinner);
        String[] items = new String[]{"High", "Medium", "Low"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_text, items);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        sevSpinner.setAdapter(adapter);
        trafficSpinner.setAdapter(adapter);

        sevSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                severinity = (String) parent.getItemAtPosition(position);
                // Notify the selected item text
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        trafficSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                traffic = (String) parent.getItemAtPosition(position);
                // Notify the selected item text
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ContextCompat.checkSelfPermission(PotholeReportActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    if (getFromPref(PotholeReportActivity.this, ALLOW_KEY)) {
                        showSettingsAlert();

                    }else if (ContextCompat.checkSelfPermission(PotholeReportActivity.this,Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED) {

                        if (ActivityCompat.shouldShowRequestPermissionRationale(PotholeReportActivity.this,Manifest.permission.CAMERA)) {

                            showAlert();

                        }else {
                            // No explanation needed, we can request the permission.
                            ActivityCompat.requestPermissions(PotholeReportActivity.this,new String[]{Manifest.permission.CAMERA},MY_PERMISSIONS_REQUEST_CAMERA);
                        }
                    }
                }else {
                    openCamera();
                }
            }
        });

        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String filename = System.currentTimeMillis()+" ";

                while (!result.isSuccessful());
                        URL = result.getResult().toString();
                        userData = new UserData(uid,postalCode,state,severinity,traffic,URL,status,potholeAddress,
                                potholeLatitude,potholeLongitude,numOfTimesPotholeReported);

                        dfref.child(filename).setValue(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                    Toast.makeText(PotholeReportActivity.this , "File sucessfully uploaded" , Toast.LENGTH_LONG).show();
                                else
                                    Toast.makeText(PotholeReportActivity.this , "File not sucessfully uploaded" , Toast.LENGTH_LONG).show();

                            }
                        });

                dfref.child(filename).setValue(userData);

            }
        });
    }


    private void showAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(PotholeReportActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("App needs to access the Camera.");

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "DONT ALLOW",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(PotholeReportActivity.this , "Cannot report Without Camera Permissions" , Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                        finish();
                    }
                });

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "ALLOW",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ActivityCompat.requestPermissions(PotholeReportActivity.this,
                                new String[]{Manifest.permission.CAMERA},
                                MY_PERMISSIONS_REQUEST_CAMERA);
//                        openCamera();
                    }
                });
        alertDialog.show();
    }

    private void showSettingsAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(PotholeReportActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("App needs to access the Camera.");

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "DONT ALLOW",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "SETTINGS",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
//                        openCamera();
                        startInstalledAppDetailsActivity(PotholeReportActivity.this);
                    }
                });

        alertDialog.show();
    }

    public static Boolean getFromPref(Context context, String key){
        SharedPreferences myPrefs = context.getSharedPreferences(CAMERA_PREF,Context.MODE_PRIVATE);
        return (myPrefs.getBoolean(key, false));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public static void startInstalledAppDetailsActivity(final Activity context) {
        if (context == null) {
            return;
        }

        final Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + context.getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(i);
    }

    private void openCamera() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        startActivityForResult(intent,MY_PERMISSIONS_REQUEST_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap;

        if (requestCode == MY_PERMISSIONS_REQUEST_CAMERA) {

            if(resultCode == Activity.RESULT_OK){
                bitmap = (Bitmap) data.getExtras().get("data");
                img.setImageBitmap(bitmap);
                final ProgressDialog progressDialoge = new ProgressDialog(this);
                progressDialoge.setTitle("uploading..");
                progressDialoge.show();

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] images = baos.toByteArray();

                final String filename = System.currentTimeMillis()+" ";

                UploadTask uploadTask = mStorageRef.child("uploads").child(filename).putBytes(images);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        progressDialoge.dismiss();
                        Toast.makeText(PotholeReportActivity.this,  "Not Added!", Toast.LENGTH_LONG).show();
                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        result = taskSnapshot.getMetadata().getReference().getDownloadUrl();

                        progressDialoge.dismiss();
//                        //Log.d("downloadUrl-->", "" + downloadUrl);
                    }
                });
            } else {
                Toast.makeText(PotholeReportActivity.this , "Photo not Taken." , Toast.LENGTH_LONG).show();

            }
        }


        //final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode){

            case REQUEST_LOCATION:
                switch (resultCode){

                    case Activity.RESULT_OK:{
                        // All required changes were successfully made
                        Toast.makeText(PotholeReportActivity.this, "Location enabled by user!", Toast.LENGTH_LONG).show();

                        while (new GpsTracker(PotholeReportActivity.this).getLatitude()==0.0
                                || new GpsTracker(PotholeReportActivity.this).getLongitude()==0.0){
                            new GpsTracker(this).getLatitude();
                            new GpsTracker(this).getLongitude();
                        }
                        updateLocation();

                        break;
                    }
                    case Activity.RESULT_CANCELED:{
                        // The user was asked to change settings, but chose not to
                        Toast.makeText(PotholeReportActivity.this, "Location not enabled, user cancelled.", Toast.LENGTH_LONG).show();
                        break;
                    }

                    default:{

                        break;
                    }
                }
                break;
        }
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
                            status.startResolutionForResult(PotholeReportActivity.this,REQUEST_LOCATION);

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


    public void updateLocation(){
        GpsTracker gps = new GpsTracker(PotholeReportActivity.this);
        double latitude = gps.getLatitude();
        double longitude = gps.getLongitude();

        HashMap<String,String> addressMap=gps.getCompleteAddressString(latitude,longitude);

        postalCode=addressMap.get("postalCode");

        potholeLatitude=latitude+"";
        state=addressMap.get("state");
        potholeLongitude=longitude+"";

        Toast.makeText(PotholeReportActivity.this, "Latitude: "+latitude+" Longitude: "+longitude, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


}
