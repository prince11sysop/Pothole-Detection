package teamenigma.com.potholedetection.GeoLocation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import teamenigma.com.potholedetection.MainActivity;
import teamenigma.com.potholedetection.R;
import teamenigma.com.potholedetection.UserData;


public class LocationMainActivity extends Activity {

    private FusedLocationProviderClient mFusedLocationClient;

    private double wayLatitude = 0.0, wayLongitude = 0.0;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private StringBuilder stringBuilder;

    private boolean isContinue = false;
    private boolean isGPS = false;

    HashMap<String,String> addressMap;
    DatabaseReference databaseReference;
    List<UserData> potholesDetails;
    double minDist=Integer.MAX_VALUE;
    String uploadKey=null;
    int val=1;


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
    public boolean isItPothole=true;

    UserData userData;
    StorageReference mStorageRef;
    Task<Uri> result;
    Button okButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_activity_main);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        uploadImageButton = (Button) findViewById(R.id.takeImageButton);
        reportButton = (Button) findViewById(R.id.reportButton);
        img = (ImageView) findViewById(R.id.potholeImage);
        sevSpinner = findViewById(R.id.severinitySpinner);
        trafficSpinner = findViewById(R.id.trafficSpinner);


        databaseReference = FirebaseDatabase.getInstance().getReference().child("Potholes");
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        fetchData();

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000); // 10 seconds
        locationRequest.setFastestInterval(5 * 1000); // 5 seconds



        new GpsUtils(this).turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                // turn on GPS
                isGPS = isGPSEnable;
            }
        });

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null)
                    return;

                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        wayLatitude = location.getLatitude();
                        wayLongitude = location.getLongitude();
                        if (!isContinue) {
//                            txtLocation.setText(String.format(Locale.US, "%s - %s", wayLatitude, wayLongitude));
                        } else {
                            stringBuilder.append(wayLatitude);
                            stringBuilder.append("-");
                            stringBuilder.append(wayLongitude);
                            stringBuilder.append("\n\n");
//                            txtContinueLocation.setText(stringBuilder.toString());
                        }
                        if (!isContinue && mFusedLocationClient != null) {
                            mFusedLocationClient.removeLocationUpdates(locationCallback);
                        }
                    }
                }
            }
        };


        reportButton.setOnClickListener(v -> {

            if (!isGPS) {
                Toast.makeText(this, "Please turn on GPS", Toast.LENGTH_SHORT).show();
                return;
            }
//            isContinue = false;
//            getLocation();

            getNearestCoordinate(wayLatitude,wayLongitude);
            addressMap=getCompleteAddressString(wayLatitude,wayLongitude);

            if(!addressMap.get("state").equals("Jharkhand")){
                Toast.makeText(this, "Location outside of Jharkhand! "+addressMap.get("state"), Toast.LENGTH_SHORT).show();

            }else{

//            if it is in radius of 10m to any previous pothole, just increase it count, else create a new report
                if (minDist < 100) {

                    if(uploadKey!=null){
                        databaseReference.child(uploadKey).child("numOfTimesReported").setValue(""+(val+1));
                        Toast.makeText(LocationMainActivity.this, "Distance less than 100m", Toast.LENGTH_SHORT).show();

                    }else
                        Toast.makeText(this, "Null key", Toast.LENGTH_SHORT).show();

                } else {

                    final String filename = System.currentTimeMillis() + " ";
                    while (!result.isSuccessful()) ;
                    URL = result.getResult().toString();
                    postalCode = addressMap.get("postalCode");
                    state = addressMap.get("state");
                    potholeAddress = addressMap.get("address");
                    potholeLatitude = "" + wayLatitude;
                    potholeLongitude = "" + wayLongitude;

                    String potholeStatus;
                    if(isItPothole==true)
                        potholeStatus="yes";
                    else
                        potholeStatus="no";

                    userData = new UserData(uid, postalCode, state, severinity, traffic, URL, status, potholeAddress,
                            potholeLatitude, potholeLongitude, numOfTimesPotholeReported, filename,potholeStatus);

                    databaseReference.child(filename).setValue(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                                Toast.makeText(LocationMainActivity.this, "Pothole reported successfully to nearest Municipality", Toast.LENGTH_LONG).show();
                            else
                                Toast.makeText(LocationMainActivity.this, "Failed to report pothole!", Toast.LENGTH_LONG).show();

                        }
                    });

                    databaseReference.child(filename).setValue(userData);

                }
            }

            showCustomDialog();
        });


        //handling image and adapter
        String[] items = new String[]{"High", "Medium", "Low"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_text, items);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        sevSpinner.setAdapter(adapter);
        trafficSpinner.setAdapter(adapter);

        sevSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                severinity = (String) parent.getItemAtPosition(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        trafficSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                traffic = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        uploadImageButton.setOnClickListener(view -> {

            if (ContextCompat.checkSelfPermission(LocationMainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                if (getFromPref(LocationMainActivity.this, ALLOW_KEY)) {
                    showSettingsAlert();

                } else if (ContextCompat.checkSelfPermission(LocationMainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(LocationMainActivity.this, Manifest.permission.CAMERA)) {

                        showAlert();

                    } else {
                        // No explanation needed, we can request the permission.
                        ActivityCompat.requestPermissions(LocationMainActivity.this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
                    }
                }
            } else {
                isContinue = false;
                getLocation();
                openCamera();
            }
        });


        //then we will inflate the custom alert dialog xml that we created
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View view = inflater.inflate(R.layout.my_dialog, null);

        okButton=(Button)view.findViewById(R.id.buttonOk);

        okButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
//                                            startActivity(new Intent(LocationMainActivity.this, MainActivity.class));
                                            finish();
                                        }
                                    });

//        okButton.setOnClickListener(v -> startActivity(new Intent(LocationMainActivity.this, MainActivity.class)));

    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(LocationMainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(LocationMainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(LocationMainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    AppConstants.LOCATION_REQUEST);

        } else {
            if (isContinue) {
                mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
            } else {
                mFusedLocationClient.getLastLocation().addOnSuccessListener(LocationMainActivity.this, location -> {
                    if (location != null) {
                        wayLatitude = location.getLatitude();
                        wayLongitude = location.getLongitude();

                    } else {
                        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                    }
                });
            }
        }
    }

    //function to get complete address
    public HashMap<String,String> getCompleteAddressString(double latitude, double longitude) {

        Geocoder geocoder;
        List<Address> addresses=new ArrayList<>();
        geocoder = new Geocoder(this, Locale.getDefault());

        HashMap<String,String> map=new HashMap<>();

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses != null && addresses.size() > 0) {
                String address = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();
                String subAdmin=addresses.get(0).getSubAdminArea();

                //adding all the above details to hashmap
                map.put("address",address);
                map.put("city",city);
                map.put("state",state);
                map.put("country",country);
                map.put("postalCode",postalCode);
                map.put("knownName",knownName);
                map.put("subAdmin",subAdmin);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  map;
    }



    private void getNearestCoordinate(double lat,double lon) {

        int index=-1;

        Location location=new Location("");
        location.setLatitude(lat);
        location.setLongitude(lon);

        for(int i=0;i<potholesDetails.size();i++){
            double latitude,longitude;
            latitude=Double.parseDouble(potholesDetails.get(i).potholeLatitude);
            longitude=Double.parseDouble(potholesDetails.get(i).potholeLongitude);

            Location location2=new Location("");
            location2.setLatitude(latitude);
            location2.setLongitude(longitude);

            double currDist=location.distanceTo(location2);

            if(currDist<minDist);{
                minDist=currDist;
                index=i;
            }
        }

        if(index!=-1){
            uploadKey=potholesDetails.get(index).uploadKey;
            val=Integer.parseInt(potholesDetails.get(index).numOfTimesReported);
        }
    }

    //fetching data from firebase to find the nearest coordinate
    public void fetchData(){

        potholesDetails = new ArrayList<>();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    UserData studentDetails = dataSnapshot.getValue(UserData.class);
                    potholesDetails.add(studentDetails);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    private void showAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(LocationMainActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("App needs to access the Camera.");

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "DONT ALLOW",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(LocationMainActivity.this , "Cannot report Without Camera Permissions" , Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                        finish();
                    }
                });

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "ALLOW",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ActivityCompat.requestPermissions(LocationMainActivity.this,
                                new String[]{Manifest.permission.CAMERA},
                                MY_PERMISSIONS_REQUEST_CAMERA);
//                        openCamera();
                    }
                });
        alertDialog.show();
    }

    private void showSettingsAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(LocationMainActivity.this).create();
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
                        startInstalledAppDetailsActivity(LocationMainActivity.this);
                    }
                });
        alertDialog.show();
    }

    public static Boolean getFromPref(Context context, String key){
        SharedPreferences myPrefs = context.getSharedPreferences(CAMERA_PREF, Context.MODE_PRIVATE);
        return (myPrefs.getBoolean(key, false));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public static void startInstalledAppDetailsActivity(final Activity context) {
        if (context == null)
            return;

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


    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1000: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (isContinue) {
                        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                    } else {
                        mFusedLocationClient.getLastLocation().addOnSuccessListener(LocationMainActivity.this, location -> {
                            if (location != null) {
                                wayLatitude = location.getLatitude();
                                wayLongitude = location.getLongitude();
//                                txtLocation.setText(String.format(Locale.US, "%s - %s", wayLatitude, wayLongitude));
                            } else {
                                mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                            }
                        });
                    }
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap;

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == AppConstants.GPS_REQUEST) {
                isGPS = true; // flag maintain before get location
            }

        }
        if (requestCode == MY_PERMISSIONS_REQUEST_CAMERA) {

            if(resultCode == Activity.RESULT_OK){
                bitmap = (Bitmap) data.getExtras().get("data");
                img.setImageBitmap(bitmap);
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Uploading image...");
                progressDialog.setTitle("Please Wait");
                progressDialog.show();
                progressDialog.setCancelable(false);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                byte[] images = baos.toByteArray();

                final String filename = System.currentTimeMillis()+" ";

                UploadTask uploadTask = mStorageRef.child("uploads").child(filename).putBytes(images);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        progressDialog.dismiss();
                        Toast.makeText(LocationMainActivity.this,  "Not Added!", Toast.LENGTH_LONG).show();
                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        result = taskSnapshot.getMetadata().getReference().getDownloadUrl();

                        new SendPostRequest().execute(); //check whether it is pothole or not

                        progressDialog.dismiss();
                    }
                });
            } else {
                Toast.makeText(LocationMainActivity.this , "Photo not Taken." , Toast.LENGTH_LONG).show();
            }
        }
    }

    private void showCustomDialog() {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(this).inflate(R.layout.my_dialog, viewGroup, false);


        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);

        //finally creating the alert dialog and displaying it
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
//        alertDialog.setCancelable(false);
    }

    public class SendPostRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){}

        protected String doInBackground(String... arg0) {

            try {

                java.net.URL url = new URL("http://700dec80.ngrok.io"); // here is your URL path

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("file_path", "https://imagevars.gulfnews.com/2019/12/12/Sunny-Leone-_16ef9a23484_original-ratio.jpg");

                Log.e("params",postDataParams.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);


                OutputStream os = conn.getOutputStream();
                os.write(postDataParams.toString().getBytes("UTF-8"));
                os.close();

                OutputStreamWriter wr= new OutputStreamWriter(conn.getOutputStream());
                wr.write(postDataParams.toString());

                int responseCode=conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader in=new BufferedReader(new
                            InputStreamReader(
                            conn.getInputStream()));

                    StringBuffer sb = new StringBuffer("");
                    String line="";

                    while((line = in.readLine()) != null) {

                        sb.append(line);
                        break;
                    }

                    in.close();
                    return sb.toString();

                }
                else {

                    return new String("false : "+responseCode);
                }
            }
            catch(Exception e){
                return new String("Exception: " + e.getMessage());
            }

        }

        @Override
        protected void onPostExecute(String result) {
//            Toast.makeText(getApplicationContext(), result,Toast.LENGTH_LONG).show();
            if(result.equals("1"))
                isItPothole=true;
            else
                isItPothole=false;

        }
    }


}