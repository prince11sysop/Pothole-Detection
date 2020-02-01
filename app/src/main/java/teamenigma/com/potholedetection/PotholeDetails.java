package teamenigma.com.potholedetection;

import androidx.appcompat.app.AppCompatActivity;

import android.media.ImageWriter;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

public class PotholeDetails extends AppCompatActivity {
    ImageView img;
    TextView date, location, status , muncipality;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pothole_details);

        img = (ImageView) findViewById(R.id.reportedPotholeImage);
        date = (TextView) findViewById(R.id.date);
        status = (TextView) findViewById(R.id.status);
        location = (TextView) findViewById(R.id.location);
        muncipality = (TextView) findViewById(R.id.muncipality);

    }
}
