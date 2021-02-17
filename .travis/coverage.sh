#!/usr/bin/env bash

sed -e 's/<[\/]\?group[^>]*>//g' coverage/target/site/jacoco-aggregate/jacoco.xml > coverage/target/site/jacoco-aggregate/jacoco-cc.xml

export JACOCO_SOURCE_PATH=$(find . -type d -regex '.*main/java' | xargs echo)
export JACOCO_SOURCE_PATH="$JACOCO_SOURCE_PATH $(find . -type d -regex '.*target/generated-sources/annotations' | xargs echo)"

./cc-test-reporter format-coverage -t jacoco ./coverage/target/site/jacoco-aggregate/jacoco-cc.xml

./cc-test-reporter upload-coverage
