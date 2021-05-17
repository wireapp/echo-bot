FROM maven:3-openjdk-11 AS build
LABEL description="Wire Echo Bot"
LABEL project="wire-bots:echo-bot"

WORKDIR /app

# download dependencies
COPY pom.xml ./
RUN mvn verify --fail-never -U

# build
COPY . ./
RUN mvn -Dmaven.test.skip=true package

# runtime stage
FROM wirebot/runtime

WORKDIR /opt/echo

# Copy libraries
COPY libs/libblender.so /opt/wire/lib/

# Copy configuration
COPY echo.yaml /opt/echo/

# Copy built target
COPY --from=build /app/target/echo.jar /opt/echo/

# create version file
ARG release_version=development
ENV RELEASE_FILE_PATH=/opt/echo/release.txt
RUN echo $release_version > $RELEASE_FILE_PATH

EXPOSE  8080 8081 8082

ENTRYPOINT ["java", "-javaagent:/opt/wire/lib/prometheus-agent.jar=8082:/opt/wire/lib/metrics.yaml", "-jar", "echo.jar", "server", "echo.yaml"]

