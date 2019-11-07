package com.monitor;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
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
        if (CollectionUtils.isEmpty(monitor.getStatuses())) {
            this.monitor.setMessage("Monitor hasn't Started yet");
        }
        model.addAttribute("monitor", this.monitor);
        return "monitor"; //view in monitor.html
    }

    @PostMapping("/")
    public String monitorSubmit(@ModelAttribute Monitor newMonitor) {
        int newInterval = newMonitor.getInterval();
        if (Monitor.isIntervalValid(newInterval)) {
            this.monitor.resetStatuses();
            this.monitor.setInterval(newInterval);
        } else {
            this.monitor.setMessage("Minimum Interval is 300ms");
        }
        String newUrl = newMonitor.getUrl();
        if (Monitor.isUrlValid(newUrl)) {
            if (!newUrl.equals(this.monitor.getUrl())) {
                this.monitor.resetStatuses();
            }
            this.monitor.setUrl(newUrl);
        } else {
            this.monitor.setMessage("URL is invalid");
            return "monitor"; //view in monitor.html
        }
        worker = new Thread(this);
        worker.start();
        this.monitor.setMessage("Monitor is Started");
        newMonitor = monitor;
        return "monitor"; //view in monitor.html
    }

    @GetMapping("/stop")
    public String stop(Model model) {
        this.running.set(false);
        this.monitor.resetStatuses();
        this.monitor.setMessage("Monitor is Stopped for: " + this.monitor.getUrl());
        model.addAttribute("monitor", this.monitor);
        return "monitor"; //view in monitor.html
    }

    public void run() {
        this.running.set(true);
        while (this.running.get()) {
            try {
                Thread.sleep(this.monitor.getInterval());
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
            int interval = monitor.getInterval();
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
