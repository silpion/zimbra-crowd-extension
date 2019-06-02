# Zimbra Crowd Authentication Extension

This Zimbra extensions implements a custom auth handler which authenticates
Zimbra accounts against an installation of Atlassian Crowd.


## Getting Started

### Prerequisites

This project depends on some Zimbra libraries which have to be placed in
the `lib` directory since they aren't available via Maven Central.

Just execute the script `lib.sh` to pull and extract them from the Zimbra
repositories.  Alternatively you can copy them from an existing Zimbra
server:

```
rsync -rt -i --delete zimbra.example.com:/opt/zimbra/lib/jars/ lib/  --include 'zimbra*.jar' --exclude '*.jar'
```

### Compilation

This project is built with Maven.  Just execute the following command:

```
mvn package
```

This will create a bundle `target/zimbra-crowd-extension.zip` which contains
the extension plus all the required libraries.


## Installation

The extension bundled in the file `target/zimbra-crowd-extension.zip` has to
be deployed on the Zimbra server.  In a multi-server environment it has to
be deployed on all instances.

To deploy the extension, copy the file to the server(s) and execture the
following commands:

```
install -d /opt/zimbra/lib/ext/crowd/
unzip zimbra-crowd-extension.zip
rsync -rt -i --delete zimbra-crowd-extension/ /opt/zimbra/lib/ext/crowd/
sudo -i -u zimbra zmmailboxdctl restart
```


## Configuration

To use the extension the following information is required:

* The Crowd URL (eg. `https://crowd.example.net:8443/crowd/`)
* The Crowd Application Name (eg. `zimbra`)
* The Crowd Application Password (eg `changeme`)

These values have to be set in `/opt/zimbra/conf/localconfig.xml` (on all
mailbox nodes).  Execute the following commands as user `zimbra`:

```
zmlocalconfig -e crowd_server_url=https://crowd.example.net:8443/crowd/
zmlocalconfig -e crowd_application_name=zimbra
zmlocalconfig -e crowd_application_password=changeme
zmmailboxdctl restart
```

To use authentication against Crowd it has to be enabled once per domain:

```
zmprov modifyDomain example.com zimbraAuthMech custom:crowd
zmprov modifyDomain example.com zimbraPasswordChangeListener crowd
zmprov flushCache -a domain
```

The authenticator will now try to authenticate against the given Crowd setup.
Per default accounts are mapped by email address, ie. for each login a search
against the Crowd directory is performed.  This can result in more than one
account which will cause an error.  Due to this it is recommended to set the
Crowd username to authenticate against explicitly for each Zimbra account:

```
zmprov modifyAccount john.doe@example.com +zimbraForeignPrincipal crowd:jdoe
```


## License

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may find a copy of the License in the file LICENSE or at

https://opensource.org/licenses/Apache-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.


## Acknowledgement

This extension was created by the joint effort of [Silpion](https://www.silpion.de/),
a Zimbra Gold Partner, and [iVentureGroup](https://www.iventuregroup.com/).
We are both located in Hamburg with subsidaries all over Germany.  If you
like to code on varying projects in a friendly atmosphere or have a knack for
running software at large scale, check out the job offerings on our websites.
