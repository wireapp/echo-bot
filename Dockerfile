FROM wire/bots.runtime:latest

COPY target/echo.jar /opt/echo/echo.jar
COPY conf/echo.yaml  /etc/echo/echo.yaml

WORKDIR /opt/echo

