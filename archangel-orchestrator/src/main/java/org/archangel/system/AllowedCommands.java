package org.archangel.system;

import java.util.Set;

public class AllowedCommands
{
    public static final Set<String> Safe_Commands = Set.of(
            "uname -a",
            "free -h",
            "df -h",
            "uptime",
            "whoami",
            "hostname"
    );
    public boolean isAllowed
}
