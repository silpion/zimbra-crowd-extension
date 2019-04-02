SHELL=/bin/bash

VERSION=$(shell cat VERSION)
TGZFILE=$(basename $(shell ls -1 atlassian-crowd-*.tgz.url | grep -F atlassian-crowd-$(VERSION).))
INSFILE=$(basename $(TGZFILE))/crowd-webapp/WEB-INF/classes/crowd-init.properties

all: $(INSFILE)


$(INSFILE): $(TGZFILE) $(TGZFILE).ok
	tar xfz $<
	touch $@

$(TGZFILE).ok: $(TGZFILE).sha256 $(TGZFILE)
	sha256sum -c $<
	touch $@

$(TGZFILE):
	xargs -n 1 curl -s -L -o $@.tmp < $(TGZFILE).url
	mv $@.tmp $@


install: /opt/crowd/crowd-webapp/WEB-INF/classes/crowd-init.properties

/opt/crowd/crowd-webapp/WEB-INF/classes/crowd-init.properties: $(INSFILE)
	rsync --archive --delete $(basename $(TGZFILE))/ /opt/crowd/
	mkdir -p /srv/crowd
	echo "crowd.home=/srv/crowd" > $@


clean:
	rm -f $(TGZFILE) $(TGZFILE).ok $(TGZFILE).tmp
