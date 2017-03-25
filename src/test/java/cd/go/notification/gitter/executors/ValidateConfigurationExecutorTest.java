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

import cd.go.notification.gitter.requests.ValidatePluginSettings;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ValidateConfigurationExecutorTest {
    @Test
    public void shouldValidateABadConfiguration() throws Exception {
        ValidatePluginSettings settings = new ValidatePluginSettings();
        GoPluginApiResponse response = new ValidateConfigurationExecutor(settings).execute();

        assertThat(response.responseCode(), is(200));
        JSONAssert.assertEquals("[\n" +
                "  {\n" +
                "    \"message\": \"Server Base URL must not be blank.\",\n" +
                "    \"key\": \"server_base_url\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"message\": \"Gitter Token must not be blank.\",\n" +
                "    \"key\": \"gitter_token\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"message\": \"Gitter Room ID must not be blank.\",\n" +
                "    \"key\": \"gitter_room_id\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"message\": \"White listed pipeline groups must not be blank.\",\n" +
                "    \"key\": \"white_listed_pipeline_groups\"\n" +
                "  }\n" +
                "]", response.responseBody(), true);
    }

    @Test
    public void shouldValidateAGoodConfiguration() throws Exception {
        ValidatePluginSettings settings = new ValidatePluginSettings();
        settings.put("server_base_url", "go.server.url");
        settings.put("gitter_token", "some-token");
        settings.put("gitter_room_id", "some-room-id");
        settings.put("white_listed_pipeline_groups", "internal,dev");;
        GoPluginApiResponse response = new ValidateConfigurationExecutor(settings).execute();

        assertThat(response.responseCode(), is(200));
        JSONAssert.assertEquals("[]", response.responseBody(), true);
    }
}
