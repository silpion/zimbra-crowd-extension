# Zimbra Crowd Authentication Extension

This Zimbra extensions implements a custom auth handler which authenticates
Zimbra accounts against an installation of Atlassian Crowd.

## Getting Started

### Prerequisites

This project is built with Maven.  It also depends on some Zimbra libraries
which aren't available via Maven Central.  The simples way to get hold of them
is by copying them from an existing Zimbra server.

```
rsync -rt -i zimbra.example.com:/opt/zimbra/lib/jars/ lib/  --include 'zimbra*.jar' --exclude '*.jar'
mvn package
```

## Installation

Maven will create a bundle `target/zimbra-crowd-extension.zip` which has to be
deployed on the Zimbra server.

```
mkdir -p /opt/zimbra/lib/ext/crowd/
unzip zimbra-crowd-extension.zip
rsync -rt -i zimbra-crowd-extension/ /opt/zimbra/lib/ext/crowd/
sudo -i -u zimbra zmmailboxdctl restart
```

## Configuration

TODO

## License

This project is currently *not* under an Open Source license.
