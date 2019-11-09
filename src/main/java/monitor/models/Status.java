package monitor;

import java.io.Serializable;
import java.util.Date;
import java.text.SimpleDateFormat;

public class Status implements Serializable {

    private String time;
    private String status;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    public Status(Date time, String status) {
        this.time = dateFormat.format(time);
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public Status setTime(Date time) {
        this.time = dateFormat.format(time);
        return this;
    }

    public Status setTime(String time) {
        this.time = time;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public Status setStatus(String status) {
        this.status = status;
        return this;
    }

}
