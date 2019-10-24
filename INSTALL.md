# GoCD build status notifier
This is GoCD's Notification plugin that updates build status (failed builds only) on Gitter.

## Requirements
* GoCD server version v15.x.x or above
* Gitter `room_id` and `access_token`

## Installation
- Download the latest plugin jar from [Releases](https://github.com/gocd-contrib/gitter-notifier/releases) section. Place it in `${GO_SERVER_DIR}/plugins/external` & restart Go Server. The `GO_SERVER_DIR` is usually `/var/lib/go-server` on Linux and `C:\Program Files\Go Server` on Windows.

## Configuration

#### Using plugin settings
* Navigate to *Admin > Plugin* in the main menu. You will see `Gitter Notification plugin` on plugin listing page
![Plugins listing page][1]

* * Click settings next to the plugin `Gitter Notification plugin` and enter the required plugin configuration. 
![Configure plugin pop-up][2]

* When the stage status changes...
![Successful Notification][3]

#### Using system properties

- You should provide `Go server url`, `token`, `roomId` and ``  through system property.
Eg:
```
-Dgo.plugin.build.status.gitter.token=thisainttoken
-Dgo.plugin.build.status.gitter.roomId=553f214e15522ed4b3df9cd4
-Dgo.plugin.build.status.go-server=build.go.cd
-Dgo.plugin.build.status.white_listed_pipeline_group=internal,dev
```

## Configuring the plugin for GoCD on Kubernetes using Helm

### Adding the plugin
- In order to add this plugin, you have to use a local values.yaml file that will override the default [values.yaml](https://github.com/helm/charts/blob/master/stable/gocd/values.yaml) present in the official GoCD helm chart repo.
- Add the .jar file link from the releases section to the `env.extraEnvVars` section as a new environment variable.
- The environment variable name must have `GOCD_PLUGIN_INSTALL` prefixed to it.
- Example

```
env:
  extraEnvVars:
    - name: GOCD_PLUGIN_INSTALL_gitternotifier
      value: https://github.com/gocd-contrib/gitter-notifier/releases/download/v2.1.0/gitter-notification-plugin-2.1.0.jar
```

- Make sure to add the link of the release you want to use.

- Then applying the local values.yaml that has these values added to it will result in a new Go Server pod being created that has the plugin installed and running.

[1]: images/list-plugin.png  "List Plugin"
[2]: images/configure-plugin.png  "Configure Plugin"
[3]: images/gitter-notification.png  "Successful Notification"

## Contributing
We encourage you to contribute to Go. For information on contributing to this project, please see our [contributor's guide](http://www.go.cd/contribute).
A lot of useful information like links to user documentation, design documentation, mailing lists etc. can be found in the [resources](http://www.go.cd/community/resources.html) section.

## License

```plain
Copyright 2015 ThoughtWorks, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```