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

package cd.go.notification.gitter.models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

import static cd.go.notification.gitter.utils.Util.listFromCommaSeparatedString;

public class PluginSettings {
    private static final Gson GSON = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    public static final String GO_PLUGIN_BUILD_STATUS_GITTER_TOKEN = "go.plugin.build.status.gitter.token";
    public static final String GO_PLUGIN_BUILD_STATUS_GITTER_ROOM_ID = "go.plugin.build.status.gitter.roomId";
    public static final String GO_PLUGIN_BUILD_STATUS_GO_SERVER = "go.plugin.build.status.go-server";
    public static final String GO_PLUGIN_BUILD_STATUS_WHITE_LISTED_PIPELINE_GROUP = "go.plugin.build.status.white_listed_pipeline_group";

    @Expose
    @SerializedName("server_base_url")
    private String goServerUrl;

    @Expose
    @SerializedName("gitter_token")
    private String gitterToken;

    @Expose
    @SerializedName("gitter_room_id")
    private String gitterRoomId;

    @Expose
    @SerializedName("white_listed_pipeline_groups")
    private String whiteListedPipelineGroups;

    public String getGitterToken() {
        return getFromSystemPropertyIfMissing(gitterToken, GO_PLUGIN_BUILD_STATUS_GITTER_TOKEN);
    }

    public String getGitterRoomId() {
        return getFromSystemPropertyIfMissing(gitterRoomId, GO_PLUGIN_BUILD_STATUS_GITTER_ROOM_ID);
    }

    public String getGoServerUrl() {
        return getFromSystemPropertyIfMissing(goServerUrl, GO_PLUGIN_BUILD_STATUS_GO_SERVER);
    }

    public boolean isWhiteListed(String pipelineGroupName) {
        final List<String> whiteList = listFromCommaSeparatedString(getFromSystemPropertyIfMissing(whiteListedPipelineGroups, GO_PLUGIN_BUILD_STATUS_WHITE_LISTED_PIPELINE_GROUP));
        return whiteList.isEmpty() || whiteList.contains(pipelineGroupName);
    }

    private String getFromSystemPropertyIfMissing(String currentValue, String propertyName) {
        if (StringUtils.isNotBlank(currentValue)) {
            return currentValue;
        }
        return System.getProperty(propertyName);
    }

    public boolean isConfigured() {
        return !(StringUtils.isBlank(getGitterRoomId()) ||
                StringUtils.isBlank(getGitterToken()) ||
                StringUtils.isBlank(getGoServerUrl()));
    }

    public static PluginSettings fromJSON(String json) {
        return GSON.fromJson(json, PluginSettings.class);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PluginSettings that = (PluginSettings) o;

        if (goServerUrl != null ? !goServerUrl.equals(that.goServerUrl) : that.goServerUrl != null) return false;
        if (gitterToken != null ? !gitterToken.equals(that.gitterToken) : that.gitterToken != null) return false;
        if (gitterRoomId != null ? !gitterRoomId.equals(that.gitterRoomId) : that.gitterRoomId != null) return false;
        return whiteListedPipelineGroups != null ? whiteListedPipelineGroups.equals(that.whiteListedPipelineGroups) : that.whiteListedPipelineGroups == null;
    }

    @Override
    public int hashCode() {
        int result = goServerUrl != null ? goServerUrl.hashCode() : 0;
        result = 31 * result + (gitterToken != null ? gitterToken.hashCode() : 0);
        result = 31 * result + (gitterRoomId != null ? gitterRoomId.hashCode() : 0);
        result = 31 * result + (whiteListedPipelineGroups != null ? whiteListedPipelineGroups.hashCode() : 0);
        return result;
    }
}
