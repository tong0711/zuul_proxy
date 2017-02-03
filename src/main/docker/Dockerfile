FROM frolvlad/alpine-oraclejdk8:slim
VOLUME /tmp
ADD zuul_proxy-0.0.1-SNAPSHOT.jar app.jar
RUN sh -c 'touch /app.jar'
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "PRIMARY_IP=`ifconfig -a|awk '/(cast)/ {print $2}'|cut -d':' -f2|head -1`;  export HOST_IP=$PRIMARY_IP;  java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]