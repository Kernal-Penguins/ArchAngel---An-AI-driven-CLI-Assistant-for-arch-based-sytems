package org.archangel.think;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// This wrapper ensures the wire format matches AnalyzeRequest in brain/main.py exactly.

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalyzeRequest {

    @JsonProperty("logs")
    private String logs;

    @JsonProperty("source")
    private String source;

    public static AnalyzeRequest fromJournal(String logs) {
        return new AnalyzeRequest(logs, "journalctl");
    }
}