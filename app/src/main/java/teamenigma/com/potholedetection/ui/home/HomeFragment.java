package teamenigma.com.potholedetection.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import teamenigma.com.potholedetection.Carousel.SliderAdapter;
import teamenigma.com.potholedetection.MainActivity;
import teamenigma.com.potholedetection.MyReportsAdapter;
import teamenigma.com.potholedetection.R;
import teamenigma.com.potholedetection.UserData;

public class HomeFragment extends Fragment {


    DatabaseReference mMessagesDatabaseReference;
    RecyclerView recyclerView;
    GridLayoutManager layoutManager;
    List<UserData> friendlyMessages;

    ViewPager viewPager;
    TabLayout indicator;

    List<Integer> sliderImages;
    List<String> sliderText;
    String sliderText1="You report the pothole, we'll fix it.";
    String sliderText2="Let's fill in the potholes together.\nOne pothole at a time.";
    String sliderText4="Sometimes, you need to think outside the car.";
    String sliderText5="Who can stop me? Oops, I spoke too soon.";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);


        //carousel
        viewPager=(ViewPager)root.findViewById(R.id.viewPager);
        indicator=(TabLayout)root.findViewById(R.id.indicator);
        setCarouselViewPager(); //to implement carousel using viewpager

        mMessagesDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Potholes");
        mMessagesDatabaseReference.keepSynced(true);
        recyclerView = (RecyclerView) root.findViewById(R.id.recycler_view);


        friendlyMessages = new ArrayList<>();
        layoutManager = new GridLayoutManager(getContext(), 1);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setHasFixedSize(true);

        mMessagesDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                friendlyMessages.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    UserData studentDetails = dataSnapshot.getValue(UserData.class);
                    friendlyMessages.add(studentDetails);
                }

                MyReportsAdapter adapter = new MyReportsAdapter(getContext(), friendlyMessages);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        return root;
    }


    //carousel/slider implementation function
    public  void setCarouselViewPager(){

                sliderText = new ArrayList<>();
                sliderText.add(sliderText1);
                sliderText.add(sliderText2);
                sliderText.add(sliderText4);
                sliderText.add(sliderText5);

                sliderImages=new ArrayList<Integer>();
                sliderImages.add(R.drawable.carousel_1);
                sliderImages.add(R.drawable.carousel_2);
                sliderImages.add(R.drawable.carousel_4);
                sliderImages.add(R.drawable.carousel_5);

                viewPager.setAdapter(new SliderAdapter(getActivity(), sliderImages, sliderText));
                indicator.setupWithViewPager(viewPager, true);

                Timer timer = new Timer();
                timer.scheduleAtFixedRate(new SliderTimer(), 5000, 6000);
            }

    //carousel image auto-slider
    public class SliderTimer extends TimerTask {
        @Override
        public void run() {

           if(getActivity()!=null) {

               getActivity().runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       if (viewPager.getCurrentItem() < sliderImages.size() - 1) {
                           viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                       } else {
                           viewPager.setCurrentItem(0);
                       }
                   }
               });
           }else
               return;;
        }
    }




}
