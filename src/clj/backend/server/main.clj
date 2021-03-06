(ns backend.server.main
  (:require [backend.server.system :as system]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [com.stuartsierra.component :as component]
            [environ.core :refer [env]]
            [taoensso.timbre :as log])
  (:gen-class :main true))

(def api-url "http://api.football-data.org/v1")
(def config {:backend/id "backend-server"
             :backend/log-path "/tmp"
             :backend/secret-key "secret"
             :backend/user-manager-type :atomic
             :backend/football-repo-type :http
             :backend/football-api-url api-url
             :backend/users {"mike" "rocket"}})

(defn -main
  [& [port]]
  (log/set-level! :debug)
  (let [port (Integer. (or port (env :port) 5000))
        api-token (str/trim (slurp (io/resource "token.txt")))]
    (log/info (str "Using port " port "."))
    (let [system (system/system (merge config {:backend/port port
                                               :backend/football-api-token api-token}))]
      (log/info "Starting system.")
      (component/start-system system)
      (log/info "Waiting forever.")
      @(promise))))
