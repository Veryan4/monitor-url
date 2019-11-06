package com.monitor;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import com.monitor.models.*;
import java.net.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;


@Controller
public class MonitorController implements Runnable {

    private URL currentUrl;
    private int interval = 1000;
    private int maxStatuses = 19;
    private boolean isStart = false;
    private List<Status> statuses = new ArrayList<>();
    private Thread worker;
    private final AtomicBoolean running = new AtomicBoolean(false);

    @GetMapping("/")
    public String main(Model model) {
        if(currentUrl == null){
          try {
            this.currentUrl = new URL("https://google.com");
          } catch (Exception e) {
            //do nothing
          }
        }
        if(CollectionUtils.isEmpty(statuses)){
          model.addAttribute("monitor", new Monitor(interval, currentUrl.toString(), statuses, "Monitoring hasn't started yet"));
        } else {
          model.addAttribute("monitor", new Monitor(interval, currentUrl.toString(), statuses, ""));
        }
        return "monitor"; //view
    }

    @PostMapping("/")
    public String monitorSubmit(@ModelAttribute Monitor monitor) {
        int newInterval = monitor.getInterval();
        if(newInterval > 299 &&  newInterval != this.interval){
          ControlSubThread(newInterval);
          statuses = new ArrayList<>();
        } else {
          monitor.setInterval(this.interval);
        }
        String url = monitor.getUrl();
        if(!StringUtils.isEmpty(url)){
          try {
            URL urlToPush = new URL(url);
            if(!urlToPush.equals(this.currentUrl)) {
               statuses = new ArrayList<>();
               currentUrl = urlToPush;
            } else {
              monitor.setUrl(this.currentUrl.toString());
            }
          } catch (Exception e) {
            e.printStackTrace();
            monitor.setMessage("The URL was incorrect");
          }
          worker = new Thread(this);
          worker.start();
          monitor.setMessage("Monitor is Started");
        }
        //model.addAttribute("monitor", monitor);
        return "monitor";
    }

    @GetMapping("/stop")
    public String stop(Model model) {
        running.set(false);
        statuses = new ArrayList<>();
        if(currentUrl != null) {
          model.addAttribute("monitor", new Monitor(interval, currentUrl.toString(), statuses, "Monitor is Stopped for: " + currentUrl.toString()));
        } else{
          model.addAttribute("monitor", new Monitor(interval, currentUrl.toString(), statuses, "Monitor hasn't Started yet"));
        }
        return "monitor";
    }

    public void ControlSubThread(int sleepInterval) {
        interval = sleepInterval;
    }

    public void run() {
        running.set(true);
        while (running.get()) {
            try {
                Thread.sleep(interval);
            } catch (InterruptedException e){
                Thread.currentThread().interrupt();
                System.out.println(
                  "Thread was interrupted, Failed to complete operation");
            }
            getStatus();
         }
    }

    public void getStatus(){
        try{
            HttpURLConnection con = (HttpURLConnection) currentUrl.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");
            int responseCode = con.getResponseCode();
            String response = "Down";
            if(responseCode == 200){
                response = "Up";
            }
            int timeout = 5000;
            if(interval < timeout){
                timeout = interval;
            }
            con.setConnectTimeout(timeout);
            Status status = new Status(new Date(), response);

            if(statuses.size() > maxStatuses){
                statuses.remove(0);
            }
            statuses.add(status);

            Thread.sleep(interval);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
