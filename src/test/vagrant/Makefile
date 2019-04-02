SHELL=/bin/bash

VERSION=$(shell cat VERSION)
TGZFILE=$(basename $(shell ls -1 zcs-*.tgz.url | grep -F zcs-$(VERSION)_GA))
INSFILE=$(basename $(TGZFILE))/install.sh

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


install: /opt/zimbra/conf/localconfig.done

/opt/zimbra/conf/localconfig.done: /opt/zimbra/.install_history $(INSFILE).config
	/opt/zimbra/libexec/zmsetup.pl -c $(INSFILE).config
	touch $@

/opt/zimbra/.install_history: $(INSFILE) $(INSFILE).config
	cd $(dir $(INSFILE)) && ./$(notdir $(INSFILE)) -s $(notdir $(INSFILE)).config

$(INSFILE).config: $(TGZFILE).config
	cp -a $< $@
	


clean:
	rm -f $(TGZFILE) $(TGZFILE).ok $(TGZFILE).tmp
