package teamenigma.com.potholedetection;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.ImageWriter;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class PotholeDetails extends Activity {
    ImageView img, reReportButton;
    TextView potholeLocation, dateReportedOn, currentStatus, traffic, severity, freq;
    DatabaseReference databaseReference;
    StorageReference mStorageRef;
    Button complainButton;
    EditText complainText;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pothole_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Complains");

        mStorageRef = FirebaseStorage.getInstance().getReference();

        img = (ImageView) findViewById(R.id.pic);
        reReportButton = (ImageView) findViewById(R.id.reReportButton);
        dateReportedOn = (TextView) findViewById(R.id.date);
        currentStatus = (TextView) findViewById(R.id.currentStatus);
        potholeLocation = (TextView) findViewById(R.id.address);
        traffic = (TextView) findViewById(R.id.traffic);
        freq = (TextView) findViewById(R.id.freq);
        severity = (TextView) findViewById(R.id.severity);

        Glide.with(this)
                .load(MyReportsAdapter.imageUrl)
                .apply(new RequestOptions()
                        .placeholder(R.mipmap.ic_launcher)
                        .fitCenter())
                .into(img);

        dateReportedOn.setText(MyReportsAdapter.dateReportedOn);
        potholeLocation.setText(MyReportsAdapter.potholeLocation);
        currentStatus.setText(MyReportsAdapter.currentStatus);
        traffic.setText(MyReportsAdapter.traffic);
        freq.setText("This pothole has been reported total of " + MyReportsAdapter.freq + " times!");
        severity.setText(MyReportsAdapter.severity);
        reReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(PotholeDetails.this, "You clicked on ImageView.", Toast.LENGTH_SHORT).show();
                View view1 = getLayoutInflater().inflate(R.layout.activity_re_report_complaint, null);
                BottomSheetDialog dialog = new BottomSheetDialog(PotholeDetails.this);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setContentView(view1);
                dialog.show();


                complainButton = (Button)view1.findViewById(R.id.complainButton);
                complainText = (EditText)view1.findViewById(R.id.complainEditText);

                final String filename = System.currentTimeMillis() + " ";
                complainButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ComplainDetails complainDetails = new ComplainDetails(MyReportsAdapter.uploadKey, complainText.getText().toString());

                        databaseReference.child(filename).setValue(complainDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                    Toast.makeText(PotholeDetails.this, "Complain Reported successfully", Toast.LENGTH_LONG).show();
                                else
                                    Toast.makeText(PotholeDetails.this, "Failed to complain the report!", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
            }
        });
    }
}
