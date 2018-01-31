SHELL := /usr/bin/env bash
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
windows: mvn -Pwindows package

.PHONY: clean
clean:
	mvn clean

.PHONY: run
run:
	java -jar target/echo.jar server conf/echo.yaml
