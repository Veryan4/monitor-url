package com.monitor;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import com.monitor.models.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Controller
public class MonitorController implements Runnable {

    private Monitor monitor = new Monitor();
    private Thread worker;
    private final AtomicBoolean running = new AtomicBoolean(false);

    @GetMapping("/")
    public String main(Model model) {
        if (!this.running.get()) {
            this.monitor.message = "Monitor is Not Running";
        }
        model.addAttribute("monitor", this.monitor);
        return "monitor"; //view in monitor.html
    }

    @PostMapping("/")
    public String monitorSubmit(@ModelAttribute Monitor newMonitor) {
        int newInterval = newMonitor.interval;
        if (Monitor.isIntervalValid(newInterval)) {
            this.monitor.interval = newInterval;
        } else {
            this.monitor.message = "Minimum Interval is 300ms";
        }
        String newUrl = newMonitor.url;
        if (Monitor.isUrlValid(newUrl)) {
            if (!newUrl.equals(this.monitor.url)) {
                this.monitor.resetStatuses();
            }
            this.monitor.url = newUrl;
        } else {
            this.monitor.message = "URL is invalid";
            return "monitor"; //view in monitor.html
        }
        this.monitor.message = "Monitor is Running";
        //@ModelAttribute seems to stop me from passing properties of this.monitor to newMonitor
        newMonitor.message = "Monitor is Running";
        newMonitor.statuses = this.monitor.statuses;
        this.worker = new Thread(this);
        this.worker.start();
        return "monitor"; //view in monitor.html
    }

    @GetMapping("/stop")
    public String stop(Model model) {
        this.running.set(false);
        this.worker.stop();
        this.monitor.message ="Monitor is Stopped";
        model.addAttribute("monitor", this.monitor);
        return "monitor"; //view in monitor.html
    }

    public void run() {
        this.running.set(true);
        while (this.running.get()) {
            try {
                Thread.sleep(this.monitor.interval);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Thread was interrupted, Failed to complete operation");
            }
            getStatus();
        }
    }

    public void getStatus() {
        try {
            URL url = this.monitor.getHttpUrl();
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");
            String response = "Down";
            int responseCode = con.getResponseCode();
            if (responseCode == 200) {
                response = "Up";
            }
            int timeout = 5000;
            int interval = monitor.interval;
            if (interval < timeout) {
                timeout = interval;
            }
            con.setConnectTimeout(timeout);
            this.monitor.addStatus(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
