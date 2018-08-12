# Using an Alpine Linux based JDK image
FROM anapsix/alpine-java:8u131b11_jdk

COPY . /home/collector

# Using a non-privileged user:
WORKDIR /home/collector

ENTRYPOINT ["sh", "./bin/run-collector"]
