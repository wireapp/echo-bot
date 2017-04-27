FROM wire/bots.runtime:latest

COPY target/github.jar      /opt/github/github.jar
COPY certs/keystore.jks    /opt/github/keystore.jks

WORKDIR /opt/github

