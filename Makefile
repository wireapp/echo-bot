SHELL := /usr/bin/env bash
BOT   := hello
OS    := $(shell uname -s | tr '[:upper:]' '[:lower:]')

CERTS_DIR		    := certs
CSR_COUNTRY		    := DE
CSR_STATE		    := Berlin
CSR_LOCALITY		:= Berlin
CSR_ORGANISATION	:= My Company GmbH
CSR_COMMON		    := company.com
KEYSTORE_PASSWORD	:= 123456
KEYSTORE_FILE		:= keystore.jks

ifeq ($(OS), darwin)
PLATFORM := darwin
else
PLATFORM := linux
endif

default: all

.PHONY: all
all: generate_cert generate_keystore
	mvn -P$(PLATFORM) package

.PHONY: linux
linux: generate_cert generate_keystore
	mvn -Plinux package

.PHONY: darwin
darwin: generate_cert generate_keystore
	mvn -Pdarwin package

.PHONY: generate_cert
generate_cert: | $(CERTS_DIR)

$(CERTS_DIR):
	mkdir -p $(CERTS_DIR)
	openssl genrsa -out $(CERTS_DIR)/privkey.pem 4096
	openssl req -new -subj "/C=$(CSR_COUNTRY)/ST=$(CSR_STATE)/L=$(CSR_LOCALITY)/O=$(CSR_ORGANISATION)/CN=$(CSR_COMMON)" -key $(CERTS_DIR)/privkey.pem -out $(CERTS_DIR)/csr.pem
	openssl x509 -req -days 7300 -in $(CERTS_DIR)/csr.pem -signkey $(CERTS_DIR)/privkey.pem -out $(CERTS_DIR)/cert.pem
	openssl rsa -in $(CERTS_DIR)/privkey.pem -pubout -out $(CERTS_DIR)/pubkey.pem

.PHONY: generate_keystore
generate_keystore: | $(CERTS_DIR)/$(KEYSTORE_FILE)

$(CERTS_DIR)/$(KEYSTORE_FILE):
	openssl pkcs12 -export -name myservercert -in $(CERTS_DIR)/cert.pem -inkey $(CERTS_DIR)/privkey.pem -out $(CERTS_DIR)/keystore.p12 -passout pass:$(KEYSTORE_PASSWORD)
	keytool -importkeystore -noprompt -destkeystore $(CERTS_DIR)/$(KEYSTORE_FILE) -srckeystore $(CERTS_DIR)/keystore.p12 -srcstorepass $(KEYSTORE_PASSWORD) -storepass $(KEYSTORE_PASSWORD) -srcstoretype pkcs12 -alias myservercert

.PHONY: clean
clean:
	mvn clean
	rm -rf $(CERTS_DIR)

.PHONY: run
run:
	java -jar target/hello.jar server hello.yaml
