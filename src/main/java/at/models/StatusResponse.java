package at.models;

import java.time.Instant; // timestamp

public class StatusResponse {
    public String status;    // status (ex: "ok")
    public String timestamp; // (hora atual)

    // Construtor
    public StatusResponse() {
        this.status = "ok";
        this.timestamp = Instant.now().toString(); // hora atual para ISO-8601
    }
}