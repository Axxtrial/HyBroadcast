package com.hytalelatam.hyannounces.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration container for the plugin.
 */
public class PluginConfig {
    private String pluginName;
    private String version;
    private boolean debugMode;
    private boolean enableScheduledMessages;
    private boolean useUtc;
    private boolean simpleMode;
    private String toastPrefix;
    private List<ScheduledMessage> scheduledMessages;

    public PluginConfig() {
        this.scheduledMessages = new ArrayList<>();
        this.toastPrefix = "[ANNOUNCEMENT] "; // Default prefix
        this.enableScheduledMessages = true; // Default enabled
        this.useUtc = false; // Default local time
    }

    public String getPluginName() {
        return pluginName;
    }

    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    public boolean isEnableScheduledMessages() {
        return enableScheduledMessages;
    }

    public void setEnableScheduledMessages(boolean enableScheduledMessages) {
        this.enableScheduledMessages = enableScheduledMessages;
    }

    public boolean isUseUtc() {
        return useUtc;
    }

    public void setUseUtc(boolean useUtc) {
        this.useUtc = useUtc;
    }

    public boolean isSimpleMode() {
        return simpleMode;
    }

    public void setSimpleMode(boolean simpleMode) {
        this.simpleMode = simpleMode;
    }

    public String getToastPrefix() {
        return toastPrefix;
    }

    public void setToastPrefix(String toastPrefix) {
        this.toastPrefix = toastPrefix;
    }

    public List<ScheduledMessage> getScheduledMessages() {
        return scheduledMessages;
    }

    public void setScheduledMessages(List<ScheduledMessage> scheduledMessages) {
        this.scheduledMessages = scheduledMessages;
    }
}
