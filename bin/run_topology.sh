#!/usr/bin/env bash
set -e
source `dirname $0`/setEnv.sh

corePath=$(cd `dirname $0` && cd ../core && pwd)

/usr/local/apache-storm-1.0.1/bin/storm jar ${corePath}/target/X-core-*-jar-with-dependencies.jar \
chen.guo.X.Main
