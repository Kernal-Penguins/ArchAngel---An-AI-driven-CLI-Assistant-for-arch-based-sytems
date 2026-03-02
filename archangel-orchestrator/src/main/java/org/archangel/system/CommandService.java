package org.archangel.system;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.archangel.model.CommandResponse;

import java.util.List;

@ApplicationScoped
public class CommandService {

    @Inject
    SystemProcessExec systemProcessExec;

    public CommandResponse executeCommand(String command) {

        if (command == null || command.isBlank()) {
            return new CommandResponse(command, -1, "", "Command cannot be empty");
        }
        command = command.trim();
        if (command.contains("|") ||
                command.contains("&") ||
                command.contains(";") ||
                command.contains(">") ||
                command.contains("<")) {

            return new CommandResponse(command, -1, "",
                    "Chained or redirected commands are not allowed");
        }
        if (!AllowedCommands.isAllowed(command)) {
            return new CommandResponse(command, -1, "", "Command not allowed");
        }
        List<String> parts = List.of(command.split("\\s+"));

        ProcessResult  result = systemProcessExec.execute(parts);

        if (result.isTimedOut()) {
            return new CommandResponse(command, -1, "", "Command timed out");
        }

        return new CommandResponse(
                command,
                result.getExitCode(),
                result.getStdout(),
                result.getStderr()
        );
    }
}