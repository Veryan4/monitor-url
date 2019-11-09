import java.io.Serializable;
import java.util.Date;
import java.text.SimpleDateFormat;

public class Status implements Serializable {

    private String time;
    private String status;

    public Status(Date time, String status) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        this.time = dateFormat.format(time);
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public String getStatus() {
        return status;
    }

    public Status setStatus(String status) {
        this.status = status;
        return this;
    }

}
