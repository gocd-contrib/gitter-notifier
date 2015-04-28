# GoCD build status notifier
This is GoCD's Notification plugin that updates build status (failed builds only) on Gitter.

## Requirements
This needs GoCD >= v15.x which is due release as of writing.

## Setup
- Download the latest plugin jar from [Releases](https://github.com/srinivasupadhya/gitter-notifier/releases) section. Place it in `<go-server-location>/plugins/external` & restart Go Server.
- You should provide `token`, `roomId` & `Go server url` through system property.
Eg:
```
-Dgo.plugin.build.status.gitter.token=thisainttoken
-Dgo.plugin.build.status.gitter.roomId=553f214e15522ed4b3df9cd4
-Dgo.plugin.build.status.go-server=build.go.cd
```