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
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class StageStatusRequestExecutorTest {

    @Test
    public void shouldRenderASuccessResponseIfNotificationWasSent() throws Exception {
        GoPluginApiResponse response = new StageStatusRequestExecutor(null, null) {
            @Override
            protected void sendNotification() {
                // do nothing!
            }
        }.execute();

        assertThat(response.responseCode(), is(200));
        JSONAssert.assertEquals("{\"status\":\"success\"}", response.responseBody(), true);
    }

    @Test
    public void shouldRenderAnErrorResponseIfNotificationWasNotSent() throws Exception {
        GoPluginApiResponse response = new StageStatusRequestExecutor(null, null) {
            @Override
            protected void sendNotification() {
                throw new RuntimeException("Boom!");
            }
        }.execute();

        assertThat(response.responseCode(), is(200));
        JSONAssert.assertEquals("{\"status\":\"failure\",\"messages\":[\"Boom!\"]}", response.responseBody(), true);
    }

    @Test
    public void shouldSendNotificationForFailedPipeline() throws Exception {
        final Gitter gitter = mock(Gitter.class);
        final StageStatusRequest request = withPipeline("foo", "internal", "failed");
        final PluginRequest pluginRequest = mock(PluginRequest.class);
        final PluginSettings pluginSettings = mock(PluginSettings.class);

        when(pluginRequest.getPluginSettings()).thenReturn(pluginSettings);
        when(pluginSettings.isConfigured()).thenReturn(true);
        when(pluginSettings.isWhiteListed("internal")).thenReturn(true);

        final StageStatusRequestExecutor executor = new StageStatusRequestExecutor(gitter, request, pluginRequest);

        executor.execute();

        verify(gitter).send(eq(pluginSettings), anyString());
    }

    @Test
    public void shouldNotSendNotificationIfStageStatusIsNotFailed() throws Exception {
        final Gitter gitter = mock(Gitter.class);
        final StageStatusRequest request = withPipeline("foo", "internal", "bar");
        final PluginRequest pluginRequest = mock(PluginRequest.class);
        final PluginSettings pluginSettings = mock(PluginSettings.class);

        when(pluginRequest.getPluginSettings()).thenReturn(pluginSettings);
        when(pluginSettings.isConfigured()).thenReturn(true);

        final StageStatusRequestExecutor executor = new StageStatusRequestExecutor(gitter, request, pluginRequest);

        executor.execute();

        verifyZeroInteractions(gitter);
    }

    @Test
    public void shouldNotSendNotificationIfPluginSettingsNotInitialized() throws Exception {
        final Gitter gitter = mock(Gitter.class);
        final StageStatusRequest spyRequest = spy(withPipeline("foo", "internal", "failed"));
        final PluginRequest pluginRequest = mock(PluginRequest.class);
        final PluginSettings pluginSettings = mock(PluginSettings.class);

        when(pluginRequest.getPluginSettings()).thenReturn(pluginSettings);
        when(pluginSettings.isConfigured()).thenCallRealMethod();

        final StageStatusRequestExecutor executor = new StageStatusRequestExecutor(gitter, spyRequest, pluginRequest);

        executor.execute();

        verifyNoMoreInteractions(spyRequest);
        verifyZeroInteractions(gitter);
    }

    private StageStatusRequest withPipeline(String name, String group, String result) {
        return StageStatusRequest.fromJSON(String.format("{\n" +
                "  \"pipeline\": {\n" +
                "    \"name\": \"%s\",\n" +
                "    \"group\": \"%s\",\n" +
                "    \"counter\": \"1\",\n" +
                "    \"stage\": {\n" +
                "      \"result\": \"%s\",\n" +
                "      \"name\": \"test-stage\",\n" +
                "      \"counter\": \"2\"\n" +
                "    }\n" +
                "  }\n" +
                "}", name, group, result));
    }
}
