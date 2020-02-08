package teamenigma.com.potholedetection;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.media.ImageWriter;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.textfield.TextInputLayout;

public class PotholeDetails extends Activity {
    ImageView img;
    TextView potholeLocation,dateReportedOn,currentStatus,traffic,severity,freq;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pothole_details);

        Toolbar toolbar =findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        img = (ImageView) findViewById(R.id.pic);
        dateReportedOn = (TextView) findViewById(R.id.date);
        currentStatus = (TextView) findViewById(R.id.currentStatus);
        potholeLocation = (TextView) findViewById(R.id.address);
        traffic = (TextView) findViewById(R.id.traffic);
        freq=(TextView) findViewById(R.id.freq);
        severity=(TextView)findViewById(R.id.severity);

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
        freq.setText("This pothole has been reported total of "+MyReportsAdapter.freq+" times!");
        severity.setText(MyReportsAdapter.severity);
    }
}
