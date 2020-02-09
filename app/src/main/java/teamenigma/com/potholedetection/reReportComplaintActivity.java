package teamenigma.com.potholedetection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class reReportComplaintActivity extends AppCompatActivity {

    Button complainButton;
    DatabaseReference databaseReference;
    StorageReference mStorageRef;
    EditText complainText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_re_report_complaint);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Complains");

        mStorageRef = FirebaseStorage.getInstance().getReference();

        complainButton = (Button)findViewById(R.id.complainButton);
        complainText = (EditText)findViewById(R.id.complainEditText);
        complainButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ComplainDetails complainDetails = new ComplainDetails(MyReportsAdapter.uploadKey, complainText.getText().toString());

                        databaseReference.setValue(complainDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                    Toast.makeText(reReportComplaintActivity.this, "Complain Reported successfully", Toast.LENGTH_LONG).show();
                                else
                                    Toast.makeText(reReportComplaintActivity.this, "Failed to complain the report!", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
    }
}
