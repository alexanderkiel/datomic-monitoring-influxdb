(defproject datomic-monitoring-influxdb "0.2-SNAPSHOT"
  :description "Custom monitoring handler for Datomic transactors and peers which writes metrics into InfluxDB."
  :url "https://github.com/alexanderkiel/datomic-monitoring-influxdb"

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/data.json "0.2.6"]
                 [http-kit "2.1.18"]])
