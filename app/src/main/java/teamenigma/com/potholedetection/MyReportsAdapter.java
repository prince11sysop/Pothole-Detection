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
import androidx.recyclerview.widget.RecyclerView;

public class MyReportsAdapter extends RecyclerView.Adapter<MyReportsAdapter.MyReportsViewHolder> {

    private Context myContext;
    private List<UserData> placementList;

    public MyReportsAdapter(Context myContext, List<UserData> placementList){
        this.myContext = myContext;
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
    public void onBindViewHolder(@NonNull final MyReportsViewHolder placementViewHolder, final int position) {

        final UserData placement = placementList.get(position);

        placementViewHolder.mAuthName.setText(placement.traffic);

        Glide.with(myContext)
                .load(placement.imageURL)
                .apply(new RequestOptions()
                        .placeholder(R.mipmap.ic_launcher)
                        .fitCenter())
                .into(placementViewHolder.mPic);


//        placementViewHolder.mPic.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                imageUrl=placement.getPic();
//                myContext.startActivity(new Intent(myContext, ImageFullScreen.class));
//            }
//        });
    }


    @Override
    public int getItemCount() {
        return placementList.size();
    }


//    public static class MyReportsViewHolder extends RecyclerView.ViewHolder {
    public class MyReportsViewHolder extends RecyclerView.ViewHolder {

        ImageView mPic;
        TextView mAuthName;


        public MyReportsViewHolder(@NonNull final View itemView) {
            super(itemView);
            mAuthName = itemView.findViewById(R.id.address);
            mPic= itemView.findViewById(R.id.pic);

            itemView.setTag(getAdapterPosition());

            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {

                    myContext.startActivity(new Intent(myContext,PotholeDetails.class));
//                    startActivity(new Intent(this,PotholeDetails.class));
                }
            });
        }

    }

}

