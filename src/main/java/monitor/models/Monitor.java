package monitor;

import org.springframework.util.StringUtils;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.net.URL;

public class Monitor implements Serializable {

    public List<Status> statuses;
    public int interval;
    public String url;
    public String message;
    private final static int maxStatuses = 20;
    private final static int minInterval = 0;

    public Monitor(List<Status> statuses, int interval, String url, String message) {
        this.statuses = statuses;
        this.interval = interval;
        this.url = url;
        this.message = message;
    }

    public Monitor addStatus(String response) {
        Status status = new Status(new Date(), response);
        if (statuses.size() >= maxStatuses) {
            statuses.remove(0);
        }
        statuses.add(status);
        return this;
    }

    public Monitor resetStatuses() {
        statuses = new ArrayList<>();
        return this;
    }

    public static boolean isIntervalValid(int interval) {
        return interval > minInterval;
    }

    public static boolean isUrlValid(String url) {
        if (StringUtils.isEmpty(url)) {
            return false;
        }
        try {
            URL testUrl = new URL(url);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public URL getHttpUrl() {
        URL httpUrl = null;
        try {
            httpUrl = new URL(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return httpUrl;
    }

    public int getInterval() {
        return interval;
    }

    public Monitor setInterval(int interval) {
        if (isIntervalValid(interval)) {
            this.interval = interval;
        }
        return this;
    }

    public String getUrl() {
        return url;
    }

    public Monitor setUrl(String url) {
        if (isUrlValid(url)) {
            this.url = url;
        }
        return this;
    }

    public List<Status> getStatuses() {
        return statuses;
    }

    public Monitor setStatuses(List<Status> statuses) {
        this.statuses = statuses;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public Monitor setMessage(String message) {
        this.message = message;
        return this;
    }

}
