package com.tw.go.plugin;

public class PluginSettings {
    private String serverBaseURL;
    private String gitterToken;
    private String gitterRoomId;

    public PluginSettings(String serverBaseURL, String gitterToken, String gitterRoomId) {
        this.serverBaseURL = serverBaseURL;
        this.gitterToken = gitterToken;
        this.gitterRoomId = gitterRoomId;
    }

    public String getServerBaseURL() {
        return serverBaseURL;
    }

    public void setServerBaseURL(String serverBaseURL) {
        this.serverBaseURL = serverBaseURL;
    }

    public String getGitterToken() {
        return gitterToken;
    }

    public void setGitterToken(String gitterToken) {
        this.gitterToken = gitterToken;
    }

    public String getGitterRoomId() {
        return gitterRoomId;
    }

    public void setGitterRoomId(String gitterRoomId) {
        this.gitterRoomId = gitterRoomId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PluginSettings that = (PluginSettings) o;

        if (gitterRoomId != null ? !gitterRoomId.equals(that.gitterRoomId) : that.gitterRoomId != null) return false;
        if (gitterToken != null ? !gitterToken.equals(that.gitterToken) : that.gitterToken != null) return false;
        if (serverBaseURL != null ? !serverBaseURL.equals(that.serverBaseURL) : that.serverBaseURL != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = serverBaseURL != null ? serverBaseURL.hashCode() : 0;
        result = 31 * result + (gitterToken != null ? gitterToken.hashCode() : 0);
        result = 31 * result + (gitterRoomId != null ? gitterRoomId.hashCode() : 0);
        return result;
    }
}
