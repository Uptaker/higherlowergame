FROM node:20-alpine as build-ui
WORKDIR /ui

COPY ui/*.json ./
RUN npm ci

COPY ui ./

RUN npm run build
RUN npm run check

FROM openjdk:17-alpine as build-server
WORKDIR /app

COPY api/gradlew ./
COPY api/gradle gradle/

RUN ./gradlew --version

COPY api/*gradle* ./

COPY api/ ./
RUN ./gradlew build

# final image
FROM openjdk:17-alpine as final
RUN adduser -S user
RUN rm -fr /usr/bin /usr/sbin /bin/ch*

WORKDIR /app
COPY --from=build-ui /ui/build public
COPY --from=build-server /app/build/libs/higherlower.jar ./

USER user
ENV TZ=Europe/Tallinn
ENV JAVA_MEM="-Xss256K -Xmx350M"
CMD java $JAVA_MEM -jar higherlower.jar

ENV PORT=8080
EXPOSE $PORT