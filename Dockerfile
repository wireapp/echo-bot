FROM wire/wbots.runtime:latest

COPY target/hello.jar /opt/hello/hello.jar
COPY hello.yaml /opt/hello/hello.yaml
COPY certs/keystore.jks /opt/hello/keystore.jks

WORKDIR /opt/hello

EXPOSE 8050

RUN mkdir /opt/hello/crypto

ENTRYPOINT [ \
    "/usr/bin/java", \
    "-jar", \
    "hello.jar", \
    "server", \
    "hello.yaml" \
  ]
