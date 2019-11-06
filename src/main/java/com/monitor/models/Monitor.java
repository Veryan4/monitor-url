package com.monitor;

import java.util.List;

public class Monitor {

    private int interval;
    private List<Status> statuses;
    private String url;
    private String message;

    public Monitor() {
    }

    public Monitor(int interval, String url, List<Status> statuses, String message) {
        this.interval = interval;
        this.url = url;
        this.statuses = statuses;
        this.message = message;
    }

    public int getInterval() {
        return interval;
    }

    public Monitor setInterval(int interval) {
        this.interval = interval;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public Monitor setUrl(String url) {
        this.url = url;
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
