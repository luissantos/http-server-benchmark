# http-server


## Usage

Build an uberjar:

    $ clojure -X:uberjar

Run api:

    $ java -server -jar http-server.jar

Run benchmark

    $ ab -n 50000 -c 100 http://localhost:8080/api/json
