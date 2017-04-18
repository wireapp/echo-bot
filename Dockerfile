FROM wire/bots.runtime:latest

COPY target/echo.jar      /opt/echo/echo.jar
COPY certs/keystore.jks    /opt/echo/keystore.jks

WORKDIR /opt/echo
EXPOSE  8050

