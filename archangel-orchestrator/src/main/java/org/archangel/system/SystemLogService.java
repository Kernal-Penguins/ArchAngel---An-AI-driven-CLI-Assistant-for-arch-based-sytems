package org.archangel.system;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class SystemLogService
{
    @Inject
    SystemProcessExec systemProcessExec;

    public String fetchRecentLogs(){
        ProcessResult result = systemProcessExec.execute(List.of("journalctl", "-p", "3..5", "-n", "50"));

        if (result.isTimedOut() || result.getExitCode() != 0) {
            return "Failed to fetch logs: " + result.getStderr();
        }

        return result.getStdout();
    }
}