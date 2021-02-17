#!/usr/bin/env bash

sed -e 's/<[\/]\?group[^>]*>//g' ./coverage/target/site/jacoco-aggregate/jacoco.xml > ./coverage/site/jacoco-aggregate/jacoco-cc.xml
JACOCO_SOURCE_PATH="commons-auth-adapter/src/main/java commons-auth-api/src/main/java commons-auth-core/src/main/java commons-auth-email-postmark/src/main/java commons-auth-email-smtp/src/main/java commons-auth-forms/src/main/java commons-auth-jpa/src/main/java commons-auth-jpa/target/generated-sources/annotations commons-auth-mongo/src/main/java commons-auth-server/src/main/java commons-auth-service/src/main/java commons-auth-test/src/main/java" ./cc-test-reporter format-coverage -t jacoco ./coverage/target/site/jacoco-aggregate/jacoco-cc.xml

./cc-test-reporter upload-coverage
