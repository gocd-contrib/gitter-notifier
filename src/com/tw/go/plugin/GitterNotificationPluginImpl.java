package com.tw.go.plugin;

import com.google.gson.GsonBuilder;
import com.thoughtworks.go.plugin.api.GoApplicationAccessor;
import com.thoughtworks.go.plugin.api.GoPlugin;
import com.thoughtworks.go.plugin.api.GoPluginIdentifier;
import com.thoughtworks.go.plugin.api.annotation.Extension;
import com.thoughtworks.go.plugin.api.logging.Logger;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.*;

import static java.util.Arrays.asList;

@Extension
public class GitterNotificationPluginImpl implements GoPlugin {
    private static Logger LOGGER = Logger.getLoggerFor(GitterNotificationPluginImpl.class);

    public static final String EXTENSION_NAME = "notification";
    private static final List<String> goSupportedVersions = asList("1.0");

    public static final String REQUEST_NOTIFICATIONS_INTERESTED_IN = "notifications-interested-in";
    public static final String REQUEST_STAGE_STATUS = "stage-status";

    public static final int SUCCESS_RESPONSE_CODE = 200;
    public static final int INTERNAL_ERROR_RESPONSE_CODE = 500;

    @Override
    public void initializeGoApplicationAccessor(GoApplicationAccessor goApplicationAccessor) {
        // ignore
    }

    @Override
    public GoPluginApiResponse handle(GoPluginApiRequest goPluginApiRequest) {
        if (goPluginApiRequest.requestName().equals(REQUEST_NOTIFICATIONS_INTERESTED_IN)) {
            return handleNotificationsInterestedIn();
        } else if (goPluginApiRequest.requestName().equals(REQUEST_STAGE_STATUS)) {
            return handleStageNotification(goPluginApiRequest);
        }
        return null;
    }

    @Override
    public GoPluginIdentifier pluginIdentifier() {
        return new GoPluginIdentifier(EXTENSION_NAME, goSupportedVersions);
    }

    private GoPluginApiResponse handleNotificationsInterestedIn() {
        Map<String, Object> response = new HashMap<String, Object>();
        response.put("notifications", Arrays.asList(REQUEST_STAGE_STATUS));
        return renderJSON(SUCCESS_RESPONSE_CODE, response);
    }

    private GoPluginApiResponse handleStageNotification(GoPluginApiRequest goPluginApiRequest) {
        Map<String, Object> dataMap = getMapFor(goPluginApiRequest);

        int responseCode = SUCCESS_RESPONSE_CODE;
        Map<String, Object> response = new HashMap<String, Object>();
        List<String> messages = new ArrayList<String>();
        try {
            Map<String, Object> pipelineMap = (Map<String, Object>) dataMap.get("pipeline");
            Map<String, Object> stageMap = (Map<String, Object>) pipelineMap.get("stage");

            String stageResult = (String) stageMap.get("result");
            if (!isEmpty(stageResult) && stageResult.equalsIgnoreCase("Failed")) {
                String accessToken = System.getProperty("go.plugin.build.status.gitter.token");
                String roomId = System.getProperty("go.plugin.build.status.gitter.roomId");
                String goServer = System.getProperty("go.plugin.build.status.go-server");
                String stageLocator = String.format("%s/%s/%s/%s", pipelineMap.get("name"), pipelineMap.get("counter"), stageMap.get("name"), stageMap.get("counter"));

                notifyGitter(accessToken, roomId, goServer, stageLocator);
            }

            response.put("status", "success");
        } catch (Exception e) {
            LOGGER.warn("Error occurred while trying to deliver notification.", e);

            responseCode = INTERNAL_ERROR_RESPONSE_CODE;
            response.put("status", "failure");
            if (!isEmpty(e.getMessage())) {
                messages.add(e.getMessage());
            }
        }

        if (!messages.isEmpty()) {
            response.put("messages", messages);
        }
        return renderJSON(responseCode, response);
    }

    void notifyGitter(String accessToken, String roomId, String goServer, String stageLocator) {
        String endpoint = String.format("https://api.gitter.im/v1/rooms/%s/chatMessages", roomId);
        String trackbackURL = String.format("https://%s/go/pipelines/%s", goServer, stageLocator);
        String message = String.format("[%s](%s) failed.", stageLocator, trackbackURL);

        Map<String, String> payload = new HashMap<String, String>();
        payload.put("text", message);
        String payloadJSON = getJSONFor(payload);

        LOGGER.info("Sending Gitter Notification - " + message);

        try {
            int responseCode = postRequest(endpoint, accessToken, payloadJSON);

            // handle non 200 response
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        LOGGER.info("Successfully delivered notification.");
    }

    private int postRequest(String endpoint, String accessToken, String payloadJSON) throws Exception {
        URL urlObj = new URL(endpoint);
        HttpsURLConnection connection = (HttpsURLConnection) urlObj.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + accessToken);
        connection.setDoOutput(true);
        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
        writer.write(payloadJSON);
        writer.flush();
        writer.close();

        int responseCode = connection.getResponseCode();

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = reader.readLine()) != null) {
            response.append(inputLine);
        }
        reader.close();

        return responseCode;
    }

    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    private Map<String, Object> getMapFor(GoPluginApiRequest goPluginApiRequest) {
        return (Map<String, Object>) new GsonBuilder().create().fromJson(goPluginApiRequest.requestBody(), Object.class);
    }

    private String getJSONFor(Object response) {
        return new GsonBuilder().create().toJson(response);
    }

    private GoPluginApiResponse renderJSON(final int responseCode, Object response) {
        final String json = response == null ? null : getJSONFor(response);
        return new GoPluginApiResponse() {
            @Override
            public int responseCode() {
                return responseCode;
            }

            @Override
            public Map<String, String> responseHeaders() {
                return null;
            }

            @Override
            public String responseBody() {
                return json;
            }
        };
    }
}
