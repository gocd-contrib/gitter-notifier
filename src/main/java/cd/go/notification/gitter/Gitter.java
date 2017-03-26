/*
 *  Copyright 2017. ThoughtWorks, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package cd.go.notification.gitter;

import cd.go.notification.gitter.models.PluginSettings;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;

import static cd.go.notification.gitter.GitterNotifierPlugin.LOG;

public class Gitter {
    public static final String GITTER_END_POINT = "https://api.gitter.im/v1/rooms/%s/chatMessages";

    public void send(PluginSettings pluginSettings, String message) {
        try {
            postRequest(endPoint(pluginSettings.getGitterRoomId()), pluginSettings.getGitterToken(), message);
        } catch (IOException e) {
            LOG.error("Failed to post message to gitter endpoint", e);
        }
    }

    private String endPoint(String roomId) {
        return String.format(GITTER_END_POINT, roomId);
    }

    private int postRequest(String endpoint, String accessToken, String payloadJSON) throws IOException {
        HttpsURLConnection connection = httpsURLConnection(endpoint, accessToken);
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

    private HttpsURLConnection httpsURLConnection(String endpoint, String accessToken) throws IOException {
        URL url = new URL(endpoint);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + accessToken);
        connection.setDoOutput(true);
        return connection;
    }

}
