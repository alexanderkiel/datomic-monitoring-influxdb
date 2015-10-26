(ns datomic-monitoring-influxdb.core
  (:require [org.httpkit.client :as http]
            [clojure.data.json :as json]
            [clojure.string :as str])
  (:import [java.util Date]
           [java.net InetAddress]))

(defn post-metrics [host port auth db metrics]
  (-> (str "http://" host ":" port "/db/" db "/series")
      (http/post {:basic-auth auth
                  :headers {"Content-Type" "application/json"}
                  :body (json/write-str metrics)})
      (deref)
      (:status)))

(defn build-metric [name time value]
  {:name name
   :columns ["time", "value"]
   :points [[time value]]})

(defn avg [metric]
  (/ (double (:sum metric)) (:count metric)))

(def lo-hi-avg-count [:lo :lo :hi :hi :avg avg :count :count])

(def available-mb
  {:key :AvailableMB
   :metrics :simple-value})

(def memory-index-mb
  {:key :MemoryIndexMB
   :metrics [:hi :hi]})

(def object-cache
  {:key :ObjectCache
   :metrics [:hit-ratio avg]})

(def transaction-datoms
  {:key :TransactionDatoms
   :metrics lo-hi-avg-count})

(def storage-get-bytes
  {:key :StorageGetBytes
   :metrics lo-hi-avg-count})

(def storage-get-msec
  {:key :StorageGetMsec
   :metrics lo-hi-avg-count})

(def storage-put-bytes
  {:key :StoragePutBytes
   :metrics lo-hi-avg-count})

(def storage-put-msec
  {:key :StoragePutMsec
   :metrics lo-hi-avg-count})

(def transaction-bytes
  {:key :TransactionBytes
   :metrics lo-hi-avg-count})

(def transaction-msec
  {:key :TransactionMsec
   :metrics lo-hi-avg-count})

(def index-write-msec
  {:key :IndexWriteMsec
   :metrics lo-hi-avg-count})

(def index-writes
  {:key :IndexWrites
   :metrics [:hi :hi]})

(def create-entire-index-msec
  {:key :CreateEntireIndexMsec
   :metrics [:hi :hi]})

(def create-fulltext-index-msec
  {:key :CreateFulltextIndexMsec
   :metrics [:hi :hi]})

(def index-datoms
  {:key :IndexDatoms
   :metrics [:hi :hi]})

(def datoms
  {:key :Datoms
   :metrics [:hi :hi]})

(def memory-index-fill-msec
  {:key :MemoryIndexFillMsec
   :metrics lo-hi-avg-count})

(def log-write-msec
  {:key :LogWriteMsec
   :metrics lo-hi-avg-count})

(def pod-get-msec
  {:key :PodGetMsec
   :metrics lo-hi-avg-count})

(def pod-update-msec
  {:key :PodUpdateMsec
   :metrics lo-hi-avg-count})

(defn de-camelcase [s]
  (->> s
       (partition-by #(Character/isLowerCase %))
       (partition-all 2)
       (map flatten)
       (map #(apply str %))
       (map str/lower-case)
       (str/join "-")))

(defn translate-sub-metric [prefix value sub-metric]
  {:name (str prefix "." (name (first sub-metric)))
   :value ((second sub-metric) value)})

(defn translate-metric [metrics {:keys [key] :as metric-def}]
  (when-let [value (key metrics)]
    (let [name (de-camelcase (name key))]
      (condp = (:metrics metric-def)
        :simple-value
        {:name name
         :value value}

        (map (partial translate-sub-metric name value)
             (partition 2 (:metrics metric-def)))))))

(defn translate-metrics [metrics metric-defs]
  (->> metric-defs
       (map (partial translate-metric metrics))
       (flatten)
       (filter identity)))

(defn handler [metrics]
  (let [time (.getTime (Date.))
        host (.getHostName (InetAddress/getLocalHost))]
    (->> [available-mb memory-index-mb object-cache
          transaction-datoms transaction-bytes transaction-msec
          storage-get-bytes storage-get-msec
          storage-put-bytes storage-put-msec
          pod-get-msec pod-update-msec
          index-write-msec index-writes
          create-entire-index-msec create-fulltext-index-msec
          index-datoms datoms
          memory-index-fill-msec log-write-msec]
         (translate-metrics metrics)
         (map #(build-metric (str host ".datomic." (:name %)) time (:value %)))
         (post-metrics "graphite" 8086 ["root" "root"] "graphite"))))
