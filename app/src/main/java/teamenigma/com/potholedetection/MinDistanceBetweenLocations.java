package teamenigma.com.potholedetection;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

public class MinDistanceBetweenLocations extends Service {


    DatabaseReference databaseReference;
    private  Context mContext;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public MinDistanceBetweenLocations(Context context,Location location){
        this.mContext=context;
        getNearestCoordinate(location);
    }

    //to get the nearest coordinate
    public String[] getNearestCoordinate(Location location) {

        final List<UserData> potholesDetails = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Potholes");

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

        double minDist=Integer.MAX_VALUE;
        int index=-1;

        for(int i=0;i<potholesDetails.size();i++){
            double latitude,longitude;
            latitude=Double.parseDouble(potholesDetails.get(i).potholeLatitude);
            longitude=Double.parseDouble(potholesDetails.get(i).potholeLongitude);

            Location location2=new Location("");
            location2.setLatitude(latitude);
            location2.setLongitude(longitude);

            double currDist=location.distanceTo(location2);

            minDist=currDist;
            index=i;
//            if(currDist<minDist);{
//                minDist=currDist;
//                index=i;
//            }
        }

        String uploadKey=null;
        if(index!=-1)
            uploadKey=potholesDetails.get(index).uploadKey;

        String res[]={Double.toString(potholesDetails.size()),uploadKey};

        return res;
    }


}
