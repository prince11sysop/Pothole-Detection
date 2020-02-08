package teamenigma.com.potholedetection.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import teamenigma.com.potholedetection.MyReportsAdapter;
import teamenigma.com.potholedetection.R;
import teamenigma.com.potholedetection.UserData;

public class HomeFragment extends Fragment {


    DatabaseReference mMessagesDatabaseReference;
    RecyclerView recyclerView;
    GridLayoutManager layoutManager;
    List<UserData> friendlyMessages;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);


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


}