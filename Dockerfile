FROM openjdk:8
MAINTAINER Oitejjho
EXPOSE 9090
ADD /target/pal-0.0.1-SNAPSHOT.jar pal-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "pal-0.0.1-SNAPSHOT.jar"]