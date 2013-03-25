elasticsearch-newrelic
======================

This plugin export node stats (indices, pool, network, http) to a newrelic account. The plugin works at a node level, new relic
will act as an agreggator for metrics of each node inside your cluster.

The plugin does not collect jvm metrics, as those are already collected by the newrelic agent by default.

The plugin has a default of collecting all metrics every 10 seconds (please note that newrelic  transmit those values
at a different rate, usually 60 seconds)

There's an endpoint where you can turn on/off collection per stats as well change the interval (in seconds, minimum: 1s)

Installation:
------------

The plugin can be built using [gradle] (http://www.gradle.org/) using the **jar** task.

from your $ES_HOME run: bin/plugin -url file://${jar_location} -install newrelic

You will also need a [new relic] (http://www.newrelic.com) agent. Please refer to their documentation on how to get your
agent along with your yml config file.

Assuming you will deploy the newrelic directory inside your ElasticSearch home folder, add this line to your **bin/elasticsearch.in.sh**

JAVA_OPTS="$JAVA_OPTS -javaagent:/$ES_HOME/newrelic/newrelic.jar"

Configuration:
-------------

There's an endpoint _newrelic that will return the configuration using GET:

    curl -XGET http://localhost:9200/_newrelic?pretty
    {
     "configuration" : {
     "indices" : true,
     "refreshInterval" : 10,
     "pool" : true,
     "http" : true,
     "network" : true
    }
    
You can also set each parameter individually by issuing a POST:

    curl -XPOST "http://localhost:9200/_newrelic?http=false&pool=false&refreshInterval=5"



