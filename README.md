# Datomic Monitoring Influxdb

Custom monitoring handler for Datomic transactors and peers which writes metrics into InfluxDB.

## Usage

Add the `datomic-monitoring-influxdb-0.1-standalone.jar` to the Datomic library folder.

Add the following to your `transactor.properties`:

```
metrics-callback=datomic-monitoring-influxdb.core/handler
```

## License

Copyright © 2015 Alexander Kiel

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
