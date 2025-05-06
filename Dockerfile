FROM quay.io/quarkus/quarkus-micro-image:2.0
WORKDIR /work/
COPY target/quarkus-app/lib/ /work/lib/
COPY target/quarkus-app/*.jar /work/
COPY target/quarkus-app/app/ /work/app/
COPY target/quarkus-app/quarkus/ /work/quarkus/
EXPOSE 8080
CMD ["java", "-jar", "/work/*-runner.jar"]
