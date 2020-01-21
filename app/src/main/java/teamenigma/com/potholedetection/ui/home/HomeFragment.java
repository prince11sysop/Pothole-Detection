package teamenigma.com.potholedetection.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.LocationRequest;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import teamenigma.com.potholedetection.R;

public class HomeFragment extends Fragment {

    LocationRequest mLocationRequest;

    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);



        return root;
    }


}