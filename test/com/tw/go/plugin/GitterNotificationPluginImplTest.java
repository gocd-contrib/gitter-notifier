package com.tw.go.plugin;

import org.junit.Test;

public class GitterNotificationPluginImplTest {
    @Test
    public void shouldNotifyGitter() throws Exception {
        String accessToken = "";
        String roomId = "553f214e15522ed4b3df9cd4"; // gocd
        String goServer = "build.go.cd";
        new GitterNotificationPluginImpl().notifyGitter(accessToken, roomId, goServer, "acceptance_with_jetty_9/2/twist-plugins/1");
    }
}
