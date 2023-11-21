FROM gradle:8.4.0-jdk17-alpine AS builder

WORKDIR /builder

COPY settings.gradle.kts .
COPY build.gradle.kts .

RUN gradle --no-daemon --console=plain dependencies --configuration runtimeClasspath

COPY src src

RUN gradle --no-daemon --console=plain bootJar -x test && \
    mkdir /layers && \
    java -Djarmode=layertools -jar build/libs/*.jar extract --destination /layers && \
    rm -rf /builder/.gradle

FROM eclipse-temurin:17-jre-alpine

RUN addgroup -S spring && adduser -S spring -G spring

USER spring:spring

WORKDIR /app

COPY --from=builder /layers/dependencies/ ./
COPY --from=builder /layers/snapshot-dependencies/ ./
COPY --from=builder /layers/spring-boot-loader/ ./
COPY --from=builder /layers/application/ ./

EXPOSE 8080

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]