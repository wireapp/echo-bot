FROM dejankovacevic/bots.runtime:2.10.0

COPY libs/libblender.so  /opt/wire/lib/libblender.so
COPY target/echo.jar     /opt/echo/echo.jar
COPY conf/echo.yaml      /etc/echo/echo.yaml

WORKDIR /opt/echo

