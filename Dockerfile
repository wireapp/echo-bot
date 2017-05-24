FROM wire/bots.runtime:latest

COPY target/echo.jar /opt/echo/echo.jar

WORKDIR /opt/echo
EXPOSE  4443

