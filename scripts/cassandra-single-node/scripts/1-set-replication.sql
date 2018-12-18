-- ALTER KEYSPACE "system_auth" WITH REPLICATION = {'class' : 'NetworkTopologyStrategy', 'dc1' : 3, 'dc2' : 2};
ALTER KEYSPACE "system_auth" WITH replication = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };
