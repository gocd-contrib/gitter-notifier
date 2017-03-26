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

package cd.go.notification.gitter.executors;

import cd.go.notification.gitter.Gitter;
import cd.go.notification.gitter.PluginRequest;
import cd.go.notification.gitter.models.PluginSettings;
import cd.go.notification.gitter.requests.StageStatusRequest;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.util.Arrays;
import java.util.HashMap;

import static cd.go.notification.gitter.GitterNotifierPlugin.LOG;
import static java.util.Collections.singletonMap;

public class StageStatusRequestExecutor implements RequestExecutor {
    private static final Gson GSON = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
    private final Gitter gitter;

    private final StageStatusRequest request;
    private final PluginRequest pluginRequest;

    public StageStatusRequestExecutor(StageStatusRequest request, PluginRequest pluginRequest) {
        this.request = request;
        this.pluginRequest = pluginRequest;
        gitter = new Gitter();
    }

    //used in test
    StageStatusRequestExecutor(Gitter gitter, StageStatusRequest request, PluginRequest pluginRequest) {
        this.request = request;
        this.pluginRequest = pluginRequest;
        this.gitter = gitter;
    }

    @Override
    public GoPluginApiResponse execute() throws Exception {
        HashMap<String, Object> responseJson = new HashMap<>();
        try {
            sendNotification();
            responseJson.put("status", "success");
        } catch (Exception e) {
            responseJson.put("status", "failure");
            responseJson.put("messages", Arrays.asList(e.getMessage()));
        }
        return new DefaultGoPluginApiResponse(200, GSON.toJson(responseJson));
    }

    protected void sendNotification() throws Exception {
        PluginSettings pluginSettings = pluginRequest.getPluginSettings();
        if (!pluginSettings.isConfigured()) {
            LOG.warn("Plugin settings need to be configured in order to use this plugin.");
            return;
        }

        if (!pluginSettings.isWhiteListed(request.pipeline.group)) {
            LOG.info(String.format("Pipeline group %s is not in whitelist", request.pipeline.group));
            return;
        }

        if ("failed".equalsIgnoreCase(request.pipeline.stage.result)) {
            String message = buildMessage(pluginSettings);
            gitter.send(pluginSettings, message);
        }
    }

    private String buildMessage(PluginSettings pluginSettings) {
        String stageLocator = String.format("%s/%s/%s/%s", request.pipeline.name, request.pipeline.counter, request.pipeline.stage.name, request.pipeline.stage.counter);
        String trackbackURL = String.format("%s/go/pipelines/%s", pluginSettings.getGoServerUrl(), stageLocator);
        return GSON.toJson(singletonMap("text", String.format("[%s](%s) failed.", stageLocator, trackbackURL)));
    }
}
