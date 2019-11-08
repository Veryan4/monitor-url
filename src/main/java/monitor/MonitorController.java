package monitor;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import monitor.models.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Controller
public class MonitorController implements Runnable {

    private Monitor monitor = new Monitor(new ArrayList < > (), 1000, "https://google.com", "");
    private Thread worker = new Thread(this);
    private final AtomicBoolean running = new AtomicBoolean(false);
    private SseEmitter emitter = new SseEmitter();
    private int count;

    @GetMapping("/")
    public String main(Model model) {
        if (!this.running.get()) {
            this.monitor.message = "Monitor is Not Running";
        }
        model.addAttribute("monitor", this.monitor);
        return "monitor"; //view in monitor.html
    }

    @GetMapping("/stream")
    public SseEmitter handleSse() {
        this.emitter = new SseEmitter();
        return this.emitter;
    }

    @PostMapping("/")
    public String start(@ModelAttribute Monitor newMonitor) {
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
        newMonitor.message = "Monitor is Running";
        newMonitor.statuses = this.monitor.statuses;
        if (!this.worker.isAlive() || this.worker.isInterrupted()) {
            this.worker.start();
        }
        return "monitor"; //view in monitor.html
    }

    @GetMapping("/stop")
    public String stop(Model model) {
        this.running.set(false);
        if (this.worker.isAlive()) {
            this.worker.interrupt();
            this.monitor.message = "Monitor is Stopped";
        }
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
            getStream();
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

    public void getStream() {
        ExecutorService sseMvcExecutor = Executors.newSingleThreadExecutor();
        sseMvcExecutor.execute(() - > {
            try {
                List < Status > statuses = this.monitor.getStatuses();
                SseEventBuilder event = SseEmitter.event()
                    .data(statuses.get(statuses.size() - 1))
                    .id(String.valueOf(this.count))
                    .name("message");
                this.emitter.send(event);
                this.count++;
            } catch (Exception ex) {
                this.emitter.completeWithError(ex);
            }
        });
    }

}
