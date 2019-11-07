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
        if(CollectionUtils.isEmpty(monitor.getStatuses())) {
            monitor.setMessage("Monitor hasn't Started yet");
        }
        model.addAttribute("monitor", monitor);
        return "monitor"; //view
    }

    @PostMapping("/")
    public String monitorSubmit(@ModelAttribute Monitor newMonitor) {
        int newInterval = newMonitor.getInterval();
        if(Monitor.isIntervalValid(newInterval)){
          monitor.resetStatuses();
          monitor.setInterval(newInterval);
        } else {
          monitor.setMessage("Minimum Interval is 300ms");
        }
        String newUrl = newMonitor.getUrl();
        if(Monitor.isUrlValid(newUrl)){
          if(!newUrl.equals(monitor.getUrl())) {
             monitor.resetStatuses();
          }
          monitor.setUrl(newUrl);
        } else {
          monitor.setMessage("URL is invalid");
          return "monitor"; //view
        }
        worker = new Thread(this);
        worker.start();
        monitor.setMessage("Monitor is Started");
        newMonitor = monitor;
        return "monitor"; //view
    }

    @GetMapping("/stop")
    public String stop(Model model) {
        running.set(false);
        monitor.resetStatuses();
        monitor.setMessage("Monitor is Stopped for: " + monitor.getUrl());
        model.addAttribute("monitor", monitor);
        return "monitor"; //view
    }

    public void run() {
        running.set(true);
        while (running.get()) {
            try {
                Thread.sleep(monitor.getInterval());
            } catch (InterruptedException e){
                Thread.currentThread().interrupt();
                System.out.println("Thread was interrupted, Failed to complete operation");
            }
            getStatus();
         }
    }

    public void getStatus() {
        try {
            URL url = monitor.getHttpUrl();
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");
            String response = "Down";
            int responseCode = con.getResponseCode();
            if(responseCode == 200){
                response = "Up";
            }
            int timeout = 5000;
            int interval = monitor.getInterval();
            if(interval < timeout){
                timeout = interval;
            }
            con.setConnectTimeout(timeout);
            monitor.addStatus(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
