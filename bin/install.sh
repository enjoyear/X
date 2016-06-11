#!/usr/bin/env bash
source `dirname $0`/setEnv.sh
mvn clean package install -e -DskipTests=true
