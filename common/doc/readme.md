# Content

## Cassandra

### Install and Setup
download pre-built version

make the configuration in the .bashrc file

```bash
    export CASSANDRA_HOME="path/to/your/package"
    export CQLSH_NO_BUNDLED=true #fix cqlsh bug. Check https://issues.apache.org/jira/browse/CASSANDRA-12402
    export PATH=$PATH:$CASSANDRA_HOME/bin
```

### Start & Stop

* foreground
    * start: cassandra -f
    * stop: pgrep -u `whoami` -f cassandra | xargs kill -9
* background
    * start: cassandra -p
    


