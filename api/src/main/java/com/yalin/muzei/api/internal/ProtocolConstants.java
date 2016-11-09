package com.yalin.muzei.api.internal;

/**
 * YaLin
 * 2016/11/9.
 */

public class ProtocolConstants {
    public static final String ACTION_SUBSCRIBE = "com.yalin.muzei.api.action.SUBSCRIBE";
    public static final String EXTRA_SUBSCRIBER_COMPONENT = "com.yalin.muzei.api.extra.SUBSCRIBER_COMPONENT";
    public static final String EXTRA_TOKEN = "com.yalin.muzei.api.extra.TOKEN";

    public static final String ACTION_HANDLE_COMMAND = "com.yalin.muzei.api.action.HANDLE_COMMAND";
    public static final String EXTRA_COMMAND_ID = "com.yalin.muzei.api.extra.COMMAND_ID";
    public static final String EXTRA_SCHEDULED = "com.yalin.muzei.api.extra.SCHEDULED";

    public static final String ACTION_NETWORK_AVAILABLE = "com.yalin.muzei.api.action.NETWORK_AVAILABLE";

    // Sent intents
    public static final String ACTION_PUBLISH_STATE = "com.yalin.muzei.api.action.PUBLISH_UPDATE";
    public static final String EXTRA_STATE = "com.yalin.muzei.api.extra.STATE";

    private ProtocolConstants() {
    }
}
