

kafka-topics.sh --zookeeper localhost:2181 --alter --topic $1 --config retention.ms=1000

wait

