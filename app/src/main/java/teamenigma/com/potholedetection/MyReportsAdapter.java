package teamenigma.com.potholedetection;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class MyReportsAdapter extends RecyclerView.Adapter<MyReportsAdapter.MyReportsViewHolder> {

    private Context myContext;
    private List<UserData> placementList;
    public static String potholeLocation,dateReportedOn,imageUrl,currentStatus,traffic,severity,freq, uploadKey;


    public MyReportsAdapter(Context myContext, List<UserData> placementList){
        this.myContext = myContext;
    ComplainDetails complainDetails;
        this.placementList = placementList;
    }

    @NonNull
    @Override
    public MyReportsAdapter.MyReportsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        LayoutInflater layoutInflater = LayoutInflater.from(myContext);
        View view = layoutInflater.inflate(R.layout.card_format, null);

        return new MyReportsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyReportsViewHolder myReportsViewHolder, final int position) {

        final UserData userData = placementList.get(position);

        myReportsViewHolder.mAuthName.setText(userData.potholeAddress);

        Glide.with(myContext)
                .load(userData.imageURL)
                .apply(new RequestOptions()
                        .placeholder(R.mipmap.ic_launcher)
                        .fitCenter())
                .into(myReportsViewHolder.mPic);

        myReportsViewHolder.traffic.setText(userData.traffic);
        myReportsViewHolder.currStatus.setText(userData.status);
        myReportsViewHolder.reportDate.setText(userData.currentDate);
        myReportsViewHolder.severity.setText(userData.severinity);
        myReportsViewHolder.freq.setText("This pothole has been reported total of "+userData.numOfTimesReported+" times!");


//        placementViewHolder.mPic.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                imageUrl=placement.getPic();
//                myContext.startActivity(new Intent(myContext, ImageFullScreen.class));
//            }
//        });
        myReportsViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageUrl=userData.imageURL;
                potholeLocation=userData.potholeAddress;
                dateReportedOn=userData.currentDate;
                traffic=userData.traffic;
                currentStatus=userData.status;
                severity=userData.severinity;
                freq=userData.numOfTimesReported;
                uploadKey = userData.uploadKey;

                myContext.startActivity(new Intent(myContext,PotholeDetails.class));
            }
        });

    }


    @Override
    public int getItemCount() {
        return placementList.size();
    }


//    public static class MyReportsViewHolder extends RecyclerView.ViewHolder {
    public class MyReportsViewHolder extends RecyclerView.ViewHolder {

        ImageView mPic;
        TextView mAuthName, reportDate,currStatus, traffic,severity,freq,uploadKey;
        CardView cardView;


        public MyReportsViewHolder(@NonNull final View itemView) {
            super(itemView);
            mAuthName = itemView.findViewById(R.id.address);
            mPic= itemView.findViewById(R.id.pic);
            reportDate= itemView.findViewById(R.id.date);
            currStatus= itemView.findViewById(R.id.currentStatus);
            traffic= itemView.findViewById(R.id.traffic);
            severity= itemView.findViewById(R.id.severity);
            freq=itemView.findViewById(R.id.freq);
            cardView=itemView.findViewById(R.id.cardView);

            itemView.setTag(getAdapterPosition());

            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {

//                    myContext.startActivity(new Intent(myContext,PotholeDetails.class));
//                    startActivity(new Intent(this,PotholeDetails.class));
                }
            });
        }

    }

}

