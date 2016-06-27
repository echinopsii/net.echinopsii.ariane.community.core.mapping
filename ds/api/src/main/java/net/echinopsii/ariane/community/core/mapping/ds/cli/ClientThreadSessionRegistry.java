package net.echinopsii.ariane.community.core.mapping.ds.cli;

import java.util.HashMap;
import java.util.Map;

public class ClientThreadSessionRegistry {
    private final static Map<String, String> sessionThreadPoolRegistry = new HashMap<>();

    public static String addCliThreadSession(String threadId, String sessionID) {
        return sessionThreadPoolRegistry.put(threadId, sessionID);
    }

    public static String removeCliThreadSession(String threadId) {
        return sessionThreadPoolRegistry.remove(threadId);
    }

    public static String getSessionFromThread(String threadId) {
        return sessionThreadPoolRegistry.get(threadId);
    }
}
