package teamenigma.com.potholedetection;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ComplainDetails {
    public String uploadKey;
    public String currentDate;
    public String complainText;

    public ComplainDetails() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public ComplainDetails(String uploadKey, String complainText){
        this.currentDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(new Date());
        this.uploadKey=uploadKey;
        this.complainText = complainText;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public String getUploadKey() {
        return uploadKey;
    }

    public String getComplainText() { return complainText; }
}
