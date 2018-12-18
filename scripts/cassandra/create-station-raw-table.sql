CREATE TABLE rtbike.station_raw_topic(
                year int,
                event_timestamp bigint,
                event_id varchar,
                source varchar,
                event_type varchar,
                readable_date varchar,
                event_raw varchar,
                offset bigint,
                PRIMARY KEY((year), event_timestamp, event_id))
                WITH CLUSTERING ORDER BY (event_timestamp DESC);
