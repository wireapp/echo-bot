FROM dejankovacevic/bots.runtime:2.10.3

COPY libs/libblender.so  /opt/wire/lib/libblender.so
COPY target/echo.jar     /opt/echo/echo.jar
COPY echo.yaml           /etc/echo/echo.yaml

WORKDIR /opt/echo

EXPOSE  8080 8081 8082

CMD ["sh", "-c","/usr/bin/java -Djava.library.path=/opt/wire/lib -jar echo.jar server /etc/echo/echo.yaml"]