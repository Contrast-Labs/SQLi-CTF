#!/bin/bash

if [ "${PROTECT_ENABLED}" == "yes" ]; then
     java -javaagent:/etc/contrast/contrast.jar  -Dcontrast.config.path=/etc/contrast/java/contrast_security_with_protect.yaml -jar /opt/app/app.jar
  else
     java -javaagent:/etc/contrast/contrast.jar  -Dcontrast.config.path=/etc/contrast/java/contrast_security.yaml -jar /opt/app/app.jar
  fi