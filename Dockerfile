FROM dejankovacevic/bots.runtime:2.10.2

COPY libs/libblender.so  /opt/wire/lib/libblender.so
COPY target/echo.jar     /opt/echo/echo.jar
COPY echo.yaml           /etc/echo/echo.yaml

WORKDIR /opt/echo

EXPOSE  8080 8081 8082
