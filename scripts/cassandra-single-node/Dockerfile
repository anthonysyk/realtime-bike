FROM cassandra:3.11

# Workaround for https://github.com/docker/docker/issues/6345
RUN ln -s -f /bin/true /usr/bin/chfn

# Disable vnodes and gossip to bootstrap faster
COPY scripts/ /home/setup/

RUN sh /home/setup/config-cassandra-single-node.sh

EXPOSE 7000 7001 7199 9042 9160
