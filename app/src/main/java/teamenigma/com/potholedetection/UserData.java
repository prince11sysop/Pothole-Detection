package teamenigma.com.potholedetection;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UserData {
    public String userId;
    public String postalCode;
    public String state;
    public String traffic;
    public String imageURL;
    public String status;
    public String severinity;
    public String currentDate;
    public String currentTime;
    public String potholeAddress;
    public String potholeLatitude;
    public String potholeLongitude;
    public String numOfTimesReported;
    public String uploadKey;
    public String isItPothole;



    public UserData() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public UserData(String uid, String postalCode, String state,String sev, String traffic,String url,
                    String status,String potholeAddress,String potholeLatitude,String potholeLongitude,
                    String numOfTimesReported, String uploadKey,String isItPothole){
        this.userId = uid;
        this.postalCode = postalCode;
        this.state = state;
        this.traffic = traffic;
        this.imageURL = url;
        this.severinity = sev;
        this.status = status;
        this.currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        this.currentTime = new SimpleDateFormat("hh : mm a", Locale.getDefault()).format(Calendar.getInstance().getTime());
        this.potholeAddress=potholeAddress;
        this.potholeLatitude=potholeLatitude;
        this.potholeLongitude=potholeLongitude;
        this.numOfTimesReported=numOfTimesReported;
        this.uploadKey=uploadKey;
        this.isItPothole=isItPothole;
    }

    public String getUserId() {
        return userId;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getState() {
        return state;
    }

    public String getTraffic() {
        return traffic;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getStatus() {
        return status;
    }

    public String getSeverinity() {
        return severinity;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public String getPotholeAddress() {
        return potholeAddress;
    }

    public String getPotholeLatitude() {
        return potholeLatitude;
    }

    public String getPotholeLongitude() {
        return potholeLongitude;
    }

    public String getNumOfTimesReported() {
        return numOfTimesReported;
    }

    public void setNumOfTimesReported(String numOfTimesReported) {
        this.numOfTimesReported = numOfTimesReported;
    }

    public String getUploadKey() {
        return uploadKey;
    }

    public void setUploadKey(String uploadKey) {
        this.uploadKey = uploadKey;
    }

    public String getIsItPothole() {
        return isItPothole;
    }

    public void setIsItPothole(String isItPothole) {
        this.isItPothole = isItPothole;
    }
}
