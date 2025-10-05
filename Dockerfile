FROM docker-dso.msb.com.vn/dso/openjdk:17-jdk-oracle

WORKDIR /opt/app

ARG JAR_FILE=target/*.jar

ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005

COPY ${JAR_FILE} dc-autotest-service.jar

ENTRYPOINT ["java","-jar","dc-autotest-service.jar"]