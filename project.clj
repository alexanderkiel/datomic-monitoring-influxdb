(defproject datomic-monitoring-influxdb "0.1-SNAPSHOT"
  :description "Custom monitoring handler for Datomic transactors and peers which writes metrics into InfluxDB."
  :url "http://git.life.uni-leipzig.local/akiel/datomic-monitoring-influxdb"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/data.json "0.2.6"]
                 [http-kit "2.1.16"]])
