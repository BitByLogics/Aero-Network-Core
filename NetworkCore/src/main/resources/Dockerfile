FROM alpine:3.5
ARG SERVER_ROOT=aero/

RUN apk add --update openjdk8-jre-base
RUN mkdir $SERVER_ROOT
ADD resources/spigot.jar $SERVER_ROOT
ADD resources/server.properties $SERVER_ROOT

WORKDIR $SERVER_ROOT
RUN mkdir plugins/
ADD resources/AeroCore.jar plugins/

CMD java -Xms512m -Xmx512m -Dcom.mojang.eula.agree=true -jar spigot.jar

EXPOSE 25565