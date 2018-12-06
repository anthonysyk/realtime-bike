# Using an Alpine Linux based JDK image
FROM anapsix/alpine-java:8u181b13_jdk

COPY . /home/processor

# Using a non-privileged user:
WORKDIR /home/processor

ENTRYPOINT ["sh", "./bin/run-processor"]

EXPOSE 9001
