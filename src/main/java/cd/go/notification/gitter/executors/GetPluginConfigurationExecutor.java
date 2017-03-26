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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cd.go.notification.gitter.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.util.LinkedHashMap;
import java.util.Map;

public class GetPluginConfigurationExecutor implements RequestExecutor {

    private static final Gson GSON = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    public static final Field GO_SERVER_URL = new NonBlankField("server_base_url", "Server Base URL", null, false, "0");
    public static final Field GITTER_TOKEN = new NonBlankField("gitter_token", "Gitter Token", null, false, "1");
    public static final Field GITTER_ROOM_ID = new NonBlankField("gitter_room_id", "Gitter Room ID", null, false, "2");
    public static final Field WHITE_LISTED_PIPELINE_GROUPS = new Field("white_listed_pipeline_groups", "White listed pipeline groups", null, false, false, "3");

    public static final Map<String, Field> FIELDS = new LinkedHashMap<>();

    static {
        FIELDS.put(GO_SERVER_URL.key(), GO_SERVER_URL);
        FIELDS.put(GITTER_TOKEN.key(), GITTER_TOKEN);
        FIELDS.put(GITTER_ROOM_ID.key(), GITTER_ROOM_ID);
        FIELDS.put(WHITE_LISTED_PIPELINE_GROUPS.key(), WHITE_LISTED_PIPELINE_GROUPS);
    }

    public GoPluginApiResponse execute() {
        return new DefaultGoPluginApiResponse(200, GSON.toJson(FIELDS));
    }
}
