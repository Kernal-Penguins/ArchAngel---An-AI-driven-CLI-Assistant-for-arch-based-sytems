package org.archangel.system;

import java.util.Set;

public final class AllowedCommands {

    private AllowedCommands() {}

    public static final Set<String> SAFE_COMMANDS = Set.of(
            // System identity
            "uname -a",
            "hostname",
            "whoami",

            // Resource status
            "free -h",
            "df -h",
            "uptime",

            // Log retrieval — required by SystemLogService and the AI analysis pipeline
            "journalctl -p 3..5 -n 50",   // errors+warnings, last 50 (SystemLogService default)
            "journalctl -n 50",            // last 50 all priorities (used by CLI summary)
            "journalctl -n 100",           // extended fetch for deeper analysis
            "journalctl -b -p 3",          // errors since last boot

            "journalctl -p 3..5 -n 50 --no-pager --output=short-iso",

            // Package state — useful for security audits
            "pacman -Q",                   // list installed packages
            "pacman -Qu"                   // list upgradable packages
    );

    public static boolean isAllowed(String command) {
        if (command == null) return false;
        return SAFE_COMMANDS.contains(command.trim());
    }
}