#!/bin/bash
./mvnw clean install \
  -Didea.version=2025.3.3 \
  -Djansi.passthrough=true \
  -Dstyle.color=always \
  -s /Users/siron/.m2/settings.xml \
  -Dmaven.repo.local=/Users/siron/.m2/repository/java-17 \
  -f pom.xml