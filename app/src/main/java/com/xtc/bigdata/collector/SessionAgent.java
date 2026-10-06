package com.xtc.bigdata.collector;

import java.util.UUID;

/** Generates the session id used by the collector. */
public class SessionAgent {

    private static String sessionId = UUID.randomUUID().toString();

    private SessionAgent() {
    }

    public static String getSessionId() {
        return sessionId;
    }

    public static void newSession() {
        sessionId = UUID.randomUUID().toString();
    }
}