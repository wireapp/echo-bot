SHELL := /usr/bin/env bash
BOT   := echo
OS    := $(shell uname -s | tr '[:upper:]' '[:lower:]')

ifeq ($(OS), darwin)
PLATFORM := darwin
else
PLATFORM := linux
endif

default: all

.PHONY: all
all: mvn -P$(PLATFORM) package

.PHONY: linux
linux: mvn -Plinux package

.PHONY: darwin
darwin: mvn -Pdarwin package
	
PHONY: windows
windows: generate_cert generate_keystore
	mvn -Pwindows package

.PHONY: clean
clean:
	mvn clean

.PHONY: run
run:
	java -jar target/$(BOT).jar server conf/$(BOT).yaml
