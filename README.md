<h1 align="center"> Machine Metrics Service API </h1> <br>

<p align="center">
  This service will communicate with internal microservices
</p>


## Table of Contents

- [Introduction](#introduction)
- [Features](#features)
- [Requirements](#requirements)
- [Test](#test)
- [Improvements](#improvements)




## Introduction

This service is responsible for exposing metrics of machines and save different parameters for machines

## Features
* Microservice architecture
* Validate request from upstream services
* Save machines and parameters in database
* Return response to other upstream services

## Requirements
The application can be run locally.

### Local
* [Java 16 SDK](https://www.oracle.com/java/technologies/downloads/#java16)
* [Maven](https://downloads.apache.org/maven/maven-3/3.8.1/binaries/)

### Run Local
If your JAVA_HOME is set to Java16 JDK
```bash
$ mvn clean install
$ java -jar target/subscription-service-0.0.1-SNAPSHOT.jar
```

For multiple JDK issue
```bash
$ JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-16.0.2.jdk/Contents/Home
$ export JAVA_HOME
$ mvn clean install
$ java -jar target/pal-0.0.1-SNAPSHOT.jar
```

Application will run by default on port `9090`

Configure the port by changing `server.port` in __application.yml__

## Test
100% UnitTest code coverage for business and controller layers using Junit Jupiter is used.


## Improvements
* API documentation using swagger-ui and open-api docs
* Integration with CICD i.e. jenkins / rancher
* Metrics Expose
* Integration with jaeger / slueth / opentelemetry for better traceability
* Integration with metrics collector i.e. prometheus
* Integration with ELK stack
* Integration with grafana for better visibility, observability and alerts
* Automation testing or behavioral testing i.e. RobotFramework, Selenium 
