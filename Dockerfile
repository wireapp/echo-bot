FROM wire/bots.runtime:latest

COPY target/hello.jar      /opt/echo/hello.jar
COPY hello.yaml            /opt/echo/hello.yaml
COPY certs/keystore.jks    /opt/echo/keystore.jks

WORKDIR /opt/echo
EXPOSE  8050
