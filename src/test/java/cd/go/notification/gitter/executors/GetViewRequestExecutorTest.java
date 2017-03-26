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

import cd.go.notification.gitter.utils.Util;
import com.google.gson.Gson;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class GetViewRequestExecutorTest {

    @Test
    public void shouldRenderTheTemplateInJSON() throws Exception {
        GoPluginApiResponse response = new GetViewRequestExecutor().execute();
        assertThat(response.responseCode(), is(200));
        Map<String, String> hashSet = new Gson().fromJson(response.responseBody(), HashMap.class);
        assertThat(hashSet, hasEntry("template", Util.readResource("/plugin-settings.template.html")));
    }

    @Test
    public void allFieldsShouldBePresentInView1() throws Exception {
        String template = Util.readResource("/plugin-settings.template.html");

        for (Map.Entry<String, Field> fieldEntry : GetPluginConfigurationExecutor.FIELDS.entrySet()) {
            assertThat(template, containsString("ng-model=\"" + fieldEntry.getKey() + "\""));
            assertThat(template, containsString("<span class=\"form_error\" ng-show=\"GOINPUTNAME[" + fieldEntry.getKey() +
                    "].$error.server\">{{GOINPUTNAME[" + fieldEntry.getKey() +
                    "].$error.server}}</span>"));
        }
    }

    @Test
    public void allFieldsShouldBePresentInView() throws Exception {
        String template = Util.readResource("/plugin-settings.template.html");
        final Document document = Jsoup.parse(template);

        for (Map.Entry<String, Field> fieldEntry : GetPluginConfigurationExecutor.FIELDS.entrySet()) {
            final Elements inputFieldForKey = document.getElementsByAttributeValue("ng-model", fieldEntry.getKey());
            assertThat(inputFieldForKey.size(), is(1));

            final Elements spanToShowError = document.getElementsByAttributeValue("ng-show", "GOINPUTNAME[" + fieldEntry.getKey() + "].$error.server");
            assertThat(spanToShowError.size(), is(1));
            assertThat(spanToShowError.text(), is("{{GOINPUTNAME[" + fieldEntry.getKey() + "].$error.server}}"));
        }
        final Elements inputs = document.select("textarea,input,select");
        assertThat("should contains only inputs that defined in ElasticProfile.java", inputs.size(), is(GetPluginConfigurationExecutor.FIELDS.size()));
    }

}
