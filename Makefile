SHELL := /usr/bin/env bash
BOT   := hello

CERTS_DIR		:= certs
CSR_COUNTRY		:= CH
CSR_STATE		:= Zug
CSR_LOCALITY		:= Zug
CSR_ORGANISATION	:= Wire Swiss GmbH
CSR_COMMON		:= wire.com
KEYSTORE_PASSWORD	:= 123456
KEYSTORE_FILE		:= keystore.jks

default: all

.PHONY: all
all: compile generate_cert generate_keystore container

.PHONY: compile
compile:
	mvn -Plinux package

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
	openssl pkcs12 -export -name myservercert -in $(CERTS_DIR)/cert.pem -inkey $(CERTS_DIR)/privkey.pem -out $(CERTS_DIR)/keystore.p12
	keytool -noprompt -importkeystore -destkeystore $(CERTS_DIR)/$(KEYSTORE_FILE) -srckeystore $(CERTS_DIR)/keystore.p12 -storepass $(KEYSTORE_PASSWORD) -srcstoretype pkcs12 -alias myservercert
	
.PHONY: container
container: $(addprefix container-, $(BOT))

container-%:
	docker build --tag wire/$* -f Dockerfile .

.PHONY: clean
clean:
	mvn clean
	rm -rf $(CERTS_DIR)
