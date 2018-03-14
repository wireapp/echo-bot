FROM dejankovacevic/bots.runtime:latest

COPY target/echo.jar     /opt/echo/echo.jar
COPY libs/libblender.so  /opt/echo/libblender.so
COPY conf/echo.yaml      /etc/echo/echo.yaml

WORKDIR /opt/echo

