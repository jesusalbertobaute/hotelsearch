FROM maven:3.9.12-amazoncorretto-21-alpine AS build
COPY pom.xml /opt/app/pom.xml
COPY src /opt/app/src
RUN mvn -f /opt/app/pom.xml clean install -DskipTests
FROM amazoncorretto:21.0.10-alpine3.20
RUN addgroup -S backgroup && adduser -S backuser -G backgroup
RUN mkdir /app && chown backuser:backgroup /app
COPY --from=build /opt/app/target/hotel-search-app-1.0.0.jar /app/hotel-search-app-1.0.0.jar
USER backuser
WORKDIR /app
EXPOSE 8080
CMD ["java","-jar","hotel-search-app-1.0.0.jar"]