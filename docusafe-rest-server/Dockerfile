FROM adorsys/openjdk-jre-base:8-minideb
ENV JAVA_OPTS -Xmx512m
WORKDIR /home/docusafe
COPY target/docusafe-rest-server.jar .
EXPOSE 9999
CMD exec $JAVA_HOME/bin/java $JAVA_OPTS -jar /home/docusafe/docusafe-rest-server.jar
