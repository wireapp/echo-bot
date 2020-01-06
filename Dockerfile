# FROM docker.io/maven AS build-env
# WORKDIR /app
# COPY pom.xml ./
# RUN mvn verify --fail-never
# COPY . ./
# RUN mvn -Dmaven.test.skip=true -Dmaven.javadoc.skip=true package
#

FROM dejankovacevic/bots.runtime:2.10.3

# COPY --from=build-env /app/target/echo.jar  /opt/echo/
COPY target/echo.jar                        /opt/echo/
COPY echo.yaml                              /opt/echo/
COPY libs/libblender.so                     /opt/wire/lib/

WORKDIR /opt/echo

EXPOSE  8080 8081 8082

ENTRYPOINT [ "java", "-jar", "echo.jar", "server", "echo.yaml" ]
