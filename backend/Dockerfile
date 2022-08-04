FROM amazoncorretto:11-alpine AS build

WORKDIR /work
COPY . /work

RUN chmod +x gradlew
RUN ./gradlew --no-daemon :shadowJar

FROM amazoncorretto:11-alpine

COPY --from=build /work/build/libs/dex-*-all.jar /app/dex-all.jar

ENTRYPOINT ["java","-jar","/app/dex-all.jar"]
