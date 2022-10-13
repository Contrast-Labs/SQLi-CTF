FROM adoptopenjdk/openjdk11:ubi
RUN mkdir /etc/contrast
RUN mkdir /opt/app/
RUN mkdir /.contrast/
COPY contrast/contrast.jar /etc/contrast/
COPY contrast/contrast_security.yaml /etc/contrast/java/
COPY contrast/contrast_security_with_protect.yaml /etc/contrast/java/
COPY target/sql-injection-ctf-0.0.1-SNAPSHOT.jar /opt/app/app.jar
COPY contrast/run.sh /opt/app/run.sh
RUN chmod +x  /opt/app/run.sh
RUN chown -R nobody:nobody /opt
RUN chown -R nobody:nobody /etc/contrast
RUN chown -R nobody:nobody /.contrast

USER nobody
CMD ["bash", "-c", "/opt/app/run.sh"]