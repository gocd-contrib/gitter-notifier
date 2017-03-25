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
import org.junit.Test;

import static cd.go.notification.gitter.models.PluginSettings.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PluginSettingsTest {

    @Test
    public void shouldDeserializeFromJSON() throws Exception {
        final PluginSettings pluginSettings = PluginSettings.fromJSON("{\n" +
                "  \"server_base_url\": \"go.server.url\",\n" +
                "  \"gitter_token\": \"some-token\",\n" +
                "  \"gitter_room_id\": \"some-room-id\",\n" +
                "  \"white_listed_pipeline_groups\": \"internal,dev\"\n" +
                "}");

        assertThat(pluginSettings.getGoServerUrl(), is("go.server.url"));
        assertThat(pluginSettings.getGitterToken(), is("some-token"));
        assertThat(pluginSettings.getGitterRoomId(), is("some-room-id"));
        assertThat(pluginSettings.isWhiteListed("internal"), is(true));
        assertThat(pluginSettings.isWhiteListed("dev"), is(true));
        assertThat(pluginSettings.isWhiteListed("foo"), is(false));
    }

    @Test
    public void shouldReadSystemPropertyIfPluginSettingIsNotConfigured() throws Exception {
        System.setProperty(GO_PLUGIN_BUILD_STATUS_GO_SERVER, "go.server.url");
        System.setProperty(GO_PLUGIN_BUILD_STATUS_GITTER_ROOM_ID, "some-room-id");
        System.setProperty(GO_PLUGIN_BUILD_STATUS_GITTER_TOKEN, "some-token");

        final PluginSettings pluginSettings = PluginSettings.fromJSON("{}");
        assertThat(pluginSettings.getGoServerUrl(), is("go.server.url"));
        assertThat(pluginSettings.getGitterToken(), is("some-token"));
        assertThat(pluginSettings.getGitterRoomId(), is("some-room-id"));
    }
}
