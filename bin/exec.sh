#!/usr/bin/env bash
source `dirname $0`/setEnv.sh
mvn exec:java -pl core
