package org.archangel.think;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.logging.Logger;

import java.time.temporal.ChronoUnit;

@Path("/analyze")
@RegisterRestClient(configKey = "logic-api")
public interface LogicInterface {

    Logger log = Logger.getLogger(LogicInterface.class);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Timeout(value = 8, unit = ChronoUnit.SECONDS)
    @CircuitBreaker(
            requestVolumeThreshold = 3,
            failureRatio = 0.66,
            delay = 30,
            delayUnit = ChronoUnit.SECONDS,
            successThreshold = 2
    )

    @Fallback(
            fallbackMethod = "fallbackAnalyze",
            applyOn = {
                    org.eclipse.microprofile.faulttolerance.exceptions.CircuitBreakerOpenException.class,
                    org.eclipse.microprofile.faulttolerance.exceptions.TimeoutException.class
            }
    )
    GeneratedResponse analyze(AnalyzeRequest request);
    default GeneratedResponse fallbackAnalyze(AnalyzeRequest request) {
        log.warn("Brain service fallback triggered — circuit open or timeout. " +
                "Run: systemctl status archangel-brain  or  GET /system/health");

        GeneratedResponse r = new GeneratedResponse();
        r.setSeverity(GeneratedResponse.Severity.UNKNOWN);
        r.setSummary("AI analysis service is currently unavailable. " +
                "Logs were collected but not analyzed.");
        r.setRecommendedAction(
                "1. Check brain service: systemctl status archangel-brain\n" +
                        "2. Verify Ollama is running: curl http://localhost:11434/api/tags\n" +
                        "3. Call GET /system/health for a structured diagnosis"
        );
        return r;
    }
}