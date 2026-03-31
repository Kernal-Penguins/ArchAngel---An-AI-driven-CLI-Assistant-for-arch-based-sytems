package org.archangel.think;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

@ApplicationScoped
public class LogicAnalyzeExceptionHandler {

    private static final Logger log = Logger.getLogger(LogicAnalyzeExceptionHandler.class);

    @Inject
    @RestClient
    LogicInterface logic;

    public GeneratedResponse analyzeLogs(String logs) {
        try {
            AnalyzeRequest request = AnalyzeRequest.fromJournal(logs);
            GeneratedResponse response;
            try {
                response = logic.analyze(request);
            } catch (Exception e) {

                log.error("AI service call failed", e);
                return degradedResponse("AI service call failed: " + causeMessage(e));
            }

            validateResponse(response);

            if (!response.isAnalysisValid()) {
                log.warn("AI returned degraded or incomplete response — " +
                        "check archangel-brain service and Ollama model availability");
            }

            return response;

        } catch (Exception e) {
            log.errorf(e, "Unexpected error during log analysis");
            return degradedResponse("Unexpected error during analysis: " + causeMessage(e));
        }
    }

    private void validateResponse(GeneratedResponse r) {
        if (r.getSeverity() == null) {
            r.setSeverity(GeneratedResponse.Severity.UNKNOWN);
        }
        if (r.getSummary() == null) {
            r.setSummary("No summary returned.");
        }
        if (r.getRecommendedAction() == null) {
            r.setRecommendedAction("No action recommended.");
        }
    }

    private GeneratedResponse degradedResponse(String reason) {
        GeneratedResponse r = new GeneratedResponse();
        r.setSeverity(GeneratedResponse.Severity.UNKNOWN);
        r.setSummary(reason);
        r.setRecommendedAction(
                "Check archangel-brain service: systemctl status archangel-brain\n" +
                        "For a structured diagnosis call: GET /system/health"
        );
        return r;
    }

    /**
     * Walk the exception cause chain to find the first non-null message.
     * RestClientException often wraps the real cause (e.g. ConnectException).
     */
    private String causeMessage(Throwable t) {
        Throwable current = t;
        while (current != null) {
            if (current.getMessage() != null && !current.getMessage().isBlank()) {
                return current.getClass().getSimpleName() + ": " + current.getMessage();
            }
            current = current.getCause();
        }
        return t.getClass().getName();
    }
}