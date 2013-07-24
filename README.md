elasticsearch-newrelic
======================

| Elasticsearch version | branch |
| ---------------- | ----------- |
| 0.90.0 -> 0.90.2 | master |
| 0.20.1 -> 0.20.5 | 0.20-version |

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
        "agents" : {
          "http" : true,
          "pool" : true,
          "transport" : true,
          "fs" : true,
          "indices" : true,
          "network" : true
        },
        "refreshInterval" : 10
       }
      }
    
You can also set each parameter individually by issuing a POST:

    curl -XPOST "http://localhost:9200/_newrelic?http=false&pool=false&refreshInterval=5"
    
Or set all to be on/off by issuing:

    curl -XPOST "http://localhost:9200/_newrelic?all=false"


Metrics:
--------

The plugin collects six main categories: indices, pool, network, transport, http and filesystem. The metrics are created
using newrelic hierarchical approach, so for example:

indices/search/total --> Total number of searches executed

This way, once you are on your custom dashboard, you can benefit from the auto complete feature by just typing the name
of the main category followed by a / : 

![Metrics screen](/../../../raw/master/site/metric_selection.png)

Screenshots:
-----------
![Sample dashboard](/../../../raw/master/site/es_dashboard.png)
