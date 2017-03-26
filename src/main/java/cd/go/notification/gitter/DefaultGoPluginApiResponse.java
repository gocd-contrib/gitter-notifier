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

import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.util.HashMap;
import java.util.Map;

public class DefaultGoPluginApiResponse extends GoPluginApiResponse {

    public static final int SUCCESS_RESPONSE_CODE = 200;
    public static final int VALIDATION_FAILED = 412;
    public static final int BAD_REQUEST = 400;
    public static final int INTERNAL_ERROR = 500;

    private int responseCode;

    private Map<String, String> responseHeaders;

    private String responseBody;

    public DefaultGoPluginApiResponse(int responseCode) {
        this(responseCode, null, new HashMap<String, String>());
    }

    public DefaultGoPluginApiResponse(int responseCode, String responseBody) {
        this(responseCode, responseBody, new HashMap<String, String>());
    }

    public DefaultGoPluginApiResponse(int responseCode, String responseBody, Map<String, String> responseHeaders) {
        this.responseCode = responseCode;
        this.responseBody = responseBody;
        this.responseHeaders = responseHeaders;
    }

    public static DefaultGoPluginApiResponse incompleteRequest(String responseBody) {
        return new DefaultGoPluginApiResponse(VALIDATION_FAILED, responseBody);
    }

    public static DefaultGoPluginApiResponse badRequest(String responseBody) {
        return new DefaultGoPluginApiResponse(BAD_REQUEST, responseBody);
    }

    public static DefaultGoPluginApiResponse error(String responseBody) {
        return new DefaultGoPluginApiResponse(INTERNAL_ERROR, responseBody);
    }

    public static DefaultGoPluginApiResponse success(String responseBody) {
        return new DefaultGoPluginApiResponse(SUCCESS_RESPONSE_CODE, responseBody);
    }

    public void addResponseHeader(String name, String value) {
        responseHeaders.put(name, value);
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    @Override
    public int responseCode() {
        return responseCode;
    }

    @Override
    public Map<String, String> responseHeaders() {
        return responseHeaders;
    }

    @Override
    public String responseBody() {
        return responseBody;
    }
}
