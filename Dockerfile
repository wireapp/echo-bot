FROM docker.io/maven AS build-env

WORKDIR /app

# download dependencies
COPY pom.xml ./
RUN mvn verify --fail-never -U

# build
COPY . ./
RUN mvn -Dmaven.test.skip=true package

# runtime stage
FROM dejankovacevic/bots.runtime:2.10.3

WORKDIR /opt/echo

# Copy libraries
COPY libs/libblender.so /opt/wire/lib/

# Copy configuration
COPY echo.yaml /opt/echo/

# Copy built target
COPY --from=build-env /app/target/echo.jar /opt/echo/

# create version file
ARG release_version=development
ENV RELEASE_FILE_PATH=/opt/echo/release.txt
RUN echo $release_version > $RELEASE_FILE_PATH

EXPOSE  8080 8081 8082
ENTRYPOINT ["java", "-jar", "echo.jar", "server", "echo.yaml"]
